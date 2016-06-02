/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldNames;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.ParametricSort;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.parametricvalues.AdaptiveBucketSizeEvaluatorFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketSizeEvaluator;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HodParametricValuesService implements ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> {
    private final FieldsService<HodFieldsRequest, HodErrorException> fieldsService;
    private final GetParametricValuesService getParametricValuesService;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;
    private final AdaptiveBucketSizeEvaluatorFactory bucketSizeEvaluatorFactory;

    public HodParametricValuesService(
            final FieldsService<HodFieldsRequest, HodErrorException> fieldsService,
            final GetParametricValuesService getParametricValuesService,
            final ConfigService<? extends HodSearchCapable> configService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever,
            final AdaptiveBucketSizeEvaluatorFactory bucketSizeEvaluatorFactory) {
        this.fieldsService = fieldsService;
        this.getParametricValuesService = getParametricValuesService;
        this.configService = configService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
        this.bucketSizeEvaluatorFactory = bucketSizeEvaluatorFactory;
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_VALUES)
    public Set<QueryTagInfo> getAllParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        final Collection<String> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(fieldsService.getFields(new HodFieldsRequest.Builder().setDatabases(parametricRequest.getQueryRestrictions().getDatabases()).build(), FieldTypeParam.Parametric).get(FieldTypeParam.Parametric));
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final FieldNames parametricFieldNames = getParametricValues(parametricRequest, fieldNames);
            final Set<String> fieldNamesSet = parametricFieldNames.getFieldNames();

            results = new HashSet<>();
            for (final String name : fieldNamesSet) {
                final Set<QueryTagCountInfo> values = new HashSet<>(parametricFieldNames.getValuesAndCountsForFieldName(name));
                if (!values.isEmpty()) {
                    results.add(new QueryTagInfo(name, name, values));
                }
            }
        }

        return results;
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_VALUES_IN_BUCKETS)
    public List<RangeInfo> getNumericParametricValuesInBuckets(final HodParametricRequest parametricRequest, final int targetNumberOfBuckets) throws HodErrorException {
        //TODO use the same method as IDOL for bucketing, once HOD-2784 and HOD-2785 are complete
        final Set<QueryTagInfo> numericFieldInfo = getNumericParametricValues(parametricRequest);

        final List<RangeInfo> ranges = new ArrayList<>(numericFieldInfo.size());
        for (final QueryTagInfo queryTagInfo : numericFieldInfo) {
            getNumericParametricValuesInBucketsForField(targetNumberOfBuckets, ranges, queryTagInfo);
        }

        return ranges;
    }

    private void getNumericParametricValuesInBucketsForField(final int targetNumberOfBuckets, final Collection<RangeInfo> ranges, final QueryTagInfo queryTagInfo) {
        final Set<QueryTagCountInfo> numericFieldValuesWithCount = queryTagInfo.getValues();
        final double maxContinuousValue = Double.parseDouble(numericFieldValuesWithCount.toArray(new QueryTagCountInfo[numericFieldValuesWithCount.size()])[numericFieldValuesWithCount.size() - 1].getValue());
        final double minContinuousValue = Double.parseDouble(numericFieldValuesWithCount.iterator().next().getValue());

        final BucketSizeEvaluator bucketSizeEvaluator = bucketSizeEvaluatorFactory.getBucketSizeEvaluator(maxContinuousValue, minContinuousValue, targetNumberOfBuckets);
        final double maxValue = bucketSizeEvaluator.adjustMax(maxContinuousValue);
        final double minValue = bucketSizeEvaluator.adjustMin(minContinuousValue);
        final double bucketSize = bucketSizeEvaluator.getBucketSize();

        final List<RangeInfo.Value> buckets = new ArrayList<>(targetNumberOfBuckets);
        final Iterator<QueryTagCountInfo> iterator = numericFieldValuesWithCount.iterator();

        QueryTagCountInfo valueAndCount = null;
        double boundaryValue = minValue;
        int totalCount = 0;
        for (int i = -1; i < targetNumberOfBuckets && (valueAndCount == null || Double.parseDouble(valueAndCount.getValue()) < maxContinuousValue); i++) {
            while (iterator.hasNext() && Double.parseDouble((valueAndCount = iterator.next()).getValue()) < boundaryValue) {
                final int count = valueAndCount.getCount();
                totalCount += count;
                buckets.get(i).addData(count);
            }

            while (valueAndCount != null && Double.parseDouble(valueAndCount.getValue()) >= boundaryValue + bucketSize) {
                buckets.add(new RangeInfo.Value(0, boundaryValue, boundaryValue += bucketSize));
            }

            if (valueAndCount != null) {
                final double value = Double.parseDouble(valueAndCount.getValue());
                if (iterator.hasNext() || value >= boundaryValue) {
                    final int count = valueAndCount.getCount();
                    totalCount += count;
                    buckets.add(new RangeInfo.Value(count, boundaryValue, Math.min(boundaryValue += bucketSize, maxValue)));
                }
            }
        }

        ranges.add(new RangeInfo(queryTagInfo.getName(), queryTagInfo.getName(), totalCount, minValue, maxValue, bucketSize, buckets));
    }

    private Set<QueryTagInfo> getNumericParametricValues(final ParametricRequest<ResourceIdentifier> parametricRequest) throws HodErrorException {
        final Collection<String> fieldNames = parametricRequest.getFieldNames();

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final FieldNames parametricFieldNames = getParametricValues(parametricRequest, fieldNames);
            final Set<String> fieldNamesSet = parametricFieldNames.getFieldNames();

            results = new LinkedHashSet<>();
            for (final String name : fieldNamesSet) {
                final Set<QueryTagCountInfo> values = new LinkedHashSet<>(parametricFieldNames.getValuesAndCountsForNumericField(name));
                if (!values.isEmpty()) {
                    results.add(new QueryTagInfo(name, name, values));
                }
            }
        }

        return results;
    }

    @Override
    public List<RecursiveField> getDependentParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        throw new NotImplementedException("Dependent parametric values not yet implemented for hod");
    }

    private FieldNames getParametricValues(final ParametricRequest<ResourceIdentifier> parametricRequest, final Collection<String> fieldNames) throws HodErrorException {
        final ResourceIdentifier queryProfile = parametricRequest.isModified() ? getQueryProfile() : null;

        final GetParametricValuesRequestBuilder parametricParams = new GetParametricValuesRequestBuilder()
                .setQueryProfile(queryProfile)
                .setSort(ParametricSort.fromParam(parametricRequest.getSort()))
                .setText(parametricRequest.getQueryRestrictions().getQueryText())
                .setFieldText(parametricRequest.getQueryRestrictions().getFieldText())
                .setMaxValues(parametricRequest.getMaxValues())
                .setMinScore(parametricRequest.getQueryRestrictions().getMinScore())
                .setSecurityInfo(authenticationInformationRetriever.getPrincipal().getSecurityInfo());

        return getParametricValuesService.getParametricValues(fieldNames,
                new ArrayList<>(parametricRequest.getQueryRestrictions().getDatabases()), parametricParams);
    }

    private ResourceIdentifier getQueryProfile() {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();
        final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
        return new ResourceIdentifier(domain, profileName);
    }

}

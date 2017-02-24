/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.aci.content.ranges.Range;
import com.hp.autonomy.aci.content.ranges.Ranges;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.hod.caching.CachingConfiguration;
import com.hp.autonomy.hod.client.api.resource.ResourceName;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldRanges;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.FieldValues;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricRangesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricRangesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesRequestBuilder;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.GetParametricValuesService;
import com.hp.autonomy.hod.client.api.textindex.query.parametric.ParametricSort;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper;
import com.hp.autonomy.searchcomponents.core.parametricvalues.DependentParametricField;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_VALUES_SERVICE_BEAN_NAME;

/**
 * Default HoD implementation of {@link ParametricValuesService}
 */
@Service(PARAMETRIC_VALUES_SERVICE_BEAN_NAME)
class HodParametricValuesServiceImpl implements HodParametricValuesService {
    private final HodFieldsService fieldsService;
    private final ObjectFactory<HodFieldsRequestBuilder> fieldsRequestBuilderFactory;
    private final GetParametricValuesService getParametricValuesService;
    private final GetParametricRangesService getParametricRangesService;
    private final BucketingParamsHelper bucketingParamsHelper;
    private final TagNameFactory tagNameFactory;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    @Autowired
    HodParametricValuesServiceImpl(
            final HodFieldsService fieldsService,
            final ObjectFactory<HodFieldsRequestBuilder> fieldsRequestBuilderFactory,
            final GetParametricValuesService getParametricValuesService,
            final GetParametricRangesService getParametricRangesService,
            final BucketingParamsHelper bucketingParamsHelper,
            final TagNameFactory tagNameFactory,
            final ConfigService<? extends HodSearchCapable> configService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever
    ) {
        this.fieldsService = fieldsService;
        this.fieldsRequestBuilderFactory = fieldsRequestBuilderFactory;
        this.getParametricValuesService = getParametricValuesService;
        this.getParametricRangesService = getParametricRangesService;
        this.bucketingParamsHelper = bucketingParamsHelper;
        this.tagNameFactory = tagNameFactory;
        this.configService = configService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    @Override
    @Cacheable(value = CacheNames.PARAMETRIC_VALUES, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public Set<QueryTagInfo> getParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        final Collection<FieldPath> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());

        if (fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFields(parametricRequest.getQueryRestrictions().getDatabases()));
        }

        if (fieldNames.isEmpty()) {
            return Collections.emptySet();
        } else {
            final int start = parametricRequest.getStart();
            final Iterable<FieldValues> response = fetchParametricValues(parametricRequest, fieldNames);

            final Set<QueryTagInfo> output = new HashSet<>();

            for (final FieldValues fieldValues : response) {
                final List<FieldValues.ValueAndCount> responseValues = fieldValues.getValues();

                if (responseValues.size() >= start) {
                    // HOD GetParametricValues has no start parameter, so implement our own here
                    final List<FieldValues.ValueAndCount> valuesInRange = responseValues.subList(start - 1, responseValues.size());

                    final String name = fieldValues.getName();
                    final Set<QueryTagCountInfo> values = valuesInRange.stream()
                            .map(valueAndCount -> new QueryTagCountInfo(valueAndCount.getValue(), tagNameFactory.getTagDisplayValue(name, valueAndCount.getValue()), valueAndCount.getCount()))
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    final TagName tagName = tagNameFactory.buildTagName(name);
                    output.add(QueryTagInfo.builder()
                            .id(tagName.getId().getNormalisedPath())
                            .displayName(tagName.getDisplayName())
                            .values(values)
                            .totalValues(fieldValues.getTotalValues())
                            .build());
                }
            }

            return output;
        }
    }

    @Override
    @Cacheable(value = CacheNames.PARAMETRIC_VALUES_IN_BUCKETS, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public List<RangeInfo> getNumericParametricValuesInBuckets(final HodParametricRequest parametricRequest, final Map<FieldPath, BucketingParams> bucketingParamsPerField) throws HodErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyList();
        } else {
            bucketingParamsHelper.validateBucketingParams(parametricRequest, bucketingParamsPerField);

            final Map<FieldPath, List<Double>> boundariesPerField = bucketingParamsPerField.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> bucketingParamsHelper.calculateBoundaries(entry.getValue())));

            final List<Range> ranges = boundariesPerField.entrySet().stream()
                    .map(entry -> new Range(entry.getKey().getNormalisedPath(), ArrayUtils.toPrimitive(entry.getValue().toArray(new Double[entry.getValue().size()]))))
                    .collect(Collectors.toList());

            final List<FieldRanges> response = fetchParametricRanges(parametricRequest, null, new Ranges(ranges).toString());

            return response.stream()
                    .map(fieldRanges -> {
                        final TagName tagName = tagNameFactory.buildTagName(fieldRanges.getName());

                        final List<Double> boundaries = boundariesPerField.get(tagName.getId());
                        // All buckets have the same size, so just use the value from the first one
                        final double bucketSize = boundaries.get(1) - boundaries.get(0);

                        final List<RangeInfo.Value> values = fieldRanges.getValueRanges().isEmpty()
                                ? bucketingParamsHelper.emptyBuckets(boundaries)
                                : fieldRanges.getValueRanges().stream()
                                .map(fieldValues -> new RangeInfo.Value(fieldValues.getLowerBound(), fieldValues.getUpperBound(), fieldValues.getCount()))
                                .collect(Collectors.toList());

                        return RangeInfo.builder()
                                .id(tagName.getId().getNormalisedPath())
                                .displayName(tagName.getDisplayName())
                                .count(fieldRanges.getValueDetails().getCount())
                                .min(boundaries.get(0))
                                .max(boundaries.get(boundaries.size() - 1))
                                .bucketSize(bucketSize)
                                .values(values)
                                .build();
                    })
                    .collect(Collectors.toList());
        }
    }

    private Collection<FieldPath> lookupFields(final Collection<ResourceName> databases) throws HodErrorException {
        return fieldsService.getFields(fieldsRequestBuilderFactory.getObject()
                .fieldType(FieldTypeParam.Parametric)
                .databases(databases)
                .build())
                .get(FieldTypeParam.Parametric)
                .stream()
                .map(TagName::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<DependentParametricField> getDependentParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        throw new NotImplementedException("Dependent parametric values not yet implemented for hod");
    }

    @Override
    public Map<TagName, ValueDetails> getValueDetails(final HodParametricRequest parametricRequest) throws HodErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyMap();
        } else {
            final List<FieldRanges> response = fetchParametricRanges(parametricRequest, 1, "");

            return response.stream()
                    .collect(Collectors.toMap(fieldRanges -> tagNameFactory.buildTagName(fieldRanges.getName()), fieldRanges -> {
                        final FieldRanges.ValueDetails responseValueDetails = fieldRanges.getValueDetails();

                        return new ValueDetails.Builder()
                                .setAverage(responseValueDetails.getMean())
                                .setMax(responseValueDetails.getMaximum())
                                .setMin(responseValueDetails.getMinimum())
                                .setSum(responseValueDetails.getSum())
                                .setTotalValues(responseValueDetails.getCount())
                                .build();
                    }));
        }
    }

    private List<FieldRanges> fetchParametricRanges(
            final ParametricRequest<HodQueryRestrictions> parametricRequest,
            final Integer maxRanges,
            final String ranges
    ) throws HodErrorException {
        final List<String> fieldNames = parametricRequest.getFieldNames().stream()
                .map(FieldPath::getNormalisedPath)
                .collect(Collectors.toList());

        final GetParametricRangesRequestBuilder params = new GetParametricRangesRequestBuilder()
                .setQueryProfile(getQueryProfile(parametricRequest))
                .setText(parametricRequest.getQueryRestrictions().getQueryText())
                .setFieldText(parametricRequest.getQueryRestrictions().getFieldText())
                .setMinScore(parametricRequest.getQueryRestrictions().getMinScore())
                .setSecurityInfo(authenticationInformationRetriever.getPrincipal().getSecurityInfo())
                .setMaxRanges(maxRanges)
                .setValueDetails(true);

        return getParametricRangesService.getParametricRanges(
                fieldNames,
                parametricRequest.getQueryRestrictions().getDatabases(),
                ranges,
                params
        );
    }

    private Iterable<FieldValues> fetchParametricValues(final ParametricRequest<HodQueryRestrictions> parametricRequest, final Collection<FieldPath> fieldPaths) throws HodErrorException {
        final ResourceName queryProfile = getQueryProfile(parametricRequest);

        final GetParametricValuesRequestBuilder parametricParams = new GetParametricValuesRequestBuilder()
                .setQueryProfile(queryProfile)
                .setSort(ParametricSort.fromParam(parametricRequest.getSort()))
                .setText(parametricRequest.getQueryRestrictions().getQueryText())
                .setFieldText(parametricRequest.getQueryRestrictions().getFieldText())
                .setMaxValues(parametricRequest.getMaxValues())
                .setMinScore(parametricRequest.getQueryRestrictions().getMinScore())
                .setTotalValues(true)
                .setSecurityInfo(authenticationInformationRetriever.getPrincipal().getSecurityInfo());

        final List<String> fieldNames = fieldPaths.stream()
                .map(FieldPath::getNormalisedPath)
                .collect(Collectors.toList());

        final Collection<ResourceName> indexes = parametricRequest.getQueryRestrictions().getDatabases();
        return getParametricValuesService.getParametricValues(fieldNames, indexes, parametricParams);
    }

    private ResourceName getQueryProfile(final ParametricRequest<HodQueryRestrictions> parametricRequest) {
        if (parametricRequest.isModified()) {
            final String profileName = configService.getConfig().getQueryManipulation().getProfile();
            final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
            return new ResourceName(domain, profileName);
        } else {
            return null;
        }
    }
}

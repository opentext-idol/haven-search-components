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
import com.hp.autonomy.hod.client.api.textindex.query.parametric.*;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationPrincipal;
import com.hp.autonomy.searchcomponents.core.caching.CacheNames;
import com.hp.autonomy.searchcomponents.core.fields.TagNameFactory;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParams;
import com.hp.autonomy.searchcomponents.core.parametricvalues.BucketingParamsHelper;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricRequest;
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsService;
import com.hp.autonomy.searchcomponents.hod.search.HodQueryRestrictions;
import com.hp.autonomy.types.idol.responses.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.*;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
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
        final Collection<TagName> fieldNames = new HashSet<>();
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

                    final Set<QueryTagCountInfo> values = valuesInRange.stream()
                            .map(valueAndCount -> new QueryTagCountInfo(valueAndCount.getValue(), valueAndCount.getCount()))
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    output.add(new QueryTagInfo(tagNameFactory.buildTagName(fieldValues.getName()), values, fieldValues.getTotalValues()));
                }
            }

            return output;
        }
    }

    @Override
    @Cacheable(value = CacheNames.PARAMETRIC_VALUES_IN_BUCKETS, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public List<RangeInfo> getNumericParametricValuesInBuckets(final HodParametricRequest parametricRequest, final Map<TagName, BucketingParams> bucketingParamsPerField) throws HodErrorException {
        if (parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyList();
        } else {
            bucketingParamsHelper.validateBucketingParams(parametricRequest, bucketingParamsPerField);

            final Map<TagName, List<Double>> boundariesPerField = bucketingParamsPerField.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> bucketingParamsHelper.calculateBoundaries(entry.getValue())));

            final List<Range> ranges = boundariesPerField.entrySet().stream()
                    .map(entry -> new Range(entry.getKey().getId(), ArrayUtils.toPrimitive(entry.getValue().toArray(new Double[entry.getValue().size()]))))
                    .collect(Collectors.toList());

            final List<FieldRanges> response = fetchParametricRanges(parametricRequest, null, new Ranges(ranges).toString());

            return response.stream()
                    .map(fieldRanges -> {
                        final TagName tagName = tagNameFactory.buildTagName(fieldRanges.getName());

                        final List<Double> boundaries = boundariesPerField.get(tagName);
                        // All buckets have the same size, so just use the value from the first one
                        final double bucketSize = boundaries.get(1) - boundaries.get(0);

                        final List<RangeInfo.Value> values = fieldRanges.getValueRanges().stream()
                                .map(fieldValues -> new RangeInfo.Value(fieldValues.getCount(), fieldValues.getLowerBound(), fieldValues.getUpperBound()))
                                .collect(Collectors.toList());

                        return new RangeInfo(tagName, fieldRanges.getValueDetails().getCount(), boundaries.get(0), boundaries.get(boundaries.size() - 1), bucketSize, values);
                    })
                    .collect(Collectors.toList());
        }
    }

    private Collection<TagName> lookupFields(final Collection<ResourceName> databases) throws HodErrorException {
        return fieldsService.getFields(fieldsRequestBuilderFactory.getObject()
                .databases(databases)
                .build(), FieldTypeParam.Parametric)
                .get(FieldTypeParam.Parametric);
    }

    @Override
    public List<RecursiveField> getDependentParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
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
                .map(TagName::getId)
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

    private Iterable<FieldValues> fetchParametricValues(final ParametricRequest<HodQueryRestrictions> parametricRequest, final Collection<TagName> tagNames) throws HodErrorException {
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

        final List<String> fieldNames = tagNames.stream()
                .map(TagName::getId)
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

/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.parametricvalues;

import com.hp.autonomy.aci.content.ranges.NumericRange;
import com.hp.autonomy.aci.content.ranges.ParametricFieldRange;
import com.hp.autonomy.aci.content.ranges.ParametricFieldRanges;
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
import com.hp.autonomy.types.requests.idol.actions.tags.DateRangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.DateValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.FieldPath;
import com.hp.autonomy.types.requests.idol.actions.tags.NumericRangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.NumericValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService.PARAMETRIC_VALUES_SERVICE_BEAN_NAME;

/**
 * Default HoD implementation of {@link ParametricValuesService}
 */
@Service(PARAMETRIC_VALUES_SERVICE_BEAN_NAME)
class HodParametricValuesServiceImpl implements HodParametricValuesService {
    private static final int HOD_MAX_VALUES = 10000;
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\*");

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

    @SuppressWarnings("SpringCacheableComponentsInspection")
    @Override
    @Cacheable(value = CacheNames.PARAMETRIC_VALUES, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public Set<QueryTagInfo> getParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        final Collection<FieldPath> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());

        if(fieldNames.isEmpty()) {
            fieldNames.addAll(lookupFields(parametricRequest.getQueryRestrictions().getDatabases()));
        }

        if(fieldNames.isEmpty()) {
            return Collections.emptySet();
        } else {
            final int start = parametricRequest.getStart();
            final Iterable<FieldValues> response = fetchParametricValues(parametricRequest, fieldNames);

            final Set<QueryTagInfo> output = new HashSet<>();

            for(final FieldValues fieldValues : response) {
                final List<FieldValues.ValueAndCount> responseValues = fieldValues.getValues();

                if(responseValues.size() >= start) {
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

    @SuppressWarnings("SpringCacheableComponentsInspection")
    @Override
    @Cacheable(value = CacheNames.NUMERIC_PARAMETRIC_VALUES_IN_BUCKETS, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public List<NumericRangeInfo> getNumericParametricValuesInBuckets(final HodParametricRequest parametricRequest, final Map<FieldPath, BucketingParams<Double>> bucketingParamsPerField) throws HodErrorException {
        return getParametricValuesInBuckets(parametricRequest, bucketingParamsPerField, bucketingParamsHelper::calculateNumericBoundaries, Function.identity());
    }

    private <T extends Comparable<? super T> & Serializable> List<NumericRangeInfo> getParametricValuesInBuckets(
        final ParametricRequest<HodQueryRestrictions> parametricRequest,
        final Map<FieldPath, BucketingParams<T>> bucketingParamsPerField,
        final Function<BucketingParams<T>, List<T>> calculateBoundaries,
        final Function<List<T>, List<Double>> convertBoundaries
    ) throws HodErrorException {
        if(parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyList();
        } else {
            bucketingParamsHelper.validateBucketingParams(parametricRequest, bucketingParamsPerField);

            final Map<FieldPath, List<Double>> boundariesPerField = bucketingParamsPerField.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> convertBoundaries.apply(calculateBoundaries.apply(entry.getValue()))));

            final List<ParametricFieldRange> ranges = boundariesPerField.entrySet().stream()
                .map(entry -> new NumericRange(entry.getKey().getNormalisedPath(), entry.getValue()))
                .collect(Collectors.toList());

            final List<FieldRanges> response = fetchParametricRanges(parametricRequest, null, new ParametricFieldRanges(ranges).toString());

            return response.stream()
                .map(fieldRanges -> {
                    final TagName tagName = tagNameFactory.buildTagName(fieldRanges.getName());

                    final List<Double> boundaries = boundariesPerField.get(tagName.getId());
                    // All buckets have the same size, so just use the value from the first one
                    final double bucketSize = boundaries.get(1) - boundaries.get(0);

                    @SuppressWarnings("RedundantTypeArguments") // presumably Java bug
                    final List<NumericRangeInfo.Value> values = fieldRanges.getValueRanges().isEmpty()
                        ? bucketingParamsHelper.<Double, Double, NumericRangeInfo.Value>emptyBuckets(boundaries, NumericRangeInfo.Value::new)
                        : fieldRanges.getValueRanges().stream()
                        .map(fieldValues -> new NumericRangeInfo.Value(fieldValues.getLowerBound(), fieldValues.getUpperBound(), fieldValues.getCount()))
                        .collect(Collectors.toList());

                    return NumericRangeInfo.builder()
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

    @SuppressWarnings("SpringCacheableComponentsInspection")
    @Override
    @Cacheable(value = CacheNames.NUMERIC_PARAMETRIC_VALUES_IN_BUCKETS, cacheResolver = CachingConfiguration.PER_USER_CACHE_RESOLVER_NAME)
    public List<DateRangeInfo> getDateParametricValuesInBuckets(final HodParametricRequest parametricRequest, final Map<FieldPath, BucketingParams<ZonedDateTime>> bucketingParamsPerField) throws HodErrorException {
        final Function<List<ZonedDateTime>, List<Double>> convertBoundaries = dateBoundaries -> dateBoundaries.stream()
            .map(boundary -> (double)boundary.toEpochSecond())
            .collect(Collectors.toList());
        return getParametricValuesInBuckets(parametricRequest, bucketingParamsPerField, bucketingParamsHelper::calculateDateBoundaries, convertBoundaries).stream()
            .map(r -> DateRangeInfo.builder()
                .id(r.getId())
                .displayName(r.getDisplayName())
                .count(r.getCount())
                .min(epochToZonedDateTime(r.getMin()))
                .max(epochToZonedDateTime(r.getMax()))
                .bucketSize(Duration.ofSeconds((long)(double)r.getBucketSize()))
                .values(r.getValues().stream()
                            .map(v -> new DateRangeInfo.Value(epochToZonedDateTime(v.getMin()), epochToZonedDateTime(v.getMax()), v.getCount()))
                            .collect(Collectors.toList()))
                .build())
            .collect(Collectors.toList());
    }

    private Collection<FieldPath> lookupFields(final Collection<ResourceName> databases) throws HodErrorException {
        return Optional.ofNullable(
                fieldsService.getFields(fieldsRequestBuilderFactory.getObject()
                                           .fieldType(FieldTypeParam.Parametric)
                                           .databases(databases)
                                           .build())
            .get(FieldTypeParam.Parametric)
            ).orElse(Collections.emptySet()).stream()
            .map(TagName::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<DependentParametricField> getDependentParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        throw new NotImplementedException("Dependent parametric values not yet implemented for hod");
    }

    @Override
    public Map<FieldPath, NumericValueDetails> getNumericValueDetails(final HodParametricRequest parametricRequest) throws HodErrorException {
        if(parametricRequest.getFieldNames().isEmpty()) {
            return Collections.emptyMap();
        } else {
            final List<FieldRanges> response = fetchParametricRanges(parametricRequest, 1, "");

            return response.stream()
                .collect(Collectors.toMap(fieldRanges -> tagNameFactory.getFieldPath(fieldRanges.getName()), fieldRanges -> {
                    final FieldRanges.ValueDetails responseValueDetails = fieldRanges.getValueDetails();

                    return NumericValueDetails.builder()
                        .average(responseValueDetails.getMean())
                        .max(responseValueDetails.getMaximum())
                        .min(responseValueDetails.getMinimum())
                        .sum(responseValueDetails.getSum())
                        .totalValues(responseValueDetails.getCount())
                        .build();
                }));
        }
    }

    @Override
    public Map<FieldPath, DateValueDetails> getDateValueDetails(final HodParametricRequest parametricRequest) throws HodErrorException {
        return getNumericValueDetails(parametricRequest).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> DateValueDetails.builder()
                .min(epochToZonedDateTime(e.getValue().getMin()))
                .max(epochToZonedDateTime(e.getValue().getMax()))
                .average(epochToZonedDateTime(e.getValue().getAverage()))
                .sum(e.getValue().getSum())
                .totalValues(e.getValue().getTotalValues())
                .build()));
    }

    private ZonedDateTime epochToZonedDateTime(final double epochTime) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond((long)epochTime), ZoneOffset.UTC);
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
            .setMaxValues(parametricRequest.getValueRestrictions().isEmpty() ? Math.min(parametricRequest.getMaxValues(), HOD_MAX_VALUES) : HOD_MAX_VALUES)
            .setMinScore(parametricRequest.getQueryRestrictions().getMinScore())
            .setTotalValues(true)
            .setSecurityInfo(authenticationInformationRetriever.getPrincipal().getSecurityInfo());

        final List<String> fieldNames = fieldPaths.stream()
            .map(FieldPath::getNormalisedPath)
            .collect(Collectors.toList());

        final Collection<ResourceName> indexes = parametricRequest.getQueryRestrictions().getDatabases();
        final List<FieldValues> parametricValues = getParametricValuesService.getParametricValues(fieldNames, indexes, parametricParams);
        return parametricRequest.getValueRestrictions().isEmpty() ? parametricValues : parametricValues.stream()
            .map(fieldValues -> {
                final List<FieldValues.ValueAndCount> values = fieldValues.getValues();
                return fieldValues.toBuilder()
                    .clearValues()
                    .values(values
                                .stream()
                                .filter(valueAndCount -> parametricRequest.getValueRestrictions().stream()
                                    .anyMatch(restriction -> valueAndCount.getValue().toLowerCase().matches(WILDCARD_PATTERN.matcher(restriction.toLowerCase()).replaceAll(".*"))))
                                .limit(parametricRequest.getMaxValues())
                                .collect(Collectors.toList()))
                    .build();
            })
            .filter(fieldValues -> !fieldValues.getValues().isEmpty())
            .collect(Collectors.toList());
    }

    private ResourceName getQueryProfile(final ParametricRequest<HodQueryRestrictions> parametricRequest) {
        if(parametricRequest.isModified()) {
            final String profileName = configService.getConfig().getQueryManipulation().getProfile();
            final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
            return new ResourceName(domain, profileName);
        } else {
            return null;
        }
    }
}

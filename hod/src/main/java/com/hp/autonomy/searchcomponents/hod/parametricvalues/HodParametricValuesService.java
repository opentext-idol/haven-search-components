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
import com.hp.autonomy.searchcomponents.core.parametricvalues.ParametricValuesService;
import com.hp.autonomy.searchcomponents.hod.configuration.HodSearchCapable;
import com.hp.autonomy.searchcomponents.hod.fields.HodFieldsRequest;
import com.hp.autonomy.types.idol.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagCountInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HodParametricValuesService implements ParametricValuesService<HodParametricRequest, ResourceIdentifier, HodErrorException> {
    private final FieldsService<HodFieldsRequest, HodErrorException> fieldsService;
    private final GetParametricValuesService getParametricValuesService;
    private final ConfigService<? extends HodSearchCapable> configService;
    private final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever;

    public HodParametricValuesService(
            final FieldsService<HodFieldsRequest, HodErrorException> fieldsService,
            final GetParametricValuesService getParametricValuesService,
            final ConfigService<? extends HodSearchCapable> configService,
            final AuthenticationInformationRetriever<?, HodAuthenticationPrincipal> authenticationInformationRetriever
    ) {
        this.fieldsService = fieldsService;
        this.getParametricValuesService = getParametricValuesService;
        this.configService = configService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    @Override
    @Cacheable(CacheNames.PARAMETRIC_VALUES)
    public Set<QueryTagInfo> getAllParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        final Collection<String> fieldNames = new HashSet<>();
        fieldNames.addAll(parametricRequest.getFieldNames());
        if (fieldNames.isEmpty()) {
            fieldNames.addAll(fieldsService.getParametricFields(new HodFieldsRequest.Builder().setDatabases(parametricRequest.getQueryRestrictions().getDatabases()).build()));
        }

        final Set<QueryTagInfo> results;
        if (fieldNames.isEmpty()) {
            results = Collections.emptySet();
        } else {
            final ResourceIdentifier queryProfile = parametricRequest.isModified() ? getQueryProfile() : null;

            final GetParametricValuesRequestBuilder parametricParams = new GetParametricValuesRequestBuilder()
                    .setQueryProfile(queryProfile)
                    .setSort(ParametricSort.document_count)
                    .setText(parametricRequest.getQueryRestrictions().getQueryText())
                    .setFieldText(parametricRequest.getQueryRestrictions().getFieldText())
                    .setMaxValues(parametricRequest.getMaxValues());

            final FieldNames parametricFieldNames = getParametricValuesService.getParametricValues(fieldNames,
                    new ArrayList<>(parametricRequest.getQueryRestrictions().getDatabases()), parametricParams);
            final Set<String> fieldNamesSet = parametricFieldNames.getFieldNames();

            results = new HashSet<>();
            for (final String name : fieldNamesSet) {
                final Set<QueryTagCountInfo> values = new HashSet<>(parametricFieldNames.getValuesAndCountsForFieldName(name));
                if (!values.isEmpty()) {
                    results.add(new QueryTagInfo(name, values));
                }
            }
        }

        return results;
    }

    @Override
    public List<RecursiveField> getDependentParametricValues(final HodParametricRequest parametricRequest) throws HodErrorException {
        throw new NotImplementedException();
    }

    private ResourceIdentifier getQueryProfile() {
        final String profileName = configService.getConfig().getQueryManipulation().getProfile();
        final String domain = authenticationInformationRetriever.getPrincipal().getApplication().getDomain();
        return new ResourceIdentifier(domain, profileName);
    }

}

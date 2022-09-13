/*
 * (c) Copyright 2015-2017 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.aci.content.identifier.reference.ReferencesBuilder;
import com.hp.autonomy.aci.content.identifier.stateid.StateIdsBuilder;
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.authentication.CommunityPrincipal;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.searchcomponents.idol.view.IdolViewRequest;
import com.hp.autonomy.types.requests.idol.actions.query.params.GetContentParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.view.params.OutputTypeParam;
import com.hp.autonomy.types.requests.idol.actions.view.params.ViewParams;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_END_TAG;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_START_TAG;
import static com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler.PARAMETER_HANDLER_BEAN_NAME;

/**
 * Default implementation of {@link HavenSearchAciParameterHandler}
 */
@Component(PARAMETER_HANDLER_BEAN_NAME)
class HavenSearchAciParameterHandlerImpl implements HavenSearchAciParameterHandler {
    private static final String GET_CONTENT_QUERY_TEXT = "*";

    private final ConfigService<? extends IdolSearchCapable> configService;
    private final DocumentFieldsService documentFieldsService;
    private final AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever;

    private final Escaper urlFragmentEscaper = UrlEscapers.urlFragmentEscaper();

    public static final String IDOL_USER_REQUEST_PREFIX_PROPERTY_KEY = "idol.user.request.prefix";
    public static final String IDOL_USER_REQUEST_FIELDS_PROPERTY_KEY = "idol.user.request.fields";

    private final String userRequestPrefix;
    private final Set<String> userRequestFields;

    @Autowired
    HavenSearchAciParameterHandlerImpl(
        final ConfigService<? extends IdolSearchCapable> configService,
        final DocumentFieldsService documentFieldsService,
        final AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever,
        @Value("${"+IDOL_USER_REQUEST_PREFIX_PROPERTY_KEY+":}") final String userRequestPrefix,
        @Value("${"+ IDOL_USER_REQUEST_FIELDS_PROPERTY_KEY +":}") final List<String> userRequestFields
    ) {
        this.configService = configService;
        this.documentFieldsService = documentFieldsService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
        this.userRequestPrefix = StringUtils.defaultIfEmpty(userRequestPrefix, null);
        this.userRequestFields = new HashSet<>();

        if (userRequestFields != null) {
            this.userRequestFields.addAll(userRequestFields);
        }
    }

    @Override
    public void addSearchRestrictions(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        aciParameters.add(QueryParams.Text.name(), queryRestrictions.getQueryText());
        if(!queryRestrictions.getDatabases().isEmpty()) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(queryRestrictions.getDatabases()));
        }
        if(!queryRestrictions.getStateMatchIds().isEmpty()) {
            aciParameters.add(QueryParams.StateMatchID.name(), new StateIdsBuilder(queryRestrictions.getStateMatchIds()));
        }
        if(!queryRestrictions.getStateDontMatchIds().isEmpty()) {
            aciParameters.add(QueryParams.StateDontMatchID.name(), new StateIdsBuilder(queryRestrictions.getStateDontMatchIds()));
        }

        final IdolSearchCapable config = configService.getConfig();

        aciParameters.add(QueryParams.Combine.name(), config.getCombineMethod());
        aciParameters.add(QueryParams.MinDate.name(), formatDate(queryRestrictions.getMinDate()));
        aciParameters.add(QueryParams.MaxDate.name(), formatDate(queryRestrictions.getMaxDate()));
        aciParameters.add(QueryParams.FieldText.name(), queryRestrictions.getFieldText());
        aciParameters.add(QueryParams.MinScore.name(), queryRestrictions.getMinScore());

        addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Override
    public void addSearchOutputParameters(final AciParameters aciParameters, final IdolSearchRequest searchRequest) {
        addSecurityInfo(aciParameters);

        aciParameters.add(QueryParams.Start.name(), searchRequest.getStart());
        aciParameters.add(QueryParams.MaxResults.name(), searchRequest.getMaxResults());
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.fromValue(searchRequest.getSummary(), null));
        aciParameters.add(QueryParams.Characters.name(), searchRequest.getSummaryCharacters());
        aciParameters.add(QueryParams.Predict.name(), true);
        aciParameters.add(QueryParams.Sort.name(), searchRequest.getSort());
        aciParameters.add(QueryParams.TotalResults.name(), true);
        aciParameters.add(QueryParams.XMLMeta.name(), true);
        addPrintParameters(
            aciParameters,
            PrintParam.fromValue(searchRequest.getPrint(), null),
            searchRequest.getPrintFields(),
            searchRequest.getReferenceField());

        if(searchRequest.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), DocumentsService.HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), DocumentsService.HIGHLIGHT_END_TAG);
        }
    }

    @Override
    public void addGetDocumentOutputParameters(final AciParameters aciParameters, final IdolGetContentRequestIndex indexAndReferences, final IdolGetContentRequest request) {
        addSecurityInfo(aciParameters);

        final IdolSearchCapable config = configService.getConfig();

        final Set<String> references = indexAndReferences.getReferences();
        aciParameters.add(QueryParams.MatchReference.name(), new ReferencesBuilder(references));
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.Concept);
        aciParameters.add(QueryParams.Combine.name(), config.getCombineMethod());
        aciParameters.add(QueryParams.Text.name(), GET_CONTENT_QUERY_TEXT);
        aciParameters.add(QueryParams.MaxResults.name(), references.size());
        aciParameters.add(QueryParams.AnyLanguage.name(), true);
        aciParameters.add(QueryParams.XMLMeta.name(), true);
        addPrintParameters(
            aciParameters,
            request.getPrint(),
            Collections.emptyList(),
            request.getReferenceField());

        if(indexAndReferences.getIndex() != null) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(indexAndReferences.getIndex()));
        }

        addDefaultReferenceField(aciParameters);
    }

    private void addDefaultReferenceField(final AciParameters aciParameters) {
        final String referenceField = configService.getConfig().getReferenceField();
        if(StringUtils.isNotEmpty(referenceField)) {
            aciParameters.add(QueryParams.ReferenceField.name(), referenceField);
        }
    }

    private void addPrintParameters(
        final AciParameters aciParameters,
        final PrintParam print,
        final Collection<String> printFields,
        final String referenceField
    ) {
        final List<String> rawPrintFields = new ArrayList<>(documentFieldsService.getPrintFields(
            printFields == null ? Collections.emptyList() : printFields));
        if (referenceField != null &&
            (print == PrintParam.Fields || print == PrintParam.Reference)
        ) {
            rawPrintFields.add(referenceField);
            addPrintParameters(aciParameters, PrintParam.Fields, rawPrintFields);
        } else {
            addPrintParameters(aciParameters, print, rawPrintFields);
        }
    }

    private void addPrintParameters(
        final AciParameters aciParameters,
        final PrintParam print,
        final Collection<String> rawPrintFields
    ) {
        aciParameters.add(QueryParams.Print.name(), print);
        if (print == PrintParam.Fields) {
            aciParameters.add(
                QueryParams.PrintFields.name(),
                new PrintFields(rawPrintFields));
        }
    }

    @Override
    public void addGetContentOutputParameters(final AciParameters parameters, final String database, final String documentReference, final String referenceField) {
        addSecurityInfo(parameters);

        if(database != null) {
            parameters.add(GetContentParams.DatabaseMatch.name(), new Databases(database));
        }

        parameters.add(GetContentParams.Reference.name(), new Reference(documentReference));

        if (referenceField != null) {
            parameters.add(QueryParams.ReferenceField.name(), referenceField);
        }
        else {
            addDefaultReferenceField(parameters);
        }
    }

    @Override
    public void addLanguageRestriction(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        // If the AnyLanguage parameter is true, documents with any language can be returned, otherwise documents matching
        // the MatchLanguage parameter are returned.
        if(queryRestrictions.isAnyLanguage()) {
            aciParameters.add(QueryParams.AnyLanguage.name(), true);
        }

        // The LanguageType parameter specifies the language and encoding of the query text. If no MatchLanguage or
        // AnyLanguage parameter is set, this acts as a MatchLanguage restriction for the specified language. If no
        // LanguageType is set, the query text is interpreted using the default language and encoding.
        if(queryRestrictions.getLanguageType() != null) {
            aciParameters.add(QueryParams.LanguageType.name(), queryRestrictions.getLanguageType());
        }
    }

    @Override
    public void addQmsParameters(final AciParameters aciParameters, final IdolQueryRestrictions queryRestrictions) {
        aciParameters.add(QmsQueryParams.Blacklist.name(), configService.getConfig().getQueryManipulation().getBlacklist());
        aciParameters.add(QmsQueryParams.ExpandQuery.name(), configService.getConfig().getQueryManipulation().getExpandQuery());
        aciParameters.add(QmsQueryParams.SynonymDatabaseMatch.name(), configService.getConfig().getQueryManipulation().getSynonymDatabaseMatch());

        final CommunityPrincipal principal = authenticationInformationRetriever.getPrincipal();
        if (principal != null) {
            aciParameters.add(QmsQueryParams.ExplicitProfiling.name(),
                configService.getConfig().getQueryManipulation().getExplicitProfiling());
            aciParameters.add(QmsQueryParams.Username.name(), principal.getName());
        }
    }

    @Override
    public void addIntentBasedRankingParameters(final AciParameters aciParameters) {
        CommunityPrincipal principal = authenticationInformationRetriever.getPrincipal();
        if (principal != null) {
            aciParameters.add("Username", principal.getName());
            aciParameters.add("IntentRankedQuery","True");
            aciParameters.add("SoftCacheMaxSize","10240");
            aciParameters.add("DefaultIRQCorpusSize","100");
        }
    }

    @Override
    public String getSecurityInfo() {
        return authenticationInformationRetriever.getPrincipal() == null || authenticationInformationRetriever.getPrincipal().getSecurityInfo() == null
            ? null
            : urlFragmentEscaper.escape(authenticationInformationRetriever.getPrincipal().getSecurityInfo());
    }

    @Override
    public void addSecurityInfo(final AciParameters aciParameters) {
        aciParameters.add(QueryParams.SecurityInfo.name(), getSecurityInfo());
    }

    @Override
    public void addUserIdentifiers(final AciParameters aciParameters) {
        if (this.userRequestPrefix != null) {
            final CommunityPrincipal principal = authenticationInformationRetriever.getPrincipal();

            if (principal != null) {
                aciParameters.add(userRequestPrefix + "User", principal.getName());

                Optional.ofNullable(principal.getFields()).ifPresent(f -> {
                    for(String property : userRequestFields) {
                        final String value = f.get(property);

                        if(value != null) {
                            aciParameters.add(userRequestPrefix + property, value);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void addStoreStateParameters(final AciParameters aciParameters) {
        aciParameters.add(QueryParams.StoreState.name(), true);
        aciParameters.add(QueryParams.StoredStateTokenLifetime.name(), -1);  // negative value means no expiry (DAH)
        final String storedStateField = configService.getConfig().getStoredStateField();
        if(StringUtils.isNotEmpty(storedStateField)) {
            aciParameters.add(QueryParams.StoredStateField.name(), storedStateField);
        }
    }

    @Override
    public void addViewParameters(final AciParameters aciParameters, final String reference, final IdolViewRequest viewRequest) {
        aciParameters.add(ViewParams.NoACI.name(), true);
        aciParameters.add(ViewParams.Reference.name(), reference);
        addSecurityInfo(aciParameters);

        if (!viewRequest.isOriginal()) {
            aciParameters.add(ViewParams.EmbedImages.name(), true);
            aciParameters.add(ViewParams.StripScript.name(), true);
            aciParameters.add(ViewParams.OriginalBaseURL.name(), true);

            if(viewRequest.getHighlightExpression() != null) {
                aciParameters.add(ViewParams.Links.name(), viewRequest.getHighlightExpression());
                aciParameters.add(ViewParams.StartTag.name(), HIGHLIGHT_START_TAG);
                aciParameters.add(ViewParams.EndTag.name(), HIGHLIGHT_END_TAG);

                // we need this because we're sending query text, not a csv of stemmed terms
                aciParameters.add(ViewParams.Boolean.name(), true);
            }
        }

        aciParameters.add(ViewParams.OutputType.name(),
            viewRequest.isOriginal() ? OutputTypeParam.Raw : OutputTypeParam.HTML);
    }

    private String formatDate(final ZonedDateTime date) {
        return date == null
            ? null
            : DateTimeFormatter.ISO_INSTANT.format(date.truncatedTo(ChronoUnit.SECONDS));
    }
}

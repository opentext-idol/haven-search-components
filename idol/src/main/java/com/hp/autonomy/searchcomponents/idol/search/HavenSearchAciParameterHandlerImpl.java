/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.GetContentParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.view.params.OutputTypeParam;
import com.hp.autonomy.types.requests.idol.actions.view.params.ViewParams;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import com.hpe.bigdata.frontend.spring.authentication.AuthenticationInformationRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_END_TAG;
import static com.hp.autonomy.searchcomponents.core.view.ViewServerService.HIGHLIGHT_START_TAG;
import static com.hp.autonomy.searchcomponents.idol.search.HavenSearchAciParameterHandler.PARAMETER_HANDLER_BEAN_NAME;

/**
 * Default implementation of {@link HavenSearchAciParameterHandler}
 */
@Component(PARAMETER_HANDLER_BEAN_NAME)
class HavenSearchAciParameterHandlerImpl implements HavenSearchAciParameterHandler {
    private static final String GET_CONTENT_QUERY_TEXT = "*";

    private static final String SIMPLE_AND_FIELDCHECK = CombineParam.Simple + "+" + CombineParam.FieldCheck;

    private final ConfigService<? extends IdolSearchCapable> configService;
    private final DocumentFieldsService documentFieldsService;
    private final AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever;

    private final Escaper urlFragmentEscaper = UrlEscapers.urlFragmentEscaper();

    @Autowired
    HavenSearchAciParameterHandlerImpl(
        final ConfigService<? extends IdolSearchCapable> configService,
        final DocumentFieldsService documentFieldsService,
        final AuthenticationInformationRetriever<?, CommunityPrincipal> authenticationInformationRetriever
    ) {
        this.configService = configService;
        this.documentFieldsService = documentFieldsService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
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

        aciParameters.add(QueryParams.Combine.name(), SIMPLE_AND_FIELDCHECK);
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
        addPrintParameters(aciParameters, PrintParam.fromValue(searchRequest.getPrint(), null), searchRequest.getPrintFields());

        if(searchRequest.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), DocumentsService.HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), DocumentsService.HIGHLIGHT_END_TAG);
        }
    }

    @Override
    public void addGetDocumentOutputParameters(final AciParameters aciParameters, final IdolGetContentRequestIndex indexAndReferences, final PrintParam print) {
        addSecurityInfo(aciParameters);

        final Set<String> references = indexAndReferences.getReferences();
        aciParameters.add(QueryParams.MatchReference.name(), new ReferencesBuilder(references));
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.Concept);
        aciParameters.add(QueryParams.Combine.name(), SIMPLE_AND_FIELDCHECK);
        aciParameters.add(QueryParams.Text.name(), GET_CONTENT_QUERY_TEXT);
        aciParameters.add(QueryParams.MaxResults.name(), references.size());
        aciParameters.add(QueryParams.AnyLanguage.name(), true);
        aciParameters.add(QueryParams.XMLMeta.name(), true);
        addPrintParameters(aciParameters, print, Collections.emptyList());

        if(indexAndReferences.getIndex() != null) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(indexAndReferences.getIndex()));
        }
    }

    private void addPrintParameters(final AciParameters aciParameters, final PrintParam print, final Collection<String> printFields) {
        aciParameters.add(QueryParams.Print.name(), print);
        if(print == PrintParam.Fields) {
            final Collection<String> printFieldsToApply = documentFieldsService.getPrintFields(printFields);
            aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(printFieldsToApply));
        }
    }

    @Override
    public void addGetContentOutputParameters(final AciParameters parameters, final String database, final String documentReference, final String referenceField) {
        addSecurityInfo(parameters);

        if(database != null) {
            parameters.add(GetContentParams.DatabaseMatch.name(), new Databases(database));
        }

        parameters.add(GetContentParams.Reference.name(), new Reference(documentReference));
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
    }

    @Override
    public void addSecurityInfo(final AciParameters aciParameters) {
        final String securityInfo = authenticationInformationRetriever.getPrincipal() == null || authenticationInformationRetriever.getPrincipal().getSecurityInfo() == null
            ? null
            : urlFragmentEscaper.escape(authenticationInformationRetriever.getPrincipal().getSecurityInfo());
        aciParameters.add(QueryParams.SecurityInfo.name(), securityInfo);
    }

    @Override
    public void addStoreStateParameters(final AciParameters aciParameters) {
        aciParameters.add(QueryParams.StoreState.name(), true);
        aciParameters.add(QueryParams.StoredStateTokenLifetime.name(), -1);  // negative value means no expiry (DAH)
    }

    @Override
    public void addViewParameters(final AciParameters aciParameters, final String reference, final IdolViewRequest viewRequest) {
        aciParameters.add(ViewParams.NoACI.name(), true);
        aciParameters.add(ViewParams.Reference.name(), reference);
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

        // this prevents ViewServer from returning the raw file
        aciParameters.add(ViewParams.OutputType.name(), OutputTypeParam.HTML);
    }

    private String formatDate(final ZonedDateTime date) {
        return date == null
            ? null
            : DateTimeFormatter.ISO_INSTANT.format(date.truncatedTo(ChronoUnit.SECONDS));
    }
}

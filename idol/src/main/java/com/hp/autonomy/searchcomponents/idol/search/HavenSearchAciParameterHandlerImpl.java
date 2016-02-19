/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
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
import com.hp.autonomy.searchcomponents.core.authentication.AuthenticationInformationRetriever;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.fields.DocumentFieldsService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.GetContentParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;

public class HavenSearchAciParameterHandlerImpl implements HavenSearchAciParameterHandler {
    private static final String IDOL_DATE_PARAMETER_FORMAT = "HH:mm:ss dd/MM/yyyy";
    private static final String GET_CONTENT_QUERY_TEXT = "*";

    protected final ConfigService<? extends IdolSearchCapable> configService;
    protected final LanguagesService languagesService;
    protected final DocumentFieldsService documentFieldsService;
    protected final AuthenticationInformationRetriever<CommunityPrincipal> authenticationInformationRetriever;

    private final Escaper urlFragmentEscaper = UrlEscapers.urlFragmentEscaper();

    public HavenSearchAciParameterHandlerImpl(final ConfigService<? extends IdolSearchCapable> configService, final LanguagesService languagesService, final DocumentFieldsService documentFieldsService, final AuthenticationInformationRetriever<CommunityPrincipal> authenticationInformationRetriever) {
        this.configService = configService;
        this.languagesService = languagesService;
        this.documentFieldsService = documentFieldsService;
        this.authenticationInformationRetriever = authenticationInformationRetriever;
    }

    @Override
    public void addSearchRestrictions(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions) {
        aciParameters.add(QueryParams.Text.name(), queryRestrictions.getQueryText());
        if (!queryRestrictions.getDatabases().isEmpty()) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(queryRestrictions.getDatabases()));
        }
        if (!queryRestrictions.getStateMatchId().isEmpty()) {
            aciParameters.add(QueryParams.StateMatchID.name(), new StateIdsBuilder(queryRestrictions.getStateMatchId()));
        }
        if (!queryRestrictions.getStateDontMatchId().isEmpty()) {
            aciParameters.add(QueryParams.StateDontMatchID.name(), new StateIdsBuilder(queryRestrictions.getStateDontMatchId()));
        }
        aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
        aciParameters.add(QueryParams.MinDate.name(), formatDate(queryRestrictions.getMinDate()));
        aciParameters.add(QueryParams.MaxDate.name(), formatDate(queryRestrictions.getMaxDate()));
        aciParameters.add(QueryParams.FieldText.name(), queryRestrictions.getFieldText());

        addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Override
    public void addSearchOutputParameters(final AciParameters aciParameters, final AciSearchRequest<String> searchRequest) {
        aciParameters.add(QueryParams.SecurityInfo.name(), getSecurityInfo());

        aciParameters.add(QueryParams.Start.name(), searchRequest.getStart());
        aciParameters.add(QueryParams.MaxResults.name(), searchRequest.getMaxResults());
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.fromValue(searchRequest.getSummary(), null));
        aciParameters.add(QueryParams.Characters.name(), searchRequest.getSummaryCharacters());
        aciParameters.add(QueryParams.Predict.name(), false);
        aciParameters.add(QueryParams.Sort.name(), searchRequest.getSort());
        aciParameters.add(QueryParams.Print.name(), PrintParam.Fields);
        aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(documentFieldsService.getPrintFields()));
        aciParameters.add(QueryParams.TotalResults.name(), true);
        aciParameters.add(QueryParams.XMLMeta.name(), true);

        if (searchRequest.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), DocumentsService.HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), DocumentsService.HIGHLIGHT_END_TAG);
        }
    }

    @Override
    public void addGetDocumentOutputParameters(final AciParameters aciParameters, final GetContentRequestIndex<String> indexAndReferences, final PrintParam print) {
        aciParameters.add(QueryParams.SecurityInfo.name(), getSecurityInfo());

        aciParameters.add(QueryParams.MatchReference.name(), new ReferencesBuilder(indexAndReferences.getReferences()));
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.Concept);
        aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
        aciParameters.add(QueryParams.Text.name(), GET_CONTENT_QUERY_TEXT);
        aciParameters.add(QueryParams.MaxResults.name(), 1);
        aciParameters.add(QueryParams.AnyLanguage.name(), true);
        aciParameters.add(QueryParams.Print.name(), print);
        if (print == PrintParam.Fields) {
            aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(documentFieldsService.getPrintFields()));
        }
        aciParameters.add(QueryParams.XMLMeta.name(), true);

        if (indexAndReferences.getIndex() != null) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(indexAndReferences.getIndex()));
        }
    }

    @Override
    public void addGetContentOutputParameters(final AciParameters parameters, final String database, final String documentReference, final String referenceField) {
        parameters.add(GetContentParams.SecurityInfo.name(), getSecurityInfo());

        if (database != null) {
            parameters.add(GetContentParams.DatabaseMatch.name(), new Databases(database));
        }
        parameters.add(GetContentParams.Reference.name(), new Reference(documentReference));
        parameters.add(GetContentParams.Print.name(), PrintParam.Fields);
        parameters.add(GetContentParams.PrintFields.name(), new PrintFields(referenceField));
    }

    @Override
    public void addLanguageRestriction(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions) {
        if (queryRestrictions.isAnyLanguage()) {
            aciParameters.add(QueryParams.AnyLanguage.name(), true);
        } else {
            final String languageType = queryRestrictions.getLanguageType() != null ? queryRestrictions.getLanguageType() : languagesService.getDefaultLanguageId();
            aciParameters.add(QueryParams.LanguageType.name(), languageType);
        }
    }

    @Override
    public void addQmsParameters(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions) {
        aciParameters.add(QmsQueryParams.Blacklist.name(), configService.getConfig().getQueryManipulation().getBlacklist());
        aciParameters.add(QmsQueryParams.ExpandQuery.name(), configService.getConfig().getQueryManipulation().getExpandQuery());
    }

    private String getSecurityInfo() {
        return authenticationInformationRetriever.getPrincipal() != null && authenticationInformationRetriever.getPrincipal().getSecurityInfo() != null
                ? urlFragmentEscaper.escape(authenticationInformationRetriever.getPrincipal().getSecurityInfo())
                : null;
    }

    protected String formatDate(final ReadableInstant date) {
        return date == null ? null : DateTimeFormat.forPattern(IDOL_DATE_PARAMETER_FORMAT).print(date);
    }
}

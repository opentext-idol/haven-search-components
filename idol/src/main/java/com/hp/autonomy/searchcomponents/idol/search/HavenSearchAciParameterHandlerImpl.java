/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.searchcomponents.core.languages.LanguagesService;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;

public class HavenSearchAciParameterHandlerImpl implements HavenSearchAciParameterHandler {
    private static final String IDOL_DATE_PARAMETER_FORMAT = "HH:mm:ss dd/MM/yyyy";

    protected final ConfigService<? extends HavenSearchCapable> configService;
    protected final LanguagesService languagesService;

    public HavenSearchAciParameterHandlerImpl(final ConfigService<? extends HavenSearchCapable> configService, final LanguagesService languagesService) {
        this.configService = configService;
        this.languagesService = languagesService;
    }

    @Override
    public void addSearchRestrictions(final AciParameters aciParameters, final QueryRestrictions<String> queryRestrictions) {
        aciParameters.add(QueryParams.Text.name(), queryRestrictions.getQueryText());
        if (!queryRestrictions.getDatabases().isEmpty()) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(queryRestrictions.getDatabases()));
        }
        aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
        aciParameters.add(QueryParams.MinDate.name(), formatDate(queryRestrictions.getMinDate()));
        aciParameters.add(QueryParams.MaxDate.name(), formatDate(queryRestrictions.getMaxDate()));
        aciParameters.add(QueryParams.FieldText.name(), queryRestrictions.getFieldText());

        addLanguageRestriction(aciParameters, queryRestrictions);
    }

    @Override
    public void addSearchOutputParameters(final AciParameters aciParameters, final SearchRequest<String> searchRequest) {
        aciParameters.add(QueryParams.Start.name(), searchRequest.getStart());
        aciParameters.add(QueryParams.MaxResults.name(), searchRequest.getMaxResults());
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.fromValue(searchRequest.getSummary(), null));
        aciParameters.add(QueryParams.Predict.name(), false);
        aciParameters.add(QueryParams.Sort.name(), searchRequest.getSort());
        aciParameters.add(QueryParams.Print.name(), PrintParam.Fields);
        aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(SearchResult.ALL_FIELDS));
        aciParameters.add(QueryParams.TotalResults.name(), true);
        aciParameters.add(QueryParams.XMLMeta.name(), true);

        if (searchRequest.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), DocumentsService.HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), DocumentsService.HIGHLIGHT_END_TAG);
        }
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

    protected String formatDate(final ReadableInstant date) {
        return date == null ? null : DateTimeFormat.forPattern(IDOL_DATE_PARAMETER_FORMAT).print(date);
    }
}

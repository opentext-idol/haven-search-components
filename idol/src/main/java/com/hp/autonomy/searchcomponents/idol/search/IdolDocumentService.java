/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SearchResult;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.Spelling;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class IdolDocumentService implements DocumentsService<String, SearchResult, AciErrorException> {
    private static final int MAX_SIMILAR_DOCUMENTS = 3;
    private static final Pattern SPELLING_SEPARATOR_PATTERN = Pattern.compile(", ");

    protected final ConfigService<? extends HavenSearchCapable> configService;
    protected final HavenSearchAciParameterHandler parameterHandler;
    protected final AciService contentAciService;
    protected final AciService qmsAciService;
    protected final Processor<QueryResponseData> queryResponseProcessor;
    protected final Processor<SuggestResponseData> suggestResponseProcessor;

    public IdolDocumentService(final ConfigService<? extends HavenSearchCapable> configService, final HavenSearchAciParameterHandler parameterHandler, final AciService contentAciService, final AciService qmsAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.configService = configService;
        this.parameterHandler = parameterHandler;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;

        queryResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(QueryResponseData.class);
        suggestResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(SuggestResponseData.class);
    }

    @Override
    public Documents<SearchResult> queryTextIndex(final SearchRequest<String> searchRequest) throws AciErrorException {
        final SearchRequest.QueryType queryType = searchRequest.getQueryType();
        final boolean useQms = qmsEnabled() && queryType != SearchRequest.QueryType.RAW;
        return queryTextIndex(useQms ? qmsAciService : contentAciService, searchRequest, queryType == SearchRequest.QueryType.PROMOTIONS);
    }

    private boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation().isEnabled();
    }

    @Override
    public Documents<SearchResult> queryTextIndexForPromotions(final SearchRequest<String> searchRequest) throws AciErrorException {
        return qmsEnabled() ? queryTextIndex(qmsAciService, searchRequest, true) : new Documents<>(Collections.<SearchResult>emptyList(), 0, null, null, null);
    }

    private Documents<SearchResult> queryTextIndex(final AciService aciService, final SearchRequest<String> searchRequest, final boolean promotions) {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());

        parameterHandler.addSearchRestrictions(aciParameters, searchRequest.getQueryRestrictions());
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        parameterHandler.addQmsParameters(aciParameters, searchRequest.getQueryRestrictions());

        if (searchRequest.isAutoCorrect()) {
            aciParameters.add(QueryParams.SpellCheck.name(), true);
        }

        if (promotions) {
            aciParameters.add(QmsQueryParams.Promotions.name(), true);
        }

        return executeQuery(aciService, aciParameters, searchRequest.isAutoCorrect());
    }

    protected Documents<SearchResult> executeQuery(final AciService aciService, final AciParameters aciParameters, final boolean autoCorrect) {
        final QueryResponseData responseData = aciService.executeAction(aciParameters, queryResponseProcessor);
        final List<Hit> hits = responseData.getHit();

        final String spellingQuery = responseData.getSpellingquery();

        // If IDOL has a spelling suggestion, retry query for auto correct
        final Documents<SearchResult> documents;
        if (autoCorrect && spellingQuery != null) {
            documents = rerunQueryWithAdjustedSpelling(aciService, aciParameters, responseData, spellingQuery);
        } else {
            final List<SearchResult> results = parseQueryHits(hits);
            documents = new Documents<>(results, responseData.getTotalhits(), null, null, null);
        }

        return documents;
    }

    protected Documents<SearchResult> rerunQueryWithAdjustedSpelling(final AciService aciService, final AciParameters aciParameters, final QueryResponseData responseData, final String spellingQuery) {
        final AciParameters correctedParameters = new AciParameters(aciParameters);
        correctedParameters.put(QueryParams.Text.name(), spellingQuery);

        final Documents<SearchResult> correctedDocuments = executeQuery(aciService, correctedParameters, false);

        final Spelling spelling = new Spelling(Arrays.asList(SPELLING_SEPARATOR_PATTERN.split(responseData.getSpelling())), spellingQuery, aciParameters.get(QueryParams.Text.name()));

        return new Documents<>(correctedDocuments.getDocuments(), correctedDocuments.getTotalResults(), correctedDocuments.getExpandedQuery(), null, spelling);
    }

    @Override
    public List<SearchResult> findSimilar(final Set<String> indexes, final String reference) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Suggest.name());
        aciParameters.add(SuggestParams.Reference.name(), new Reference(reference));
        if (!indexes.isEmpty()) {
            aciParameters.add(SuggestParams.DatabaseMatch.name(), new Databases(indexes));
        }
        aciParameters.add(SuggestParams.Print.name(), PrintParam.None);
        aciParameters.add(SuggestParams.MaxResults.name(), MAX_SIMILAR_DOCUMENTS);
        aciParameters.add(SuggestParams.Summary.name(), SummaryParam.Concept);

        final SuggestResponseData responseData = contentAciService.executeAction(aciParameters, suggestResponseProcessor);
        final List<Hit> hits = responseData.getHit();
        return parseQueryHits(hits);
    }

    protected List<SearchResult> parseQueryHits(final Collection<Hit> hits) {
        final List<SearchResult> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final SearchResult.Builder searchResultBuilder = new SearchResult.Builder()
                    .setReference(hit.getReference())
                    .setIndex(hit.getDatabase())
                    .setTitle(hit.getTitle())
                    .setSummary(hit.getSummary())
                    .setDate(hit.getDatestring())
                    .setWeight(hit.getWeight())
                    .setPromotionName(hit.getPromotionname());

            final DocContent content = hit.getContent();
            if (content != null) {
                final Element docContent = (Element) content.getContent().get(0);
                searchResultBuilder
                        .setContentType(parseFields(docContent, SearchResult.CONTENT_TYPE_FIELD))
                        .setUrl(parseFields(docContent, SearchResult.URL_FIELD))
                        .setAuthors(parseFields(docContent, SearchResult.AUTHOR_FIELD))
                        .setCategories(parseFields(docContent, SearchResult.CATEGORY_FIELD))
                        .setDateCreated(parseFields(docContent, SearchResult.DATE_CREATED_FIELD))
                        .setCreatedDate(parseFields(docContent, SearchResult.CREATED_DATE_FIELD))
                        .setModifiedDate(parseFields(docContent, SearchResult.MODIFIED_DATE_FIELD))
                        .setDateModified(parseFields(docContent, SearchResult.DATE_MODIFIED_FIELD))
                        .setQmsId(parseFields(docContent, SearchResult.QMS_ID_FIELD))
                        .setInjectedPromotion(parseFields(docContent, SearchResult.INJECTED_PROMOTION_FIELD));
            }
            results.add(searchResultBuilder.build());
        }
        return results;
    }

    private List<String> parseFields(final Element node, final String fieldName) {
        final NodeList childNodes = node.getElementsByTagName(fieldName.toUpperCase());
        final int length = childNodes.getLength();
        final List<String> values = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node childNode = childNodes.item(i);
            values.add(childNode.getFirstChild().getNodeValue());
        }

        return values;
    }
}

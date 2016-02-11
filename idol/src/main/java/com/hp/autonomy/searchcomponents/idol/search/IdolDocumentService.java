/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.aci.content.database.Databases;
import com.hp.autonomy.aci.content.identifier.reference.Reference;
import com.hp.autonomy.aci.content.identifier.reference.ReferencesBuilder;
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import com.hp.autonomy.searchcomponents.core.search.AciSearchRequest;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequest;
import com.hp.autonomy.searchcomponents.core.search.GetContentRequestIndex;
import com.hp.autonomy.searchcomponents.core.search.PromotionCategory;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.SearchRequest;
import com.hp.autonomy.searchcomponents.core.search.SuggestRequest;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.searchcomponents.idol.databases.IdolDatabasesRequest;
import com.hp.autonomy.types.idol.Database;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.Spelling;
import com.hp.autonomy.types.requests.Warnings;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.actions.query.params.QmsQueryParams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class IdolDocumentService implements DocumentsService<String, IdolSearchResult, AciErrorException> {
    static final String MISSING_DATABASE_WARNING = "At least one of the databases provided in the query does not exist";

    private static final Pattern SPELLING_SEPARATOR_PATTERN = Pattern.compile(", ");
    private static final String GET_CONTENT_QUERY_TEXT = "*";

    protected final ConfigService<? extends HavenSearchCapable> configService;
    protected final HavenSearchAciParameterHandler parameterHandler;
    protected final AciService contentAciService;
    protected final AciService qmsAciService;
    protected final Processor<QueryResponseData> queryResponseProcessor;
    protected final Processor<SuggestResponseData> suggestResponseProcessor;
    private final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public IdolDocumentService(final ConfigService<? extends HavenSearchCapable> configService, final HavenSearchAciParameterHandler parameterHandler, final AciService contentAciService, final AciService qmsAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory, final DatabasesService<Database, IdolDatabasesRequest, AciErrorException> databasesService) {
        this.configService = configService;
        this.parameterHandler = parameterHandler;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;
        this.databasesService = databasesService;

        queryResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(QueryResponseData.class);
        suggestResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(SuggestResponseData.class);
    }

    @Override
    public Documents<IdolSearchResult> queryTextIndex(final SearchRequest<String> searchRequest) throws AciErrorException {
        final SearchRequest.QueryType queryType = searchRequest.getQueryType();
        final boolean useQms = qmsEnabled() && queryType != SearchRequest.QueryType.RAW;
        return queryTextIndex(useQms ? qmsAciService : contentAciService, searchRequest, queryType == SearchRequest.QueryType.PROMOTIONS);
    }

    private boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation().isEnabled();
    }

    @Override
    public Documents<IdolSearchResult> queryTextIndexForPromotions(final SearchRequest<String> searchRequest) throws AciErrorException {
        return qmsEnabled() ? queryTextIndex(qmsAciService, searchRequest, true) : new Documents<>(Collections.<IdolSearchResult>emptyList(), 0, null, null, null, null);
    }

    private Documents<IdolSearchResult> queryTextIndex(final AciService aciService, final SearchRequest<String> searchRequest, final boolean promotions) {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());

        parameterHandler.addSearchRestrictions(aciParameters, searchRequest.getQueryRestrictions());
        parameterHandler.addSearchOutputParameters(aciParameters, searchRequest);
        if (searchRequest.getQueryType() != SearchRequest.QueryType.RAW) {
            parameterHandler.addQmsParameters(aciParameters, searchRequest.getQueryRestrictions());
        }

        if (searchRequest.isAutoCorrect()) {
            aciParameters.add(QueryParams.SpellCheck.name(), true);
        }

        if (promotions) {
            aciParameters.add(QmsQueryParams.Promotions.name(), true);
        }

        final QueryResponseData responseData = executeQuery(aciService, aciParameters);
        return parseResults(aciService, searchRequest, aciParameters, responseData);
    }

    @SuppressWarnings("TypeMayBeWeakened")
    protected QueryResponseData executeQuery(final AciService aciService, final AciParameters aciParameters) {
        return aciService.executeAction(aciParameters, queryResponseProcessor);
    }

    private Documents<IdolSearchResult> parseResults(final AciService aciService, final AciSearchRequest<String> searchRequest, final AciParameters aciParameters, final QueryResponseData responseData) {
        final List<Hit> hits = responseData.getHit();

        final Warnings warnings = parseWarnings(searchRequest, aciParameters, responseData);

        final String spellingQuery = responseData.getSpellingquery();

        // If IDOL has a spelling suggestion, retry query for auto correct
        final Documents<IdolSearchResult> documents;
        if (spellingQuery != null) {
            documents = rerunQueryWithAdjustedSpelling(aciService, aciParameters, responseData, spellingQuery, warnings);
        } else {
            final List<IdolSearchResult> results = parseQueryHits(hits);
            documents = new Documents<>(results, responseData.getTotalhits(), null, null, null, warnings);
        }

        return documents;
    }

    protected Warnings parseWarnings(final AciSearchRequest<String> searchRequest, final AciParameters aciParameters, final QueryResponseData responseData) {
        Warnings warnings = null;
        for (final String warning : responseData.getWarning()) {
            if (MISSING_DATABASE_WARNING.equals(warning.trim())) {
                final Set<Database> updatedDatabases = databasesService.getDatabases(new IdolDatabasesRequest());
                final List<String> oldQueryRestrictionDatabases = searchRequest.getQueryRestrictions().getDatabases();
                final Set<String> badDatabases = new LinkedHashSet<>(oldQueryRestrictionDatabases);
                for (final Database database : updatedDatabases) {
                    final String databaseName = database.getName();
                    badDatabases.remove(databaseName);
                }

                warnings = new Warnings(badDatabases);

                final Set<String> newQueryRestrictionDatabases = new LinkedHashSet<>(oldQueryRestrictionDatabases);
                newQueryRestrictionDatabases.removeAll(badDatabases);
                aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(newQueryRestrictionDatabases));
            }
        }
        return warnings;
    }

    protected Documents<IdolSearchResult> rerunQueryWithAdjustedSpelling(final AciService aciService, final AciParameters aciParameters, final QueryResponseData responseData, final String spellingQuery, final Warnings warnings) {
        final AciParameters correctedParameters = new AciParameters(aciParameters);
        correctedParameters.put(QueryParams.Text.name(), spellingQuery);

        final QueryResponseData correctedResponseData = executeQuery(aciService, aciParameters);
        final List<IdolSearchResult> correctedResults = parseQueryHits(correctedResponseData.getHit());

        final Spelling spelling = new Spelling(Arrays.asList(SPELLING_SEPARATOR_PATTERN.split(responseData.getSpelling())), spellingQuery, aciParameters.get(QueryParams.Text.name()));

        return new Documents<>(correctedResults, correctedResponseData.getTotalhits(), null, null, spelling, warnings);
    }

    @Override
    public Documents<IdolSearchResult> findSimilar(final SuggestRequest<String> suggestRequest) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Suggest.name());
        aciParameters.add(SuggestParams.Reference.name(), new Reference(suggestRequest.getReference()));

        parameterHandler.addSearchRestrictions(aciParameters, suggestRequest.getQueryRestrictions());
        parameterHandler.addSearchOutputParameters(aciParameters, suggestRequest);

        final SuggestResponseData responseData = contentAciService.executeAction(aciParameters, suggestResponseProcessor);
        final List<Hit> hits = responseData.getHit();
        return new Documents<>(parseQueryHits(hits), responseData.getTotalhits(), null, null, null, null);
    }

    @Override
    public List<IdolSearchResult> getDocumentContent(final GetContentRequest<String> request) throws AciErrorException {
        final List<IdolSearchResult> results = new ArrayList<>(request.getIndexesAndReferences().size());

        for (final GetContentRequestIndex<String> indexAndReferences : request.getIndexesAndReferences()) {

            // We use Query and not GetContent here so we can use Combine=simple to ensure returned references are unique
            final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
            aciParameters.add(QueryParams.MatchReference.name(), new ReferencesBuilder(indexAndReferences.getReferences()));
            aciParameters.add(QueryParams.Summary.name(), SummaryParam.Concept);
            aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
            aciParameters.add(QueryParams.Text.name(), GET_CONTENT_QUERY_TEXT);
            aciParameters.add(QueryParams.MaxResults.name(), 1);
            aciParameters.add(QueryParams.AnyLanguage.name(), true);
            aciParameters.add(QueryParams.Print.name(), PrintParam.Fields);
            aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(IdolSearchResult.ALL_FIELDS));
            aciParameters.add(QueryParams.XMLMeta.name(), true);

            if (indexAndReferences.getIndex() != null) {
                aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(indexAndReferences.getIndex()));
            }

            final QueryResponseData responseData = contentAciService.executeAction(aciParameters, queryResponseProcessor);
            final List<Hit> hits = responseData.getHit();
            results.addAll(parseQueryHits(hits));
        }

        return results;
    }

    @Override
    public String getStateToken(final QueryRestrictions<String> queryRestrictions, final int maxResults) throws AciErrorException {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
        aciParameters.add(QueryParams.StoreState.name(), true);
        parameterHandler.addSearchRestrictions(aciParameters, queryRestrictions);

        aciParameters.add(SuggestParams.Print.name(), PrintParam.NoResults);
        aciParameters.add(SuggestParams.MaxResults.name(), maxResults);

        // No promotion or QMS related parameters added; at the time of writing, QMS does not fully support stored state

        final QueryResponseData responseData = contentAciService.executeAction(aciParameters, queryResponseProcessor);
        return responseData.getState();
    }

    protected List<IdolSearchResult> parseQueryHits(final Collection<Hit> hits) {
        final List<IdolSearchResult> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final IdolSearchResult.Builder searchResultBuilder = new IdolSearchResult.Builder()
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
                        .setContentType(parseField(docContent, IdolSearchResult.CONTENT_TYPE_FIELD))
                        .setUrl(parseField(docContent, IdolSearchResult.URL_FIELD))
                        .setAuthors(parseFields(docContent, IdolSearchResult.AUTHOR_FIELD))
                        .setCategories(parseFields(docContent, IdolSearchResult.CATEGORY_FIELD))
                        .setDateCreated(parseDateField(docContent, Arrays.asList(IdolSearchResult.DATE_CREATED_FIELD, IdolSearchResult.CREATED_DATE_FIELD)))
                        .setDateModified(parseDateField(docContent, Arrays.asList(IdolSearchResult.MODIFIED_DATE_FIELD, IdolSearchResult.DATE_MODIFIED_FIELD)))
                        .setQmsId(parseField(docContent, IdolSearchResult.QMS_ID_FIELD))
                        .setPromotionCategory(determinePromotionCategory(docContent, hit.getPromotionname(), hit.getDatabase()));
            }
            results.add(searchResultBuilder.build());
        }
        return results;
    }

    private PromotionCategory determinePromotionCategory(final Element docContent, final CharSequence promotionName, final CharSequence database) {
        final PromotionCategory promotionCategory;
        if (Boolean.parseBoolean(parseField(docContent, IdolSearchResult.INJECTED_PROMOTION_FIELD))) {
            promotionCategory = PromotionCategory.CARDINAL_PLACEMENT;
        } else if (StringUtils.isNotEmpty(promotionName)) {
            // If the database isn't found, then assume it is a static content promotion
            promotionCategory = StringUtils.isNotEmpty(database) ? PromotionCategory.SPOTLIGHT : PromotionCategory.STATIC_CONTENT_PROMOTION;
        } else {
            promotionCategory = PromotionCategory.NONE;
        }

        return promotionCategory;
    }

    private List<String> parseFields(final Element node, final Iterable<String> fieldNames) {
        final List<String> values = new ArrayList<>();

        final Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext() && values.isEmpty()) {
            values.addAll(parseFields(node, iterator.next()));
        }

        return values;
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

    private String parseField(final Element node, final Iterable<String> fieldNames) {
        final List<String> fields = parseFields(node, fieldNames);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private String parseField(final Element node, final String fieldName) {
        final List<String> fields = parseFields(node, fieldName);
        return CollectionUtils.isNotEmpty(fields) ? fields.get(0) : null;
    }

    private DateTime parseDateField(final Element node, final Iterable<String> fieldNames) {
        final String stringField = parseField(node, fieldNames);
        return stringField != null ? DateTime.parse(stringField) : null;
    }
}

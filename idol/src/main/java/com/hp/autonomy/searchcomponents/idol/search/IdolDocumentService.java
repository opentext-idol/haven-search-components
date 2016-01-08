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
import com.hp.autonomy.aci.content.printfields.PrintFields;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.search.DocumentsService;
import com.hp.autonomy.searchcomponents.core.search.HavenDocument;
import com.hp.autonomy.searchcomponents.core.search.HavenQueryParams;
import com.hp.autonomy.searchcomponents.idol.configuration.HavenSearchCapable;
import com.hp.autonomy.types.idol.DocContent;
import com.hp.autonomy.types.idol.Hit;
import com.hp.autonomy.types.idol.QueryResponseData;
import com.hp.autonomy.types.idol.SuggestResponseData;
import com.hp.autonomy.types.requests.Documents;
import com.hp.autonomy.types.requests.idol.actions.query.QueryActions;
import com.hp.autonomy.types.requests.idol.actions.query.params.CombineParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.HighlightParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.PrintParam;
import com.hp.autonomy.types.requests.idol.actions.query.params.QueryParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SuggestParams;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.qms.QmsActionParams;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class IdolDocumentService implements DocumentsService<String, HavenDocument, AciErrorException> {
    static final String MISSING_RULE_ERROR = "missing rule";
    static final String INVALID_RULE_ERROR = "invalid rule";
    private static final String IDOL_DATE_PARAMETER_FORMAT = "HH:mm:ss dd/MM/yyyy";
    private static final int MAX_SIMILAR_DOCUMENTS = 3;

    private final ConfigService<? extends HavenSearchCapable> configService;
    private final AciService contentAciService;
    private final AciService qmsAciService;
    private final Processor<QueryResponseData> queryResponseProcessor;
    private final Processor<SuggestResponseData> suggestResponseProcessor;

    @Autowired
    public IdolDocumentService(final ConfigService<? extends HavenSearchCapable> configService, final AciService contentAciService, final AciService qmsAciService, final AciResponseJaxbProcessorFactory aciResponseProcessorFactory) {
        this.configService = configService;
        this.contentAciService = contentAciService;
        this.qmsAciService = qmsAciService;

        queryResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(QueryResponseData.class);
        suggestResponseProcessor = aciResponseProcessorFactory.createAciResponseProcessor(SuggestResponseData.class);
    }

    @Override
    public Documents<HavenDocument> queryTextIndex(final HavenQueryParams<String> HavenQueryParams) throws AciErrorException {
        return queryTextIndex(qmsEnabled() ? qmsAciService : contentAciService, HavenQueryParams, false);
    }

    private boolean qmsEnabled() {
        return configService.getConfig().getQueryManipulation().isEnabled();
    }

    @Override
    public Documents<HavenDocument> queryTextIndexForPromotions(final HavenQueryParams<String> HavenQueryParams) throws AciErrorException {
        return qmsEnabled() ? queryTextIndex(qmsAciService, HavenQueryParams, true) : new Documents<>(Collections.<HavenDocument>emptyList(), 0, null);
    }

    private Documents<HavenDocument> queryTextIndex(final AciService aciService, final HavenQueryParams<String> HavenQueryParams, final boolean promotions) {
        final AciParameters aciParameters = new AciParameters(QueryActions.Query.name());
        aciParameters.add(QueryParams.Text.name(), HavenQueryParams.getText());
        aciParameters.add(QueryParams.MaxResults.name(), HavenQueryParams.getMaxResults());
        aciParameters.add(QueryParams.Summary.name(), SummaryParam.fromValue(HavenQueryParams.getSummary(), null));

        if (!promotions && !HavenQueryParams.getIndex().isEmpty()) {
            aciParameters.add(QueryParams.DatabaseMatch.name(), new Databases(HavenQueryParams.getIndex()));
        }

        aciParameters.add(QueryParams.Combine.name(), CombineParam.Simple);
        aciParameters.add(QueryParams.Predict.name(), false);
        aciParameters.add(QueryParams.FieldText.name(), HavenQueryParams.getFieldText());
        aciParameters.add(QueryParams.Sort.name(), HavenQueryParams.getSort());
        aciParameters.add(QueryParams.MinDate.name(), formatDate(HavenQueryParams.getMinDate()));
        aciParameters.add(QueryParams.MaxDate.name(), formatDate(HavenQueryParams.getMaxDate()));
        aciParameters.add(QueryParams.Print.name(), PrintParam.Fields);
        aciParameters.add(QueryParams.PrintFields.name(), new PrintFields(HavenDocument.ALL_FIELDS));
        aciParameters.add(QueryParams.XMLMeta.name(), true);
        aciParameters.add(QueryParams.AnyLanguage.name(), true);

        if (HavenQueryParams.isHighlight()) {
            aciParameters.add(QueryParams.Highlight.name(), HighlightParam.SummaryTerms);
            aciParameters.add(QueryParams.StartTag.name(), HIGHLIGHT_START_TAG);
            aciParameters.add(QueryParams.EndTag.name(), HIGHLIGHT_END_TAG);
        }

        aciParameters.add(QmsActionParams.Blacklist.name(), configService.getConfig().getQueryManipulation().getBlacklist());
        aciParameters.add(QmsActionParams.ExpandQuery.name(), configService.getConfig().getQueryManipulation().getExpandQuery());

        if (promotions) {
            aciParameters.add(QmsActionParams.Promotions.name(), true);
        }

        return executeQuery(aciService, aciParameters);
    }

    private Documents<HavenDocument> executeQuery(final AciService aciService, final AciParameters aciParameters) {
        QueryResponseData responseData;
        try {
            responseData = aciService.executeAction(aciParameters, queryResponseProcessor);
        } catch (final AciErrorException e) {
            final String errorString = e.getErrorString();
            if (MISSING_RULE_ERROR.equals(errorString) || INVALID_RULE_ERROR.equals(errorString)) {
                aciParameters.remove(QmsActionParams.Blacklist.name());
                responseData = aciService.executeAction(aciParameters, queryResponseProcessor);
            }
            else {
                throw e;
            }
        }

        final List<Hit> hits = responseData.getHit();
        final List<HavenDocument> results = parseQueryHits(hits);
        return new Documents<>(results, responseData.getTotalhits(), null);
    }

    @Override
    public List<HavenDocument> findSimilar(final Set<String> indexes, final String reference) throws AciErrorException {
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

    private String formatDate(final ReadableInstant date) {
        return date == null ? null : DateTimeFormat.forPattern(IDOL_DATE_PARAMETER_FORMAT).print(date);
    }

    private List<HavenDocument> parseQueryHits(final Collection<Hit> hits) {
        final List<HavenDocument> results = new ArrayList<>(hits.size());
        for (final Hit hit : hits) {
            final HavenDocument.Builder HavenDocumentBuilder = new HavenDocument.Builder()
                    .setReference(hit.getReference())
                    .setIndex(hit.getDatabase())
                    .setTitle(hit.getTitle())
                    .setSummary(hit.getSummary())
                    .setDate(hit.getDatestring());

            final DocContent content = hit.getContent();
            if (content != null) {
                final Element docContent = (Element) content.getContent().get(0);
                HavenDocumentBuilder
                        .setContentType(parseFields(docContent, HavenDocument.CONTENT_TYPE_FIELD))
                        .setUrl(parseFields(docContent, HavenDocument.URL_FIELD))
                        .setAuthors(parseFields(docContent, HavenDocument.AUTHOR_FIELD))
                        .setCategories(parseFields(docContent, HavenDocument.CATEGORY_FIELD))
                        .setDateCreated(parseFields(docContent, HavenDocument.DATE_CREATED_FIELD))
                        .setCreatedDate(parseFields(docContent, HavenDocument.CREATED_DATE_FIELD))
                        .setModifiedDate(parseFields(docContent, HavenDocument.MODIFIED_DATE_FIELD))
                        .setDateModified(parseFields(docContent, HavenDocument.DATE_MODIFIED_FIELD));
            }
            results.add(HavenDocumentBuilder.build());
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

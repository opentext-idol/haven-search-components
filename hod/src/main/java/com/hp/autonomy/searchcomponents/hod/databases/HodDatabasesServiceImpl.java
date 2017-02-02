/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.*;
import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.core.databases.DatabasesService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HoD databases service implementation: retrieves private and public index information by querying HoD for content resources
 */
@Service(DatabasesService.DATABASES_SERVICE_BEAN_NAME)
class HodDatabasesServiceImpl implements HodDatabasesService {
    private final ResourcesService resourcesService;
    private final IndexFlavourService indexFlavourService;

    HodDatabasesServiceImpl(
            final ResourcesService resourcesService,
            final IndexFlavourService indexFlavourService
    ) {
        this.resourcesService = resourcesService;
        this.indexFlavourService = indexFlavourService;
    }

    @Override
    public Set<Database> getDatabases(final HodDatabasesRequest request) throws HodErrorException {
        final ListResourcesRequestBuilder builder = new ListResourcesRequestBuilder()
                .setTypes(Collections.singleton(ResourceType.TEXT_INDEX));

        final List<ResourceDetails> resources = resourcesService.list(builder);
        final Map<Boolean, List<ResourceDetails>> partitioned = resources.stream().collect(Collectors.partitioningBy(isPublicIndex));
        final Collection<FlavouredResource> flavouredPrivateResources = fetchFlavours(partitioned.get(false));

        final Stream<Database> privateDatabases = flavouredPrivateResources.stream()
                .filter(isContentFlavour)
                .map(databaseForResource.compose(FlavouredResource::getDetails));

        final Stream<Database> publicDatabases = request.isPublicIndexesEnabled()
                ? partitioned.get(true).stream().map(databaseForResource)
                : Stream.empty();

        return Stream.concat(privateDatabases, publicDatabases)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Collection<FlavouredResource> fetchFlavours(final Iterable<ResourceDetails> detailsList) throws HodErrorException {
        final Collection<FlavouredResource> flavouredResources = new LinkedList<>();

        for (final ResourceDetails details : detailsList) {
            final IndexFlavor indexFlavour = indexFlavourService.getIndexFlavour(details.getResource().getResourceUuid());
            flavouredResources.add(new FlavouredResource(details, indexFlavour));
        }

        return flavouredResources;
    }

    @Data
    private static class FlavouredResource {
        private final ResourceDetails details;
        private final IndexFlavor flavour;
    }

    private static final Predicate<FlavouredResource> isContentFlavour = flavouredResource -> CONTENT_FLAVOURS.contains(flavouredResource.getFlavour());
    private static final Predicate<ResourceDetails> isPublicIndex = details -> ResourceName.PUBLIC_INDEXES_DOMAIN.equals(details.getResource().getDomain());

    private static final Function<ResourceDetails, Database> databaseForResource = details -> {
        return Database.builder()
                .name(details.getResource().getName())
                .domain(details.getResource().getDomain())
                .displayName(details.getDisplayName())
                .isPublic(isPublicIndex.test(details))
                .build();
    };
}

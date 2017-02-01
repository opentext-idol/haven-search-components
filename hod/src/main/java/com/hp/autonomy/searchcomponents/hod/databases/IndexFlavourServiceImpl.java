package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.ResourceUuid;
import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatus;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatusService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.searchcomponents.hod.caching.HodCacheNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service(IndexFlavourService.INDEX_FLAVOUR_SERVICE_BEAN_NAME)
class IndexFlavourServiceImpl implements IndexFlavourService {
    private final TextIndexStatusService textIndexStatusService;

    @Autowired
    public IndexFlavourServiceImpl(final TextIndexStatusService textIndexStatusService) {
        this.textIndexStatusService = textIndexStatusService;
    }

    @Override
    @Cacheable(HodCacheNames.INDEX_FLAVOUR)
    public IndexFlavor getIndexFlavour(final ResourceUuid resourceUuid) throws HodErrorException {
        final TextIndexStatus indexStatus = textIndexStatusService.getIndexStatus(resourceUuid);
        return indexStatus.getFlavor();
    }
}

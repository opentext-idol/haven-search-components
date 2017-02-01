package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.ResourceUuid;
import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.error.HodErrorException;

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface IndexFlavourService {

    String INDEX_FLAVOUR_SERVICE_BEAN_NAME = "indexFlavourService";

    IndexFlavor getIndexFlavour(ResourceUuid resourceUuid) throws HodErrorException;

}

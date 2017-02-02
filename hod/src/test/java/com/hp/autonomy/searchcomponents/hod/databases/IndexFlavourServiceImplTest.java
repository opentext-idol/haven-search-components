package com.hp.autonomy.searchcomponents.hod.databases;

import com.hp.autonomy.hod.client.api.resource.ResourceUuid;
import com.hp.autonomy.hod.client.api.textindex.IndexFlavor;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatus;
import com.hp.autonomy.hod.client.api.textindex.status.TextIndexStatusService;
import com.hp.autonomy.hod.client.error.HodErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IndexFlavourServiceImplTest {
    @Mock
    private TextIndexStatusService textIndexStatusService;

    private IndexFlavourService indexFlavourService;

    @Before
    public void setUp() {
        indexFlavourService = new IndexFlavourServiceImpl(textIndexStatusService);
    }

    @Test
    public void getIndexFlavour() throws HodErrorException {
        final ResourceUuid resourceUuid = new ResourceUuid(UUID.randomUUID());
        final TextIndexStatus indexStatus = TextIndexStatus.builder().flavor(IndexFlavor.CUSTOM_FIELDS).build();
        when(textIndexStatusService.getIndexStatus(resourceUuid)).thenReturn(indexStatus);

        final IndexFlavor output = indexFlavourService.getIndexFlavour(resourceUuid);
        assertThat(output, is(IndexFlavor.CUSTOM_FIELDS));
    }
}
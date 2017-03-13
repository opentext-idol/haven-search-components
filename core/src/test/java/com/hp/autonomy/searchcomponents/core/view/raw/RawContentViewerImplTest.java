package com.hp.autonomy.searchcomponents.core.view.raw;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RawContentViewerImplTest {
    private RawContentViewer rawContentViewer;

    @Before
    public void setUp() {
        rawContentViewer = new RawContentViewerImpl();
    }

    @Test
    public void rendersTitleAndContent() throws IOException {
        final RawDocument rawDocument = RawDocument.builder()
                .reference("the_reference")
                .title("The Title")
                .content("Line 1\n&Line 2")
                .build();

        final InputStream inputStream = rawContentViewer.formatRawContent(rawDocument);
        final String output = IOUtils.toString(inputStream);
        assertThat(output, is("<h1>The Title</h1><p>Line 1<br>&amp;Line 2</p>"));
    }
}
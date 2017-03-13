package com.hp.autonomy.searchcomponents.core.search;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class DocumentTitleResolverTest {
    @Test
    public void prefersTitleIfPresent() {
        assertThat(DocumentTitleResolver.resolveTitle("The Title", "/home/trevor/doc.pdf"), is("The Title"));
    }

    @Test
    public void nullIfBothArgumentsNull() {
        assertThat(DocumentTitleResolver.resolveTitle(null, null), is(nullValue()));
    }

    @Test
    public void nullIfTitleNullAndReferenceEmpty() {
        assertThat(DocumentTitleResolver.resolveTitle(null, ""), is(nullValue()));
    }

    @Test
    public void returnsReferenceIfTitleNull() {
        assertThat(DocumentTitleResolver.resolveTitle(null, "my_reference"), is("my_reference"));
    }

    @Test
    public void returnsReferenceIfTitleEmpty() {
        assertThat(DocumentTitleResolver.resolveTitle("", "my_reference"), is("my_reference"));
    }

    @Test
    public void readsFileNameFromReference() {
        assertThat(DocumentTitleResolver.resolveTitle(null, "/home/trevor/doc.pdf"), is("doc.pdf"));
    }
}

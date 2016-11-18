/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import com.hp.autonomy.searchcomponents.core.requests.ResponseObjectTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public abstract class SearchResultTest extends ResponseObjectTest<SearchResult, SearchResult.SearchResultBuilder> {
    @Test
    public void testWindowsFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = object.toBuilder().reference("C:\\Documents\\file.txt").title(null).build();
        assertThat(searchResult.getTitle(), is("file.txt"));
    }

    @Test
    public void testUnixFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = object.toBuilder().reference("/home/user/another-file.txt").title(null).build();
        assertThat(searchResult.getTitle(), is("another-file.txt"));
    }

    @Test
    public void testWholeReferenceUsedIfReferenceEndsWithASlash() {
        final SearchResult searchResult = object.toBuilder().reference("http://example.com/main/").title(null).build();
        assertThat(searchResult.getTitle(), is("http://example.com/main/"));
    }

    @Test
    public void testWholeReferenceUseIfReferenceEndsWithASlashFollowedByWhitespace() {
        final SearchResult searchResult = object.toBuilder().reference("foo/   \n ").title(null).build();
        assertThat(searchResult.getTitle(), is("foo/   \n "));
    }

    @Override
    protected String toStringContent() {
        return "reference";
    }
}
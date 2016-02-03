/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public abstract class SearchResultTest {
    protected abstract SearchResult buildSearchResult(final String reference);

    @Test
    public void testWindowsFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = buildSearchResult("C:\\Documents\\file.txt");
        assertThat(searchResult.getTitle(), is("file.txt"));
    }

    @Test
    public void testUnixFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = buildSearchResult("/home/user/another-file.txt");
        assertThat(searchResult.getTitle(), is("another-file.txt"));
    }

    @Test
    public void testWholeReferenceUsedIfReferenceEndsWithASlash() {
        final SearchResult searchResult = buildSearchResult("http://example.com/main/");
        assertThat(searchResult.getTitle(), is("http://example.com/main/"));
    }

    @Test
    public void testWholeReferenceUseIfReferenceEndsWithASlashFollowedByWhitespace() {
        final SearchResult searchResult = buildSearchResult("foo/   \n ");
        assertThat(searchResult.getTitle(), is("foo/   \n "));
    }

}
/*
 * Copyright 2015-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.search;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SearchResultTest {

    @Test
    public void testWindowsFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = new SearchResult.Builder()
            .setReference("C:\\Documents\\file.txt")
            .build();

        assertThat(searchResult.getTitle(), is("file.txt"));
    }

    @Test
    public void testUnixFilePathReferenceUsedCorrectlyAsTitle() {
        final SearchResult searchResult = new SearchResult.Builder()
            .setReference("/home/user/another-file.txt")
            .build();

        assertThat(searchResult.getTitle(), is("another-file.txt"));
    }

    @Test
    public void testWholeReferenceUsedIfReferenceEndsWithASlash() {
        final SearchResult searchResult = new SearchResult.Builder()
            .setReference("http://example.com/main/")
            .build();

        assertThat(searchResult.getTitle(), is("http://example.com/main/"));
    }

    @Test
    public void testWholeReferenceUseIfReferenceEndsWithASlashFollowedByWhitespace() {
        final SearchResult searchResult = new SearchResult.Builder()
            .setReference("foo/   \n ")
            .build();

        assertThat(searchResult.getTitle(), is("foo/   \n "));
    }

}
package com.naihs.pagecompare.parsers.google;

import com.naihs.pagecompare.models.ItemContent;

/**
 * Content item implementation for google search result
 *
 *
 * @version        1.0., 16/12/29
 * @author         yexiang
 */
public class GoogleItemContentImpl extends ItemContent {

    /**
     * Constructs for google search result item
     *
     *
     * @param url url of search result item
     * @param title title text of search result item
     * @param content description text for search result item
     */
    public GoogleItemContentImpl(String url, String title, String content) {
        super(url, title, content);
    }

    @Override
    public String getKey() {
        return this.title;    // Use title as index
    }
}

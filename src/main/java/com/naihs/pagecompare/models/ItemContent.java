package com.naihs.pagecompare.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Parsed search result item class
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang01
 */
@Data
@AllArgsConstructor
public abstract class ItemContent {

    /** link url of search result item */
    protected String url;

    /** title text of search result item */
    protected String title;

    /** description text of search result item  (can be null) */
    protected String content;

    /**
     * create a key for current item in page container
     *
     *
     * @return key for current item
     */
    abstract public String getKey();
}


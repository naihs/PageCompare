package com.naihs.pagecompare.parsers.baidu;

import com.naihs.pagecompare.models.ItemContent;

/**
 * Content item implementation for baidu search result
 *
 *
 * @version        1.0., 16/12/29
 * @author         yexiang
 */
public class BaiduItemContentImpl extends ItemContent {

    /**
     * Constructs for baidu search result item
     *
     *
     * @param url url of search result item
     * @param title title text of search result item
     * @param content description text for search result item
     */
    public BaiduItemContentImpl(String url, String title, String content) {
        super(url, title, content);
    }

    @Override
    public String getKey() {
        return this.title;    // 使用title作为比较的基准key
    }
}


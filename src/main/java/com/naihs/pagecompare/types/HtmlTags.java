package com.naihs.pagecompare.types;

/**
 * HTML Tags enum
 */
public enum HtmlTags {
    H3("h3"), A("a"), HREF("href");

    /** tag name string in html content */
    private String tagStr;

    /**
     * Create default Tag name string
     *
     * @param tagStr tag name string in html content
     */
    HtmlTags(String tagStr) {
        this.tagStr = tagStr;
    }

    @Override
    public String toString() {
        return tagStr;
    }
}

package com.naihs.pagecompare.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Comparer abstract, all comparers extend this class
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
@Data
@AllArgsConstructor
public abstract class Comparer {

    /** comparer's name */
    protected String name;

    /**
     * Get similarity ration between 2 page content
     *
     *
     * @param leftContent parsed and waiting comparing page content
     * @param rightContent parsed and waiting comparing page content
     *
     * @return similarity ratio
     */
    abstract public float similarityRatio(PageContent leftContent, PageContent rightContent);
}


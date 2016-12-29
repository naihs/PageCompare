package com.naihs.pagecompare.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 比较器抽象
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
@Data
@AllArgsConstructor
public abstract class Comparer {

    /** 比较器名称 */
    protected String name;

    /**
     * 获取两个页面搜索结果的相似度
     *
     *
     * @param leftContent 页面搜索结果容器
     * @param rightContent 页面搜索结果容器
     *
     * @return 相似度
     */
    abstract public float similarityRatio(PageContent leftContent, PageContent rightContent);
}


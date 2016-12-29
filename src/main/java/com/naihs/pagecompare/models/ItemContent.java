package com.naihs.pagecompare.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 搜索结果小项抽象
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang01
 */
@Data
@AllArgsConstructor
public abstract class ItemContent {

    /** 单个搜索结果的目标url */
    protected String url;

    /** 单个搜索结果的标题 */
    protected String title;

    /** 单个搜索结果的描述（可能为空） */
    protected String content;

    /**
     * 获取搜索结果在页面容器中的标识
     *
     *
     * @return 搜索结果标识
     */
    abstract public String getKey();
}


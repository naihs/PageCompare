package com.naihs.pagecompare.parsers.baidu;

import com.naihs.pagecompare.models.ItemContent;

/**
 * Baidu页面搜索结果小项
 *
 *
 * @version        1.0., 16/12/29
 * @author         yexiang
 */
public class BaiduItemContentImpl extends ItemContent {

    /**
     * 构造器
     *
     *
     * @param url 单个搜索结果的目标url
     * @param title 单个搜索结果的标题
     * @param content 单个搜索结果的描述（可能为空）
     */
    public BaiduItemContentImpl(String url, String title, String content) {
        super(url, title, content);
    }

    @Override
    public String getKey() {
        return this.title;    // 使用title作为比较的基准key
    }
}


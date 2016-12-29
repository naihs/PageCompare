package com.naihs.pagecompare.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 页面容器
 *
 * 每个搜索请求的所有结果均使用该容器保存
 *
 * @version        1.0, 16/12/29
 * @author         yexiang
 */
@Data
public class PageContent {

    /** 搜索结果ma
     *  key :  搜索结果项标识
     *  value: 搜索结果项
     *  */
    private Map<String, ItemContent> pageItemsMap = new HashMap<>();

    /**
     * 加入一个搜索结果项
     *
     * @param item 搜索结果项
     */
    public void addItem(ItemContent item) {
        pageItemsMap.put(item.getKey(), item);
    }

    /**
     * 加入一组搜索结果
     *
     * @param items 搜索结果List
     */
    public void addItems(List<ItemContent> items) {
        items.forEach(this::addItem);
    }
}

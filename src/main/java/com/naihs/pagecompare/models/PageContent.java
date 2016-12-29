package com.naihs.pagecompare.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Page content container
 *
 * save all parsed items from parser
 *
 * @version        1.0, 16/12/29
 * @author         yexiang
 */
@Data
public class PageContent {

    /** parsed result container
     *  key :  from item.getKey()
     *  value: parsed item
     *  */
    private Map<String, ItemContent> pageItemsMap = new HashMap<>();

    /**
     * Add a item into container, key from item.getKey()
     *
     * @param item parsed item
     */
    public void addItem(ItemContent item) {
        pageItemsMap.put(item.getKey(), item);
    }

    /**
     * Add some parsed items, key from item.getKey()
     *
     * @param items item list
     */
    public void addItems(List<ItemContent> items) {
        items.forEach(this::addItem);
    }
}

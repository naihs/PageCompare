package com.naihs.pagecompare.comparer;

import java.util.HashSet;
import java.util.Map;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.parsers.baidu.UrlFinder;

import lombok.AllArgsConstructor;

/**
 * Comparer class for compare urls between 2 page content
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class UrlComparer extends Comparer {

    /** Url comparer name */
    private static final String NAME = "URL_COMPARE";

    /**
     * Constructs ...
     *
     */
    private UrlComparer() {
        super(NAME);
    }

    @Override
    public float similarityRatio(PageContent leftContent, PageContent rightContent) {
        if (leftContent.getPageItemsMap().isEmpty() || rightContent.getPageItemsMap().isEmpty()) {
            return 0.0f;
        }

        Map<String, ItemContent> baseContentMap;
        HashSet<String>          urlSet;

        // Use content map with max size as base content map
        if (leftContent.getPageItemsMap().size() > rightContent.getPageItemsMap().size()) {
            baseContentMap = rightContent.getPageItemsMap();
            urlSet         = new HashSet<>(leftContent.getPageItemsMap().size());
            leftContent.getPageItemsMap().values().forEach(item -> urlSet.add(item.getUrl()));
        } else {
            baseContentMap = leftContent.getPageItemsMap();
            urlSet         = new HashSet<>(rightContent.getPageItemsMap().size());
            rightContent.getPageItemsMap().values().forEach(item -> urlSet.add(item.getUrl()));
        }

        int baseCount = urlSet.size();
        int sameCount = 0;

        for (Map.Entry<String, ItemContent> entry : baseContentMap.entrySet()) {

            // todo: Some result's url is random (like zhidao.baidu.com ...), we need to get their real url
            if (urlSet.contains(entry.getValue().getUrl())) {
                sameCount++;
            }
        }

        return sameCount / (baseCount * 1.0f);
    }

    /**
     * Singleton getter
     *
     *
     * @return instance of url comparer
     */
    public static UrlComparer getComparer() {
        return ComparerHolder.INSTANCE;
    }

    /**
     * Singleton Holder
     */
    private static class ComparerHolder {
        private static UrlComparer INSTANCE = new UrlComparer();
    }
}


package com.naihs.pagecompare.comparer;

import java.util.HashSet;
import java.util.Map;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.parsers.baidu.UrlFinder;

import lombok.AllArgsConstructor;

/**
 * 比较两个页面搜索结果各个项的标题
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class UrlComparer extends Comparer {

    /** 比较器名称 */
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

            // todo: 有一些搜索结果的url是随机变化的（如百度知道），这些结果需要进行二次跳转才能知道真正地址
            if (urlSet.contains(entry.getValue().getUrl())) {
                sameCount++;
            }
        }

        return sameCount / (baseCount * 1.0f);
    }

    /**
     * 获取比较器实例
     *
     *
     * @return 描述比较器实例
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


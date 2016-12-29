package com.naihs.pagecompare.comparer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;

/**
 * 比较两个页面搜索结果各个项的描述
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class DescriptionComparer extends Comparer {

    /** 比较器名称 */
    private static final String NAME = "CONTENT_COMPARE";

    /** 需排除的字符列表 */
    private static final List<String> IGNORED_CHARS = Arrays.asList("\\s*", "_");

    /** 比较长度n，取前n个字符作为比较依据 */
    private static final int COMPARE_LENGTH = 10;

    /**
     * Constructs ...
     *
     */
    private DescriptionComparer() {
        super(NAME);
    }

    @Override
    public float similarityRatio(PageContent leftContent, PageContent rightContent) {
        if (leftContent.getPageItemsMap().isEmpty() || rightContent.getPageItemsMap().isEmpty()) {
            return 0.0f;
        }

        Map<String, ItemContent> baseContentMap;
        HashSet<String>          urlSet;

        if (leftContent.getPageItemsMap().size() > rightContent.getPageItemsMap().size()) {
            baseContentMap = rightContent.getPageItemsMap();
            urlSet         = new HashSet<>(leftContent.getPageItemsMap().size());
            leftContent.getPageItemsMap().values().forEach(item -> urlSet.add(getCompareTargetStr(item.getContent())));
        } else {
            baseContentMap = leftContent.getPageItemsMap();
            urlSet         = new HashSet<>(rightContent.getPageItemsMap().size());
            rightContent.getPageItemsMap().values().forEach(item -> urlSet.add(getCompareTargetStr(item.getContent())));
        }

        urlSet.remove(null);    // 移除描述为空的项

        int baseCount = urlSet.size();
        int sameCount = 0;

        for (Map.Entry<String, ItemContent> entry : baseContentMap.entrySet()) {

            // 不比较为空的项
            if (null == entry.getValue().getContent()) {
                continue;
            }

            if (urlSet.contains(getCompareTargetStr(entry.getValue().getContent()))) {
                sameCount++;
            }
        }

        return sameCount / (baseCount * 1.0f);
    }

    /**
     * 预处理搜索结果的描述
     *
     *
     * @param sourceStr 搜索结果描述
     *
     * @return 预处理后的搜索结果描述
     */
    private static String getCompareTargetStr(String sourceStr) {
        if (null == sourceStr) {
            return null;
        }

        // 截取过长的描述
        if (sourceStr.length() > COMPARE_LENGTH) {
            sourceStr = sourceStr.substring(0, COMPARE_LENGTH - 1);
        }

        // 移除需要忽略的字符
        for (String eliminateChar : IGNORED_CHARS) {
            sourceStr = sourceStr.replace(eliminateChar, "");
        }

        return sourceStr;
    }

    /**
     * 获取比较器实例
     *
     *
     * @return 描述比较器实例
     */
    public static DescriptionComparer getComparer() {
        return DescriptionComparer.ComparerHolder.INSTANCE;
    }

    /**
     * 单例Holder
     */
    private static class ComparerHolder {
        private static DescriptionComparer INSTANCE = new DescriptionComparer();
    }
}


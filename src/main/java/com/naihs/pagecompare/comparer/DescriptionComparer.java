package com.naihs.pagecompare.comparer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;

/**
 * Comparer class for compare titles between 2 page content
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class DescriptionComparer extends Comparer {

    /** description comparer name */
    private static final String NAME = "CONTENT_COMPARE";

    /** Ignored chars in compared */
    private static final List<String> IGNORED_CHARS = Arrays.asList("\\s*", "_");

    /** Compare length on every descriptions */
    private static final int COMPARE_LENGTH = 10;    // Compare first 10 chars

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

        // Use content map with max size as base content map
        if (leftContent.getPageItemsMap().size() > rightContent.getPageItemsMap().size()) {
            baseContentMap = rightContent.getPageItemsMap();
            urlSet         = new HashSet<>(leftContent.getPageItemsMap().size());
            leftContent.getPageItemsMap().values().forEach(item -> urlSet.add(getCompareTargetStr(item.getContent())));
        } else {
            baseContentMap = leftContent.getPageItemsMap();
            urlSet         = new HashSet<>(rightContent.getPageItemsMap().size());
            rightContent.getPageItemsMap().values().forEach(item -> urlSet.add(getCompareTargetStr(item.getContent())));
        }

        urlSet.remove(null);    // Remove null description if exist

        int baseCount = urlSet.size();
        int sameCount = 0;

        for (Map.Entry<String, ItemContent> entry : baseContentMap.entrySet()) {

            // Not compare null content
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
     * remove ignored chars and fix description length
     *
     *
     * @param sourceStr result description
     *
     * @return fixed result description
     */
    public static String getCompareTargetStr(String sourceStr) {
        if (null == sourceStr) {
            return null;
        }

        // Fix comparing descriptions' length
        if (sourceStr.length() > COMPARE_LENGTH) {
            sourceStr = sourceStr.substring(0, COMPARE_LENGTH - 1);
        }

        // Remove ignored chars in comparing descriptions
        for (String eliminateChar : IGNORED_CHARS) {
            sourceStr = sourceStr.replace(eliminateChar, "");
        }

        return sourceStr;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public static DescriptionComparer getComparer() {
        return DescriptionComparer.ComparerHolder.INSTANCE;
    }

    /**
     * Class description
     *
     *
     * @version        Enter version here..., 16/12/29
     * @author         Enter your name here...
     */
    private static class ComparerHolder {

        /** Field description */
        private static DescriptionComparer INSTANCE = new DescriptionComparer();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com

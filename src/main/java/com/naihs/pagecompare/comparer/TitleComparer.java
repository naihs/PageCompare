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
public class TitleComparer extends Comparer {

    /** title comparer name */
    private static final String NAME = "TITLE_COMPARE";

    /** ignore chars in compare */
    private static final List<String> ELIMINATE_CHARS = Arrays.asList("\\s*", "_");

    /**
     * Constructs ...
     *
     */
    private TitleComparer() {
        super(NAME);
    }

    /**
     * remove ignored char in source title
     *
     *
     * @param source source title
     *
     * @return fixed title
     */
    public String removeIgnoreChars(String source) {
        for (String eliminateChar : ELIMINATE_CHARS) {
            source = source.replace(eliminateChar, "");
        }

        return source;
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
            leftContent.getPageItemsMap().values().forEach(item -> urlSet.add(removeIgnoreChars(item.getTitle())));
        } else {
            baseContentMap = leftContent.getPageItemsMap();
            urlSet         = new HashSet<>(rightContent.getPageItemsMap().size());
            rightContent.getPageItemsMap().values().forEach(item -> urlSet.add(removeIgnoreChars(item.getTitle())));
        }

        int baseCount = urlSet.size();
        int sameCount = 0;

        for (Map.Entry<String, ItemContent> entry : baseContentMap.entrySet()) {
            if (urlSet.contains(removeIgnoreChars(entry.getValue().getTitle()))) {
                sameCount++;
            }
        }

        return sameCount / (baseCount * 1.0f);
    }

    /**
     * Singleton getter
     *
     *
     * @return instance of title comparer
     */
    public static TitleComparer getComparer() {
        return TitleComparer.ComparerHolder.INSTANCE;
    }

    /**
     * Singleton Holder
     */
    private static class ComparerHolder {
        private static TitleComparer INSTANCE = new TitleComparer();
    }
}

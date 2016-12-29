package com.naihs.pagecompare.comparer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;

/**
 * 比较两个页面搜索结果各个项的标题
 *
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class TitleComparer extends Comparer {

    /** 比较器名称 */
    private static final String NAME = "TITLE_COMPARE";

    /**  需排除的字符列表 */
    private static final List<String> ELIMINATE_CHARS = Arrays.asList("\\s*", "_");

    /**
     * Constructs ...
     *
     */
    private TitleComparer() {
        super(NAME);
    }

    /**
     * 预处理搜索结果的描述
     *
     *
     * @param source 源标题
     *
     * @return 预处理后的标题
     */
    private String removeIgnoreChars(String source) {
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
     * 获取比较器实例
     *
     *
     * @return 标题比较器实例
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

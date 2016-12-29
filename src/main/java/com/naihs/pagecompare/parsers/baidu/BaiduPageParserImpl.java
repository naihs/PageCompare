package com.naihs.pagecompare.parsers.baidu;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.models.Parser;
import com.naihs.pagecompare.types.HtmlTags;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Parser on baidu
 *
 * @version        1.0, 16/12/29
 * @author         yexiang01
 */
public class BaiduPageParserImpl extends Parser {

    /** Baidu 默认搜索请求格式 */
    private static final String BAIDU_SEARCH_FORMAT = "http://www.baidu.com/baidu?word=%s";
    /** JSoup根据类名查找元素格式 */
    private static final String SELECT_CLASS_FORMAT = "[class='%s']";
    /** 目标特征1 - 单个的搜索结果 */
    private static final String RESULT_CONTAINER_CHARA = String.format(SELECT_CLASS_FORMAT, "result c-container ");
    /** 目标特征2 - 包含子项的搜索结果 */
    private static final String RESULT_OP_CONTAINER_CHARA = String.format(SELECT_CLASS_FORMAT,
                                                                          "result-op c-container xpath-log");
    /** 描述特征 */
    private static final String CONTENT_CHARA = "div.c-abstract";

    private UrlFinder urlFinder = new UrlFinder();

    @Override
    public PageContent parser(String keyWord) throws IOException, ExecutionException, InterruptedException {
        String      searchUrl = String.format(BAIDU_SEARCH_FORMAT, keyWord);
        PageContent result    = new PageContent();
        Request     request   = new Request.Builder().url(searchUrl).get().build();
        Response    resp      = client.newCall(request).execute();
        Document    doc       = Jsoup.parse(resp.body().string(), searchUrl);

        result.addItems(getResultContainerItem(doc));
        result.addItems(getResultOpContainerItem(doc));
        result = urlFinder.updateRealUrl(result);

        return result;
    }

    /**
     * Singleton getter
     *
     * @return 百度页面Parser实例
     */
    public static BaiduPageParserImpl getParser() {
        return ParserHolder.INSTANCE;
    }

    /**
     * 获取单个搜索结果项
     *
     *
     * @param fullDoc 页面Doc
     *
     * @return 所有单个搜索结果项
     *
     * @throws IOException
     */
    private List<ItemContent> getResultContainerItem(Document fullDoc) throws IOException {
        List<ItemContent> contents = new ArrayList<>();
        Elements          em       = fullDoc.select(RESULT_CONTAINER_CHARA);

        for (Element itemBlock : em) {
            Element aNode       = itemBlock.select(HtmlTags.H3.name()).select(HtmlTags.A.name()).first();
            Element contentNode = itemBlock.select(CONTENT_CHARA).first();

            contents.add(new BaiduItemContentImpl(aNode.attr(HtmlTags.HREF.name()), aNode.text(), contentNode.text()));
        }

        return contents;
    }

    /**
     * 获取包含子项的搜索结果项的子项
     *
     *
     * @param fullDoc 页面Doc
     *
     * @return all 所有搜索结果子项
     */
    private List<ItemContent> getResultOpContainerItem(Document fullDoc) {
        // 包含子项的搜索结果特征
        final String      COFFSET_MARK_SELECT = String.format(SELECT_CLASS_FORMAT, "c-offset");
        final String      CROW_MARK_SELECT    = String.format(SELECT_CLASS_FORMAT, "c-row");
        List<ItemContent> contents            = new ArrayList<>();
        Elements          em                  = fullDoc.select(RESULT_OP_CONTAINER_CHARA);

        for (Element h3Items : em.tagName(HtmlTags.H3.name())) {
            Elements rowItems = h3Items.select(COFFSET_MARK_SELECT);

            if (rowItems.size() > 0) {
                // 如果该项包含子项，则只使用子项作为搜索结果
                for (Element rowItem : rowItems.select(CROW_MARK_SELECT)) {
                    Element aNode = rowItem.select(HtmlTags.A.name()).first();

                    contents.add(new BaiduItemContentImpl(aNode.attr(HtmlTags.HREF.name()), aNode.text(), null));
                }
            } else {
                Element aNode = h3Items.select(HtmlTags.A.name()).first();

                contents.add(new BaiduItemContentImpl(aNode.attr(HtmlTags.HREF.name()), aNode.text(), null));
            }
        }

        return contents;
    }

    /**
     * Singleton Holder
     */
    private static class ParserHolder {
        private static final BaiduPageParserImpl INSTANCE = new BaiduPageParserImpl();
    }
}

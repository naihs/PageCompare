package com.naihs.pagecompare.parsers.google;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.models.Parser;
import com.naihs.pagecompare.types.HtmlTags;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Parser on google
 *
 *
 * @version        1.0, 16/12/29
 * @author         yexiang
 */
public class GooglePageParserImpl extends Parser {

    /** Google 默认搜索请求格式 */
    private static final String GOOGLE_SEARCH_FORMAT = "https://www.google.com/search?q=%s";

    /** JSoup根据div类名查找表达式 */
    private static final String SELECT_DIV_CLASS_FORMAT = "div.%s";

    /** JSoup根据h3类名查找表达式 */
    private static final String SELECT_H3_CLASS_FORMAT = "h3.%s";

    /** JSoup根据span类名查找表达式 */
    private static final String SELECT_SPAN_CLASS_FORMAT = "span.%s";

    /** 搜索项字段特征 */
    private static final String TARGET_BLOCK_CHARA = String.format(SELECT_DIV_CLASS_FORMAT, "g");

    /** 标题字段特征 */
    private static final String TITLE_URL_BLOCK_CHARA = String.format(SELECT_H3_CLASS_FORMAT, "r");

    /** 描述字段特征 */
    private static final String CONTENT_BLOCK_CHARA = String.format(SELECT_SPAN_CLASS_FORMAT, "st");

    /** 请求头key */
    private static final String HEADER_KEY = "user-agent";

    /** 模拟浏览器请求 */
    private static final String HEADER_VALUE = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 "
                                               + "(KHTML, like Gecko) Chrome/53.0.2785.101 Safari/537.36";

    @Override
    public PageContent parser(String keyWord) throws IOException, ExecutionException, InterruptedException {
        String      searchUrl = String.format(GOOGLE_SEARCH_FORMAT, keyWord);
        PageContent result    = new PageContent();
        Request     request   = new Request.Builder().url(searchUrl).get().addHeader(HEADER_KEY, HEADER_VALUE).build();
        Response    resp      = client.newCall(request).execute();
        String      content;
        Document    doc = Jsoup.parse(resp.body().string(), searchUrl);
        Elements    em  = doc.select(TARGET_BLOCK_CHARA);

        for (Element item : em) {
            Element aNode       = item.select(TITLE_URL_BLOCK_CHARA).select(HtmlTags.A.name()).first();
            Element contentNode = item.select(CONTENT_BLOCK_CHARA).first();

            if (null == aNode) {
                // 并非所有的div.g都包含有a标签
                // 忽略不包含a标签的div.g
                continue;
            }

            if (null != contentNode) {
                content = contentNode.text();
            } else {
                // 一些搜索结果不包含描述
                content = null;
            }

            result.addItem(new GoogleItemContentImpl(aNode.attr(HtmlTags.HREF.name()), aNode.text(), content));
        }

        return result;
    }

    /**
     * Singleton getter
     *
     * @return instance of google page parser
     */
    public static GooglePageParserImpl getParser() {
        return ParserHolder.INSTANCE;
    }

    /**
     * Singleton Holder
     */
    private static class ParserHolder {
        private static final GooglePageParserImpl INSTANCE = new GooglePageParserImpl();
    }
}

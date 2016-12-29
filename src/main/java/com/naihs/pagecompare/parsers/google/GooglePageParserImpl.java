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

    /** Google search request url formatting */
    private static final String GOOGLE_SEARCH_FORMAT = "https://www.google.com/search?q=%s";

    /** formatting for JSoup to search by div's class */
    private static final String SELECT_DIV_CLASS_FORMAT = "div.%s";

    /** formatting for JSoup to search by h3's class */
    private static final String SELECT_H3_CLASS_FORMAT = "h3.%s";

    /** formatting for JSoup to search by span's class */
    private static final String SELECT_SPAN_CLASS_FORMAT = "span.%s";

    /** target block's characteristic */
    private static final String TARGET_BLOCK_CHARA = String.format(SELECT_DIV_CLASS_FORMAT, "g");

    /** target title's characteristic */
    private static final String TITLE_URL_BLOCK_CHARA = String.format(SELECT_H3_CLASS_FORMAT, "r");

    /** target description's characteristic */
    private static final String CONTENT_BLOCK_CHARA = String.format(SELECT_SPAN_CLASS_FORMAT, "st");

    /** request header key */
    private static final String HEADER_KEY = "user-agent";

    /** request header value */
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
                // Not all 'div.g' tag has <a> child
                // Not parse those 'div.g' tags
                continue;
            }

            if (null != contentNode) {
                content = contentNode.text();
            } else {
                // Some result not description, use null as description
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

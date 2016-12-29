package com.naihs.pagecompare.parsers.baidu;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.*;

import com.naihs.pagecompare.models.ItemContent;
import com.naihs.pagecompare.models.PageContent;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 获取百度搜索结果的真实Url工具类
 *
 *
 * @version        1.0, 16/12/29
 * @author         yexiang
 */
public class UrlFinder {

    private final static OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
    private final static Pattern pt = Pattern.compile("URL=\\'(.*?)\\'");
    private final static String TARGET_URL_NAME = "location";
    private Queue<ItemContent> itemQueue;

    /**
     * 更新所有搜索结果中的url为真实url
     *
     *
     * @param sourceContent 搜索结果页面容器
     *
     * @return 更新后的搜索结果页面容器
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public PageContent updateRealUrl(PageContent sourceContent) throws ExecutionException, InterruptedException {
        PageContent      resultPageContent = new PageContent();
        ExecutorService  executorService   = Executors.newCachedThreadPool();
        List<FutureTask> tasks             = new ArrayList<>();

        itemQueue = new ConcurrentLinkedDeque<>(sourceContent.getPageItemsMap().values());

        Callable<ItemContent> findUrlTask = () -> {
                                                if (!itemQueue.isEmpty()) {
                                                    ItemContent item = itemQueue.poll();

                                                    try {
                                                        item.setUrl(getRealUrl(item.getUrl()));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    return item;
                                                } else {
                                                    return null;
                                                }
                                            };

        for (int i = 0; i < sourceContent.getPageItemsMap().size(); i++) {
            FutureTask<ItemContent> task = new FutureTask<>(findUrlTask);

            tasks.add(task);
            executorService.submit(task);
        }

        for (FutureTask<ItemContent> task : tasks) {
            ItemContent newItem = task.get();

            if (null == newItem) {
                continue;    // Impossible here
            }

            resultPageContent.addItem(newItem);
        }

        executorService.shutdown();

        return resultPageContent;
    }

    /**
     * 获取真实url
     *
     *
     * @param sourceUrl 搜索结果中的原始url
     *
     * @return 真实url
     *
     * @throws IOException
     */
    private static String getRealUrl(String sourceUrl) throws IOException {
        Response resp = client.newCall(new Request.Builder().url(sourceUrl).build()).execute();

        if (HTTP_OK == resp.code()) {
            // 原始url指向页面为200，此时页面为跳转js脚本，用正则爬目标
            Matcher mt = pt.matcher(resp.body().string());

            if (mt.find()) {
                return mt.group(1);
            } else {
                return sourceUrl;
            }
        } else if (HTTP_MOVED_TEMP == resp.code()) {
            // 原始url指向页面为302重定向，从头中获取真实地址
            return resp.header(TARGET_URL_NAME);
        } else {
            return sourceUrl;
        }
    }
}


package com.naihs.pagecompare.models;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

/**
 * Parser抽象
 *
 *
 * @version        1.0 16/12/29
 * @author         yexiang
 */
public abstract class Parser {

    /** 公共 OKHTTP CLIENT */
    protected final static OkHttpClient client = new OkHttpClient().newBuilder().build();

    /**
     * parser逻辑
     *
     *
     * @param keyWord 搜索关键词
     *
     * @return 搜索结果容器PageContent
     *
     * @throws ExecutionException
     * @throws IOException
     * @throws InterruptedException
     */
    abstract public PageContent parser(String keyWord) throws IOException, ExecutionException, InterruptedException;
}

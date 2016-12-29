package com.naihs.pagecompare.models;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

/**
 * Parser abstract class for all parser
 *
 *
 * @version        1.0 16/12/29
 * @author         yexiang
 */
public abstract class Parser {

    /** Public okhttp client, sharing for all parser */
    protected final static OkHttpClient client = new OkHttpClient().newBuilder().build();

    /**
     * abstract parser method
     *
     *
     * @param keyWord search target key word
     *
     * @return parsed content container
     *
     * @throws ExecutionException
     * @throws IOException
     * @throws InterruptedException
     */
    abstract public PageContent parser(String keyWord) throws IOException, ExecutionException, InterruptedException;
}

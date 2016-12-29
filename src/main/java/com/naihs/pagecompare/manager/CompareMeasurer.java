package com.naihs.pagecompare.manager;

import java.util.*;
import java.util.concurrent.*;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.models.Parser;

/**
 * 比较控制器
 *
 * 比较目标网站对于同一个keyword的结果相似度
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class CompareMeasurer {

    /** 比较器容器
     *  key: 比较器实例
     *  value: 比较结果权重
     * */
    Map<Comparer, Float> comparerMap = new HashMap<>();

    /** 比较结果容器
     *  key: 比较器名称
     *  value: 该比较器比较结果
     * */
    Map<String, Float> resultMap = new HashMap<>();

    /**
     * 比较两个parser在搜索keyword的结果相似度
     *
     *
     * @param leftParser 待比较的parser实例
     * @param rightParser 待比较的parser实例
     * @param keyWord 搜索关键字
     *
     * @return 计算权重的相似度结果
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public float compare(Parser leftParser, Parser rightParser, String keyWord)
            throws ExecutionException, InterruptedException {
        ExecutorService       executorService = Executors.newCachedThreadPool();
        Queue<Parser>         itemQueue       = new ConcurrentLinkedDeque<>(Arrays.asList(leftParser, rightParser));
        float                 totalWeight     = getTotalWeight();
        float                 totalRate       = 0.0f;
        Callable<PageContent> compareTask     = () -> {
                                                    if (!itemQueue.isEmpty()) {
                                                        try {
                                                            return itemQueue.poll().parser(keyWord);
                                                        } catch (Exception e) {
                                                            System.out.println("Parser failed: " + e.getMessage());

                                                            return new PageContent();
                                                        }
                                                    } else {
                                                        return new PageContent();
                                                    }
                                                };

        // Create and submit 2 parser task (left & right)
        FutureTask<PageContent> leftParserTask  = new FutureTask<>(compareTask);
        FutureTask<PageContent> rightParserTask = new FutureTask<>(compareTask);

        executorService.submit(leftParserTask);
        executorService.submit(rightParserTask);

        // Wait content
        PageContent leftContent  = leftParserTask.get();
        PageContent rightContent = rightParserTask.get();

        for (Map.Entry<Comparer, Float> entry : comparerMap.entrySet()) {
            Comparer comparer     = entry.getKey();
            float    comparedRate = comparer.similarityRatio(leftContent, rightContent);

            totalRate += comparedRate * (entry.getValue() / totalWeight);
            resultMap.put(comparer.getName(), comparedRate);
        }

        executorService.shutdown();

        return totalRate;
    }

    /**
     * 注册比较器
     *
     *
     * @param comparer 欲注册的比较器实例
     * @param weight 该比较器权重
     *
     * @throws Exception
     */
    public void registComparer(Comparer comparer, float weight) throws Exception {
        if (weight > 0) {
            comparerMap.put(comparer, weight);
        } else {
            throw new Exception("Weight must more than 0!!");
        }
    }

    /**
     * 清空已注册的比较器
     *
     */
    public void resetComparers() {
        comparerMap.clear();
    }

    /**
     * 获取结果Map
     *
     *
     * @return 比较结果Map
     *          key: 比较器名
     *          value: 该比较器比较结果
     */
    public Map<String, Float> getDetails() {
        return resultMap;
    }

    /**
     * 计算总权重
     *
     *
     * @return 所有比较器的总权重
     */
    private float getTotalWeight() {
        float totalWeight = comparerMap.values().stream().reduce(0.0f, (x, y) -> (x + y));

        return (totalWeight > 0)
               ? totalWeight
               : 1.0f;
    }
}

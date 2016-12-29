package com.naihs.pagecompare.manager;

import java.util.*;
import java.util.concurrent.*;

import com.naihs.pagecompare.models.Comparer;
import com.naihs.pagecompare.models.PageContent;
import com.naihs.pagecompare.models.Parser;

/**
 * Compare measurer
 *
 * compare similarity between 2 parsed page content with weight
 *
 * @version        1.0, 16/12/28
 * @author         yexiang
 */
public class CompareMeasurer {

    /** Registed comparer
     *  key: comparer
     *  value: weight
     * */
    Map<Comparer, Float> comparerMap = new HashMap<>();

    /** Compare result
     *  key: comparer name
     *  value: compare result for this comparer
     * */
    Map<String, Float> resultMap = new HashMap<>();

    /**
     * compare result from 2 parser on key word
     *
     *
     * @param leftParser parser instance
     * @param rightParser parser instance
     * @param keyWord target search key word
     *
     * @return similarity ratio with weight
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
     * registe a comparer
     *
     *
     * @param comparer comparer instance
     * @param weight weight on this comparer
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
     * Reset registed comparers
     *
     */
    public void resetComparers() {
        comparerMap.clear();
    }

    /**
     * Get compare result on every comparers
     *
     *
     * @return Compare result
     *          key: comparer name
     *          value: compare result for this comparer
     */
    public Map<String, Float> getDetails() {
        return resultMap;
    }

    /**
     * Calculate total weight
     *
     *
     * @return total weight
     */
    public float getTotalWeight() {
        float totalWeight = comparerMap.values().stream().reduce(0.0f, (x, y) -> (x + y));

        return (totalWeight > 0)
               ? totalWeight
               : 1.0f;
    }
}

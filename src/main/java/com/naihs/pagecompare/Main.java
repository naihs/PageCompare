package com.naihs.pagecompare;

import java.util.Map;

import com.naihs.pagecompare.comparer.DescriptionComparer;
import com.naihs.pagecompare.comparer.TitleComparer;
import com.naihs.pagecompare.comparer.UrlComparer;
import com.naihs.pagecompare.manager.CompareMeasurer;
import com.naihs.pagecompare.parsers.baidu.BaiduPageParserImpl;
import com.naihs.pagecompare.parsers.google.GooglePageParserImpl;

/**
 * Main Class
 *
 * @version        1.0 12/28/2016
 * @author         Ye Xiang
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java Main [Key word]");
            System.exit(0);
        }

        CompareMeasurer measurer = new CompareMeasurer();

        measurer.registComparer(DescriptionComparer.getComparer(), 0.1f);
        measurer.registComparer(TitleComparer.getComparer(), 0.4f);
        measurer.registComparer(UrlComparer.getComparer(), 0.5f);

        float total = measurer.compare(BaiduPageParserImpl.getParser(), GooglePageParserImpl.getParser(), args[0]);

        System.out.println("Total: " + String.valueOf(total));

        for (Map.Entry<String, Float> entry : measurer.getDetails().entrySet()) {
            System.out.printf("%s: %f\n", entry.getKey(), entry.getValue());
        }
    }
}


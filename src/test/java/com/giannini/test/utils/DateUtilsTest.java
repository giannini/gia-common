package com.giannini.test.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.giannini.common.utils.DateUtils;

public class DateUtilsTest {

    public static void main(String[] args)
            throws Exception {

        ExecutorService exec = Executors.newFixedThreadPool(3);
        List<Future<Date>> result = new LinkedList<Future<Date>>();
        
        // simple转换
        for (int i = 1; i < 10; i++) {
            final String str = "2016-06-0" + i;
            Callable<Date> task = new Callable<Date>() {
                public Date call() throws Exception {
                    return DateUtils.parseSimpleDateString(str);
                }
            };
            result.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdown();
        // simple转换结果输出
        for (Future<Date> res: result) {
            System.out.println(res.get());
        }
        
        System.out.println("===========================================");
        // 指定格式转换
        exec = Executors.newFixedThreadPool(3);
        Map<String, String> dates = new HashMap<String, String>();
        dates.put("20160503", "yyyyMMdd");
        dates.put("2016-06-03 12:15:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("2016-06-13 08:18:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("2016-03-23 23:15:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("20160303", "yyyyMMdd");
        result.clear();
        for (final Entry<String, String> date: dates.entrySet()) {
            Callable<Date> task = new Callable<Date>() {
                public Date call() throws Exception {
                    return DateUtils.parseDateString(date.getKey(),
                            date.getValue());
                }
            };
            result.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdownNow();
        // 输出指定格式的结果
        for (Future<Date> res: result) {
            System.out.println(res.get());
        }
    }

}

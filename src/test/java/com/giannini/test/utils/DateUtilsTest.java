package com.giannini.test.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

import com.giannini.common.utils.DateUtils;

public class DateUtilsTest {

    @Test
    public void testSimpleDateString()
            throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newFixedThreadPool(3);
        List<Future<Date>> results = new LinkedList<Future<Date>>();

        // simple转换
        for (int i = 1; i < 10; i++) {
            final String str = "2016-06-0" + i;
            Callable<Date> task = new Callable<Date>() {
                public Date call() throws Exception {
                    return DateUtils.parseSimpleDateString(str);
                }
            };
            results.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdown();
        
        for (Future<Date> result: results) {
            Assert.assertNotNull(result.get());
        }
    }

    @Test
    public void testParseDateStringWithPattern()
            throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newFixedThreadPool(3);
        List<Future<Date>> results = new LinkedList<Future<Date>>();

        Map<String, String> dates = new HashMap<String, String>();
        dates.put("20160503", "yyyyMMdd");
        dates.put("2016-06-03 12:15:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("2016-06-13 08:18:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("2016-03-23 23:15:34", "yyyy-MM-dd hh:mm:ss");
        dates.put("20160303", "yyyyMMdd");
        for (final Entry<String, String> date: dates.entrySet()) {
            Callable<Date> task = new Callable<Date>() {
                public Date call() throws Exception {
                    return DateUtils.parseDateString(date.getKey(),
                            date.getValue());
                }
            };
            results.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdownNow();
        
        for (Future<Date> result: results) {
            Assert.assertNotNull(result.get());
        }
    }

    @Test
    public void testStrToMilliseconds() {
        String val1 = "15d";
        Assert.assertNotEquals(-1L, DateUtils.strToMilliseconds(val1));

        String val2 = "35M";
        Assert.assertNotEquals(-1, DateUtils.strToMilliseconds(val2));

        String val3 = "3000";
        Assert.assertEquals(3000L, DateUtils.strToMilliseconds(val3));

        String val4 = "200ms";
        Assert.assertEquals(200L, DateUtils.strToMilliseconds(val4));

    }

    @Test
    public void testMillisecondsToStr() {
        // 2d4h30m55s:189055000
        long val1 = 189055000L;
        Assert.assertEquals("2d4h30m55s", DateUtils.milliSecondToStr(val1));
        
        // 35m20s200ms
        long val2 = 2120200L;
        Assert.assertEquals("35m20s200ms", DateUtils.milliSecondToStr(val2));
        
        //200ms
        long val3 = 200L;
        Assert.assertEquals("200ms", DateUtils.milliSecondToStr(val3));
    }
}

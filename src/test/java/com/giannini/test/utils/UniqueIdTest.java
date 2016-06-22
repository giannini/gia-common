package com.giannini.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.giannini.common.utils.UniqueIdUtils;

public class UniqueIdTest {

    @Test
    public void testGetUUID() {
        assertNotNull(UniqueIdUtils.getUUID());
    }

    @Test
    public void testGetNumbersUUID() {
        int number = 5;
        String[] uuids = UniqueIdUtils.getUUID(number);
        assertNotNull(uuids);
        assertEquals(uuids.length, number);
        Set<String> uuidSet = new HashSet<String>(Arrays.asList(uuids));
        assertEquals(uuids.length, uuidSet.size());
    }

    @Test
    public void testMultiThreadsGetUUID()
            throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        List<Future<String>> result = new LinkedList<Future<String>>();

        int number = 10;
        for (int i = 0; i < number; i++) {
            Callable<String> task = new Callable<String>() {
                public String call() throws Exception {
                    return UniqueIdUtils.getUUID();
                }
            };
            result.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdown();

        Set<String> uuidSet = new HashSet<String>();
        for (Future<String> res: result) {
            assertNotNull(res.get());
            uuidSet.add(res.get());
        }
        assertEquals(number, uuidSet.size());
    }

    @Test
    public void testMultiThreadGetNumbersUUID() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        List<Future<String[]>> result = new LinkedList<Future<String[]>>();

        int threads = 10;
        final int number = 50;
        for (int i = 0; i < threads; i++) {
            Callable<String[]> task = new Callable<String[]>() {
                public String[] call() throws Exception {
                    return UniqueIdUtils.getUUID(number);
                }
            };
            result.add(exec.submit(task));
        }
        Thread.sleep(100);
        exec.shutdown();

        Set<String> uuidSet = new HashSet<String>();
        for (Future<String[]> res: result) {
            assertNotNull(res.get());
            assertEquals(number, res.get().length);
            for (String id: res.get()) {
                uuidSet.add(id);
            }
        }
        assertEquals(number * threads, uuidSet.size());
    }

}


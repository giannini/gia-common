package com.giannini.common.utils;

import java.util.UUID;

/**
 * 使用了UUID生成唯一标识符，去掉了其中的‘-’
 * 
 * @author giannini
 */
public final class UniqueIdUtils {

    private static final String spliter = "-";

    private UniqueIdUtils() {}
    
    /**
     * 获取单个uuid
     * 
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return formatUUID(uuid);
    }

    /**
     * 批量获取uuid
     * 
     * @param number
     * @return
     */
    public static String[] getUUID(int number) {
        if (number < 1) {
            return null;
        }

        String[] uuids = new String[number];
        for (int i = 0; i < number; i++) {
            uuids[i] = getUUID();
        }

        return uuids;
    }

    /**
     * 去除生成的uuid中包含的'-'字段
     * 
     * @param uuid
     * @return
     */
    private static String formatUUID(String uuid) {
        /***
         * UUID是由一个十六位的数字组成,表现出来的形式例如<br>
         * 550E8400-E29B-11D4-A716-446655440000
         * <p>
         * 目前是按照:8-4-4-4-12来划分的，避免以后标准更改导致的问题，直接用'-'来划分了
         */
        if (!uuid.contains(spliter)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String substr = uuid;
        int index = -1;
        while ((index = substr.indexOf(spliter)) >= 0) {
            sb.append(substr.substring(0, index));
            substr = substr.substring(index + 1);
        }
        sb.append(substr);

        return sb.toString();
    }

}

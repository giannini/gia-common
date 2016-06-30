package com.giannini.common.cache;

/**
 * 缓存工厂类
 * 
 * @author giannini
 */
public final class CacheFactory {

    /**
     * 本地内存缓存
     */
    private static ICacheService localMemCache = null;

    /**
     * TODO 远程内存缓存
     */
    private static ICacheService remoteMemCache = null;

    private CacheFactory() {}

    public static synchronized ICacheService createLocalCache() {
        // TODO
        return localMemCache;

    }

    public static synchronized ICacheService createNamedLocalCache() {
        // TODO 创建指定的本地缓存，如果已经有同名，直接返回该缓存
        return null;
    }

}

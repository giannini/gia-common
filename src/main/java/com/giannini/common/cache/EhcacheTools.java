package com.giannini.common.cache;

import java.io.IOException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

/**
 * ehcache缓存框架的工具类
 * 
 * @author giannini
 */
public class EhcacheTools {

    /**
     * 缓存管理器
     */
    private static CacheManager ehcacheManger = null;

    /**
     * 缓存配置信息
     */
    private static Configuration ehcacheConfig = null;

    /**
     * 关闭底层Ehcache
     */
    public static synchronized void close() {
        try {
            if (ehcacheManger != null) {
                ehcacheManger.shutdown();
                ehcacheManger = null;
            }
        } catch (Throwable th) {
            // ignore
        }
    }

    /**
     * 创建管理器实例
     * 
     * @param configuration
     * @throws IOException
     */
    private static void createManager(Configuration configuration)
            throws IOException {

        ehcacheConfig = configuration;
        ehcacheConfig.setDynamicConfig(false); // 禁止动态配置
        ehcacheConfig.setUpdateCheck(false); // 关闭版本检测
        ehcacheConfig.setMonitoring("off"); // 关闭自动注册SampledCacheMBean

        ehcacheManger = CacheManager.create(ehcacheConfig);
    }

    /**
     * 创建ehcache缓存实例
     * 
     * @param configuration
     *            ehcache的配置文件
     * @param cacheConfig
     *            缓存的配置节点
     * @return
     * @throws IOException
     */
    public synchronized static ICacheService createEhcache(
            Configuration configuration, CacheConfiguration cacheConfig)
            throws Exception {
        Cache cache = null;

        if (ehcacheManger == null) {
            createManager(configuration);
        }

        CacheConfiguration cacheConf = null;
        if (cacheConfig == null) {
            // 获取default cache的配置信息
            cacheConf = ehcacheManger.getConfiguration()
                    .getDefaultCacheConfiguration();
            cache = new Cache(cacheConf);
            ehcacheManger.addCache(cache);
        } else {
            cache = ehcacheManger.getCache(cacheConfig.getName());
        }

        if (cache == null) {
            cacheConf = new CacheConfiguration();
            cacheConf.persistence(null);// 不适用持久化
            cacheConf.setTransactionalMode("off");

            cache = new Cache(cacheConf);
            cache.setName(Thread.currentThread().getName() + "."
                    + System.currentTimeMillis());
            ehcacheManger.addCache(cache);
        }

        return new EhcacheAdapter(cache);
    }

}

package com.giannini.common.cache;

import java.util.Date;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存接口适配器(单个缓存)
 * 
 * @author giannini
 */
public class EhcacheAdapter implements ICacheService {

    /**
     * ehcache实例对象
     */
    private final Cache cache;

    /**
     * 缓存存活秒数
     */
    private Integer timeToLiveSeconds;

    /**
     * 缓存的闲置时间，单位：秒
     */
    private Integer timeToIdleSeconds;

    public EhcacheAdapter(Cache cache) {
        this.cache = cache;
        if (cache == null) {
            throw new NullPointerException("null cache");
        }

        this.timeToLiveSeconds = (int) cache.getCacheConfiguration()
                .getTimeToLiveSeconds();
        if (timeToLiveSeconds <= 0) {
            this.timeToLiveSeconds = Integer.MAX_VALUE;
        }

        this.timeToIdleSeconds = (int) cache.getCacheConfiguration()
                .getTimeToIdleSeconds();
        if (timeToIdleSeconds <= 0) {
            this.timeToIdleSeconds = Integer.MAX_VALUE;
        }
    }

    public void dispose() {
        cache.getCacheManager().removeCache(cache.getName());
    }

    public void clear() {
        cache.removeAll();
    }

    public boolean delete(Object key) {
        return cache.remove(key);
    }

    public Object get(Object key) {
        Element element = cache.get(key);
        if (element == null || element.isExpired()) {
            return null;
        }
        return element.getObjectValue();
    }

    public boolean keyExists(Object key) {
        return cache.isKeyInCache(key);
    }

    public Object replace(Object key, Object value) {
        Element old = cache.replace(
                new Element(key, value, timeToIdleSeconds, timeToLiveSeconds));
        return old == null ? null : old.getObjectValue();
    }

    public Object replace(Object key, Object value, Date expiry) {
        Element element = null;
        if (expiry == null) {
            element = new Element(key, value, timeToIdleSeconds,
                    timeToLiveSeconds);
        } else {
            int timeToLive = (int) (expiry.getTime()
                    - System.currentTimeMillis())
                    / 1000;
            element = new Element(key, value, timeToIdleSeconds, timeToLive);
        }

        Element old = cache.replace(element);

        return old == null ? null : old.getObjectValue();
    }

    public boolean set(Object key, Object value) {
        Element element = new Element(key, value, timeToIdleSeconds,
                timeToLiveSeconds);
        cache.put(element);
        return true;
    }

    public boolean set(Object key, Object value, Date expiry) {
        Element element = null;
        if (expiry == null) {
            element = new Element(key, value, timeToIdleSeconds,
                    timeToLiveSeconds);
        } else {
            int timeToLive = (int) (expiry.getTime()
                    - System.currentTimeMillis()) / 1000;
            element = new Element(key, value, timeToIdleSeconds, timeToLive);
        }

        cache.put(element);
        return true;
    }

    public boolean set(Object key, Object value, Long liveMillis) {
        Element element = null;
        if (liveMillis == null || liveMillis < 0) {
            element = new Element(key, value, timeToIdleSeconds,
                    timeToLiveSeconds);
        } else {
            element = new Element(key, value, timeToIdleSeconds,
                    (int) (liveMillis / 1000));
        }

        cache.put(element);
        return true;
    }

    public Object setIfAbsent(Object key, Object value) {
        Element old = cache.putIfAbsent(
                new Element(key, value, timeToIdleSeconds, timeToLiveSeconds));
        return old == null ? null : old.getObjectValue();
    }

    public Object setIfAbsent(Object key, Object value, Date expiry) {
        Element element = null;
        if (expiry == null) {
            element = new Element(key, value, timeToIdleSeconds,
                    timeToLiveSeconds);
        } else {
            int timeToLive = (int) (expiry.getTime()
                    - System.currentTimeMillis()) / 1000;
            element = new Element(key, value, timeToIdleSeconds, timeToLive);
        }

        Element old = cache.putIfAbsent(element);

        return old == null ? null : old.getObjectValue();
    }

    public String getName() {
        return this.cache.getName();
    }

}

package com.giannini.common.cache;

import java.util.Date;

/**
 * 缓存服务接口
 * 
 * @author giannini
 */
public interface ICacheService {

    /**
     * 缓存服务关闭接口
     * <p>
     * 将所有当前缓存的数据丢弃, 并关闭服务
     * <p>
     * 注意: 该接口应该只供Tools类来调用
     */
    public void disposeAll();

    /**
     * 清除当前已经缓存的所有数据
     */
    public void clear();

    /**
     * 删除指定键值对
     * 
     * @param key
     *            建
     * @return true=操作成功;false=操作失败
     */
    public boolean delete(Object key);

    /**
     * 获取指定键对应的值
     * 
     * @param key
     *            键
     * @return 值
     */
    public Object get(Object key);

    /**
     * 判断指定键是否存在
     * 
     * @param key
     *            键
     * @return true=存在; false=不存在
     */
    public boolean keyExists(Object key);

    /**
     * 替换一个键值对, 该键值对不会过期
     * <p>
     * 只有当指定的键存在cache中时, 替换才会发生.
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return object=原来被缓存的值; null=指定键不存在cache中
     * @see #setIfAbsent(ICachedKey, Object)
     */
    public Object replace(Object key, Object value);

    /**
     * 替换一个键值对, 并设定过期时间
     * <p>
     * 只有当指定的键存在cache中时, 替换才会发生.
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @param expiry
     *            过期时间(null=默认过期时间)
     * @return object=原来被缓存的值; null=指定键不存在cache中
     */
    public Object replace(Object key, Object value, Date expiry);

    /**
     * 设置一个键值对, 该键值对不会过期
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return true=操作成功;false=操作失败
     */
    public boolean set(Object key, Object value);

    /**
     * 设置一个键值对, 并设定过期时间
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @param expiry
     *            过期时间(null=默认过期时间)
     * @return true=操作成功;false=操作失败
     */
    public boolean set(Object key, Object value, Date expiry);

    /**
     * 设置一个键值对, 并设定存活时间
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @param liveMillis
     *            存活时间(单位:毫秒, null=默认过期时间)
     * @return true=操作成功;false=操作失败
     */
    public boolean set(Object key, Object value, Long liveMillis);

    /**
     * 设置一个键值对, 该键值对不会过期
     * <p>
     * 只有当指定的键没有被cache缓存时, 设置才能成功
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @return object=已存在的值;null=设置操作成功
     */
    public Object setIfAbsent(Object key, Object value);

    /**
     * 设置一个键值对, 并设定过期时间
     * <p>
     * 只有当指定的键没有被cache缓存时, 设置才能成功
     * 
     * @param key
     *            键
     * @param value
     *            值
     * @param expiry
     *            过期时间(null=默认过期时间)
     * @return object=已存在的值;null=设置操作成功
     */
    public Object setIfAbsent(Object key, Object value, Date expiry);

}

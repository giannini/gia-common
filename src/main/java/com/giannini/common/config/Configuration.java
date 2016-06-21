package com.giannini.common.config;

/**
 * 配置文件接口类
 * 
 * @author giannini
 */
public interface Configuration {

    /**
     * 获取版本号
     */
    long getVersion();

    /**
     * 获取配置文件自动重载的时间间隔 （单位毫秒）
     * 
     * @return null,使用默认时间间隔；<=0，不使用自动重载
     */
    Long getScanMillis();

    /**
     * 获取配置文档中的配置根节点
     * 
     * @return 配置根节点
     */
    ElementNode getRootElement();

    /**
     * 载入配置参数
     * <p>
     * 可以被重复调用, 每次载入成功时应该自动更新配置版本号
     * 
     * @throws Exception
     *             载入参数失败
     */
    void load() throws Exception;

    /**
     * 添加一个配置更新监控器
     * 
     * @param watcher
     *            配置更新监控器实例
     * @return true=添加成功; false=该监控器已添加
     */
    boolean addWatcher(ConfigWatcher watcher);

    /**
     * 移除一个配置更新监控器
     * 
     * @param watcher
     *            配置更新监控器实例
     */
    void removeWatcher(ConfigWatcher watcher);

    /**
     * 清除所有配置更新监控器
     */
    void clearWatcher();
}

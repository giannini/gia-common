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
     * 载入配置参数
     * <p>
     * 可以被重复调用, 每次载入成功时应该自动更新配置版本号
     * 
     * @throws Exception
     *             载入参数失败
     */
    void load() throws Exception;

    /**
     * 配置文件更新后，重新加载
     * 
     * @throws Exception
     */
    void change() throws Exception;

}

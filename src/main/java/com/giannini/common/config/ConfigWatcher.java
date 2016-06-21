package com.giannini.common.config;

import org.dom4j.Element;

/**
 * 配置更新器接口
 * 
 * @author giannini
 */
public interface ConfigWatcher {

    /**
     * 配置更新通知
     * 
     * @param node
     *            更新后的配置根节点
     * @param version
     *            更新后的配置版本号
     */
    void notify(Element node, long version);

}

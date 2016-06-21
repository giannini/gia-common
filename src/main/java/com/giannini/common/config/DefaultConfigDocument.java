package com.giannini.common.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缺省配置文件类
 * 
 * @author giannini
 */
public class DefaultConfigDocument implements Configuration {

    /**
     * 日志记录
     */
    private static final Logger logger = LoggerFactory
            .getLogger(DefaultConfigDocument.class);

    /**
     * 系统属性名称,可配置conf默认路径
     */
    public static final String PROPERTY_CONF_HOME = "CONF_HOME";

    /**
     * conf文件的默认路径
     */
    public static final String DEFAULT_CONF_HOME = "/home/gia/conf";

    /**
     * 当前conf文件的目录
     */
    private String confHome = DEFAULT_CONF_HOME;

    /**
     * 配置的根节点
     */
    private volatile ElementNode rootNode = null;

    /**
     * 配置的版本号
     */
    private volatile long version = System.currentTimeMillis();

    private final Set<ConfigWatcher> watcherSet = new HashSet<ConfigWatcher>();

    public DefaultConfigDocument() {
        this.confHome = System.getProperty(PROPERTY_CONF_HOME,
                DEFAULT_CONF_HOME);
    }

    public void load() throws Exception {
        // TODO Auto-generated method stub

    }

    public long getVersion() {
        return version;
    }

    public Long getScanMillis() {
        return 0L;
    }

    public ElementNode getRootElement() {
        return this.rootNode;
    }

    public boolean addWatcher(ConfigWatcher watcher) {
        synchronized (this.watcherSet) {
            return this.watcherSet.add(watcher);
        }
    }

    public void removeWatcher(ConfigWatcher watcher) {
        synchronized (this.watcherSet) {
            this.watcherSet.remove(watcher);
        }
    }

    public void clearWatcher() {
        synchronized (this.watcherSet) {
            this.watcherSet.clear();
        }
    }

    public String getConfHome() {
        return confHome;
    }

    public void setConfHome(String confHome) {
        this.confHome = confHome;
    }

}

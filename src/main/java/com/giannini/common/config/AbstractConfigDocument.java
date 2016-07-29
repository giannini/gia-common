package com.giannini.common.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 缺省配置文件类
 * 
 * @author giannini
 */
public abstract class AbstractConfigDocument implements Configuration {

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
     * 配置文件路径
     */
    protected String configFilePath = null;

    /**
     * 配置的版本号
     */
    private volatile long version = System.currentTimeMillis();

    private final Set<ConfigWatcher> watcherSet = new HashSet<ConfigWatcher>();

    public AbstractConfigDocument() {
        this.confHome = System.getProperty(PROPERTY_CONF_HOME,
                DEFAULT_CONF_HOME);
    }

    public AbstractConfigDocument(String configFile) {
        this();

        File config = new File(configFile);
        if (config.isAbsolute()) {
            this.configFilePath = config.getAbsolutePath();
        } else {
            this.configFilePath = this.getConfHome() + "/" + configFile;
        }
    }

    public long getVersion() {
        return version;
    }

    public Long getScanMillis() {
        return 0L;
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

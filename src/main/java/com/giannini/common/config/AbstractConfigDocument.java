package com.giannini.common.config;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * 配置文件自动更新器
     */
    private Timer timer = null;

    /**
     * 是否需要提供自动更新, 默认支持自动更新
     */
    private boolean needAutoUpdate = true;

    /**
     * 自动更新时间间隔（单位：毫秒），默认6小时检查一次
     */
    private Long updatePeriod = 6 * 60 * 60 * 1000L;

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

    public AbstractConfigDocument(String configFile, boolean isUpdate) {
        this(configFile);
        this.needAutoUpdate = isUpdate;
    }

    public AbstractConfigDocument(String configFile, Long updateInterval) {
        this(configFile);
        if (!updateInterval.equals(Long.MAX_VALUE)) {
            this.needAutoUpdate = true;
        } else {
            this.needAutoUpdate = false;
        }
        this.updatePeriod = updateInterval;
    }

    public long getVersion() {
        return version;
    }

    public Long getScanMillis() {
        return Long.MAX_VALUE;
    }

    public String getConfHome() {
        return confHome;
    }

    public void setConfHome(String confHome) {
        this.confHome = confHome;
    }

    public boolean isNeedAutoUpdate() {
        return needAutoUpdate;
    }

    public void setNeedAutoUpdate(boolean needAutoUpdate) {
        this.needAutoUpdate = needAutoUpdate;
    }

    public Long getUpdatePeriod() {
        return updatePeriod;
    }

    public void setUpdatePeriod(Long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public void change() throws Exception {
        File config = new File(configFilePath);
        if (this.version < config.lastModified()) {
            synchronized (this) {
                version = config.lastModified();
                doLoad();
            }
        }
    }

    protected abstract void doLoad() throws Exception;

    public void load() throws Exception {
        if (needAutoUpdate && this.timer == null) {
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    File config = new File(configFilePath);
                    long newTimeStamp = config.lastModified();
                    if (newTimeStamp > version) {
                        try {
                            change();
                            if (timer == null || !needAutoUpdate
                                    || updatePeriod
                                            .equals(Long.MAX_VALUE)) {
                                this.cancel();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            this.timer = new Timer(true);
            timer.scheduleAtFixedRate(task, updatePeriod, updatePeriod);
        }
    }

}

package com.giannini.common.config;

/**
 * 缺省配置文件类
 * 
 * @author gainnini
 */
public class DefaultConfigDocument implements Configuration {

    public long getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Long getScanMillis() {
        // TODO Auto-generated method stub
        return null;
    }

    public ElementNode getRootElement() {
        // TODO Auto-generated method stub
        return null;
    }

    public void load() throws Exception {
        // TODO Auto-generated method stub

    }

    public boolean addWatcher(ConfigWatcher watcher) {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeWatcher(ConfigWatcher watcher) {
        // TODO Auto-generated method stub

    }

    public void clearWatcher() {
        // TODO Auto-generated method stub

    }

}

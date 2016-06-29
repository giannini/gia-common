package com.giannini.common.cache;

import java.util.Date;

public class EhcacheAdapter implements ICacheService {

    public void disposeAll() {
        // TODO Auto-generated method stub

    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    public boolean delete(Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    public Object get(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean keyExists(Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    public Object replace(Object key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object replace(Object key, Object value, Date expiry) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean set(Object key, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean set(Object key, Object value, Date expiry) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean set(Object key, Object value, Long liveMillis) {
        // TODO Auto-generated method stub
        return false;
    }

    public Object setIfAbsent(Object key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object setIfAbsent(Object key, Object value, Date expiry) {
        // TODO Auto-generated method stub
        return null;
    }

}

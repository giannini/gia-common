package com.giannini.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties格式的配置文件，部分接口可能不可用
 *
 * @author giannini
 */
public class PropertiesConfigDocument extends AbstractConfigDocument {

    private Properties prop = new Properties();

    public PropertiesConfigDocument(String configFile) {
        super(configFile);
    }

    public PropertiesConfigDocument(String configFile, boolean update) {
        super(configFile, update);
    }

    public PropertiesConfigDocument(String configFile, long mills) {
        super(configFile, mills);
    }

    public synchronized void load() throws Exception {
        super.load();
        doLoad();
    }

    /**
     * 强制以boolean类型返回指定的配置参数值
     * 
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        Object value = prop.get(key);
        if (value == null) {
            throw new NullPointerException(key + " no value.");
        }

        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 根据配置参数返回指定类型值，如果不存在该配置，返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }

        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 强制以String类型返回指定的配置参数值
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        Object value = prop.get(key);
        if (value == null) {
            throw new NullPointerException(key + " no value.");
        }

        if (value instanceof String) {
            return (String) value;
        }

        return String.valueOf(value);
    }

    /**
     * 根据配置参数返回指定类型值，如果不存在该配置，返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        Object value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof String) {
            return (String) value;
        }

        return String.valueOf(value);
    }

    /**
     * 强制以int类型返回指定的配置参数值
     * 
     * @param key
     * @return
     */
    public int getInt(String key) {
        Object value = prop.get(key);
        if (value == null) {
            throw new NullPointerException(key + " no value.");
        }

        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        
        return Integer.parseInt(value.toString());
    }

    /**
     * 根据配置参数返回指定类型值，如果不存在该配置，返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key, int defaultValue) {
        Object value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }

        return Integer.parseInt(value.toString());
    }
    
    /**
     * 强制以long类型返回指定的配置参数值
     * 
     * @param key
     * @return
     */
    public long getLong(String key){
        Object value = prop.get(key);
        if (value == null) {
            throw new NullPointerException(key + " no value.");
        }

        if (value instanceof Long) {
            return ((Long) value).longValue();
        }

        return Long.parseLong(value.toString());
    }

    /**
     * 根据配置参数返回指定类型值，如果不存在该配置，返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(String key, long defaultValue) {
        Object value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Long) {
            return ((Long) value).longValue();
        }

        return Long.parseLong(value.toString());
    }

    /**
     * 强制以double类型返回指定的配置参数值
     * 
     * @param key
     * @return
     */
    public double getDouble(String key) {
        Object value = prop.get(key);
        if (value == null) {
            throw new NullPointerException(key + " no value.");
        }

        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }

        return Double.parseDouble(value.toString());
    }

    /**
     * 根据配置参数返回指定类型值，如果不存在该配置，返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(String key, double defaultValue) {
        Object value = prop.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }

        return Double.parseDouble(value.toString());
    }

    @Override
    protected void doLoad() throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(this.configFilePath));
            Properties p = new Properties();
            p.load(is);
            this.prop = p;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}

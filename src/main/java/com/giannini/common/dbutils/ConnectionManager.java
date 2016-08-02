package com.giannini.common.dbutils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * jdbc连接管理（依赖c3p0）
 * 
 * @author giannini
 */
public final class ConnectionManager {

    private static final Logger logger = LoggerFactory
            .getLogger(ConnectionManager.class);

    /**
     * String为应用名
     */
    private static Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

    private static String dbConfigFile = "db.properties";

    private static final String CONF_DB_DRIVE = "drive";

    private static final String CONF_DB_URL = "url";

    private static final String CONF_DB_USER = "user";

    private static final String CONF_DB_PASS = "pass";

    private static final String CONF_DB_POOL_INIT_SIZE = "initsize";

    private static final String CONF_DB_POOL_MIN_SIZE = "initsize";

    private static final String CONF_DB_POOL_MAX_SIZE = "maxsize";

    private static final String CONF_DB_TYPE = "type";

    private ConnectionManager() {};

    /**
     * 从连接池中获取连接
     * 
     * @param prefix
     * @return
     */
    public static DataSource getDataSource(String prefix) {
        DataSource ds = dataSources.get(prefix);
        if (ds == null) {
            synchronized (dataSources) {
                ds = dataSources.get(prefix);
                if (ds == null) {
                    initSource(prefix);
                }
            }
        } else {
            return ds;
        }

        return dataSources.get(prefix);
    }

    public static void closeDataSource(String prefix) {
        DataSource ds = dataSources.get(prefix);
        if (ds == null) {
            return;
        } else {
            synchronized (dataSources) {
                ds = dataSources.get(prefix);
                ComboPooledDataSource source = (ComboPooledDataSource) ds;
                source.close();
                dataSources.remove(prefix);
            }
            logger.info("close DataSource: {}", prefix);
        }
    }

    private static void initSource(String prefix) {
        Properties prop = new Properties();
        FileInputStream fis = null;
        String url = null, user = null;
        try {
            // TODO 换用PropertiesConfigDocument
            fis = new FileInputStream(new File(dbConfigFile));
            prop.load(fis);
            String drive = prop.getProperty(prefix + CONF_DB_DRIVE);
            url = prop.getProperty(prefix + CONF_DB_URL);
            user = prop.getProperty(prefix + CONF_DB_USER);
            String pass = prop.getProperty(prefix + CONF_DB_PASS);
            int initSize = Integer.parseInt(
                    prop.getProperty(prefix + CONF_DB_POOL_INIT_SIZE));
            int minSize = Integer
                    .parseInt(prop.getProperty(prefix + CONF_DB_POOL_MIN_SIZE));
            int maxSize = Integer
                    .parseInt(prop.getProperty(prefix + CONF_DB_POOL_MAX_SIZE));

            /**
             * 初始化连接池
             */
            ComboPooledDataSource source = new ComboPooledDataSource();
            source.setDataSourceName(prefix);
            source.setDriverClass(drive);
            source.setJdbcUrl(url);
            source.setUser(user);
            source.setPassword(pass);
            source.setAutoCommitOnClose(false);
            source.setInitialPoolSize(initSize);
            source.setMinPoolSize(minSize);
            source.setMaxPoolSize(maxSize);
            source.setMaxIdleTime(7200);
            source.setAcquireIncrement(3);
            source.setCheckoutTimeout(10000);
            source.setMaxIdleTimeExcessConnections(3600);
            source.setUnreturnedConnectionTimeout(600);// 10分钟没有释放的连接将强制回收
            source.setDebugUnreturnedConnectionStackTraces(true);

            logger.info("init c3p0 success " + url + " " + user + " " + prefix);
            dataSources.put(prefix, source);
        } catch (Exception e) {
            logger.error("error when init conn pool! " + url + " " + user + " "
                    + prefix, e);
            throw new ExceptionInInitializerError(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}

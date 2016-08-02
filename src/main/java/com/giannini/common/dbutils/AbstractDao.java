package com.giannini.common.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDao implements BaseDao {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(AbstractDao.class);

    /**
     * sql语句执行超时时间
     */
    private static final int TIMEOUT = 5 * 60;

    /**
     * db连接
     */
    private Connection conn;

    /**
     * 自动递交
     */
    private boolean autoCommit = true;

    private long connTime = System.currentTimeMillis();

    /**
     * 准备连接
     * 
     * @throws Exception
     */
    private void prepareConn() throws Exception {
        if (conn == null) {
            try {
                long t1 = System.currentTimeMillis();
                conn = getConnection();
                conn.setAutoCommit(autoCommit);
                logger.debug("prepareConn connTime:" + connTime + ",use time:"
                        + (System.currentTimeMillis() - t1));
            } catch (Exception e) {
                logger.error("prepareConn error ", e);
                if (conn != null) {
                    closeTransaction();
                }
                throw new Exception(e);
            }
        }
    }


    public abstract Connection getConnection() throws SQLException;

    public <T> T query(String sql, ResultSetHandler<T> handler, Object... param)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public int update(String sql, Object... param) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 关闭事务
     * 
     * @throws Exception
     */
    public void closeTransaction() throws Exception {
        if (conn == null) {
            logger.debug("close transaction, conn is null.");
            return;
        }

        logger.debug("close transaction.");
        try {
            DbUtils.close(conn);
            autoCommit = true;
        } catch (Exception ex) {
            logger.error("close transaction error, ", ex);
            throw ex;
        }
    }

    /**
     * 开始事务
     * 
     * @throws Exception
     */
    public void startTransaction() throws Exception {
        autoCommit = false;
        
        logger.debug("start transaction.");
        if (conn != null) {
            try {
                conn.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                logger.error("start transaction error, ", e);
                throw new Exception(e);
            }
        } else {
            logger.warn("start transaction error, conn is null.");
        }
    }

    /**
     * 递交事务
     * 
     * @throws Exception
     */
    public void commit() throws Exception {
        if (conn == null) {
            logger.error("commit error, conn is null.");
            return;
        }

        logger.debug("commit.");
        try {
            conn.commit();
        } catch (Exception ex) {
            logger.error("commit error, ", ex);
            throw ex;
        }
    }

    /**
     * 回滚事务
     * 
     * @throws Exception
     */
    public void rollbackTransaction() throws Exception {
        if (conn == null) {
            logger.error("rollback transaction error, conn is null.");
            return;
        }

        logger.debug("rollback transaction.");
        try {
            DbUtils.rollback(conn);
        } catch (Exception ex) {
            logger.error("rollback transaction error, ", ex);
            throw ex;
        }

    }
}

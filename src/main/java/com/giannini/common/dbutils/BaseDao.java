package com.giannini.common.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * @author giannini
 */
public interface BaseDao {

    /**
     * 获取连接
     * 
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException;

    /**
     * 查询
     * 
     * @param sql
     * @param handler
     * @param param
     * @return
     * @throws Exception
     */
    public <T> T query(String sql, ResultSetHandler<T> handler, Object... param)
            throws Exception;

    /**
     * 更新
     * 
     * @param sql
     * @param param
     * @return
     * @throws Exception
     */
    public int update(String sql, Object... param) throws Exception;

}

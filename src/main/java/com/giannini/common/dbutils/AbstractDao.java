package com.giannini.common.dbutils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO基类
 */
public abstract class AbstractDao {
    private static Logger log = LoggerFactory.getLogger(AbstractDao.class);

    /**
     * sql语句执行超时时间
     */
    private static final int SQL_TIMEOUT = 5 * 60;

    private Connection conn;// Con

    private int commitCount = 0;// 自动提交设置次数

    private boolean autoCommit = true;

    private long connTime = System.currentTimeMillis();

    protected int begin = -1;

    protected int count = -1;

    private String returnColumn;

    private long returnId = -1;

    static {
        // 设置oracle的jdbc返回时间类型使用旧的映射date-java.sql.Timestamp
        // System.setProperty("oracle.jdbc.V8Compatible", "true");
    }

    /**
     * DB操作
     */
    private QueryRunner run = new QueryRunner();

    public void setRun(QueryRunner run) {
        this.run = run;
    }

    /**
     * 设置分页参数
     * 
     * @param begin
     * @param count
     */
    protected void setLimit(int begin, int count) {
        this.begin = begin;
        this.count = count;
    }

    /**
     * 设置返回字段名
     * 
     * @param returnColumn
     */
    protected void setReturnColumn(String returnColumn) {
        this.returnColumn = returnColumn;
    }

    /**
     * 获得返回的id
     * 
     * @return
     */
    protected long getReturnId() {
        return returnId;
    }

    /**
     * 准备连接
     * 
     * @throws BizException
     */
    private void prepareConn() throws Exception {
        if (conn == null) {
            try {
                long t1 = System.currentTimeMillis();
                conn = getConnection();
                conn.setAutoCommit(autoCommit);
                log.debug("prepareConn connTime:" + connTime + ",use time:"
                        + (System.currentTimeMillis() - t1));
            } catch (Exception e) {
                log.error("prepareConn error ", e);
                if (conn != null) {
                    closeTransaction();
                }
                throw new Exception(e);
            }
        }
    }

    /**
     * 获得当前dao需要的链接
     * 
     * @return
     */
    protected abstract Connection getConnection() throws SQLException;

    /**
     * 增加分页
     * 
     * @param sql
     * @return
     */
    protected abstract String addLmit(String sql);

    /**
     * 查询操作
     * 
     * @param sql
     * @param handle
     * @param param
     * @return
     * @throws BizException
     */
    protected <T> T query(String sql, ResultSetHandler<T> handle,
            Object... param) throws Exception {
        prepareConn();

        long beginTime = System.currentTimeMillis();
        try {
            // 如果有分页，调用分页方法处理
            if (begin > -1 && count > -1) {
                sql = addLmit(sql);
            }

            // 修订参数类型
            fixParam(param);

            T result = run.query(conn, sql, handle, param);
            log.debug("query success connTime:" + connTime + ",sql:" + sql
                    + ",param:" + convertParamToString(param) + ",time:"
                    + getTime(beginTime));

            return result;
        } catch (Throwable e) {
            log.error("query error connTime:" + connTime + ",sql:" + sql
                    + ",param:" + convertParamToString(param) + ",time:"
                    + getTime(beginTime), e);
            throw new Exception(e);
        } finally {
            begin = -1;
            count = -1;
            close();
        }
    }

    private long getTime(long beginTime) {
        return System.currentTimeMillis() - beginTime;
    }

    /**
     * 获得结果里的数量值
     * 
     * @param sql
     * @param param
     * @return
     * @throws BizException
     */
    protected int getInt(String sql, Object... param) throws Exception {
        ScalarHandler handle = new ScalarHandler();
        Object obj = query(sql, handle, param);
        if (obj == null) {
            return 0;
        }

        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        }
        return Integer.parseInt(String.valueOf(obj));
    }

    /**
     * 获得长整型结果
     * 
     * @param sql
     * @param param
     * @return
     * @throws BizException
     */
    protected long getLong(String sql, Object... param)
            throws Exception {
        ScalarHandler handle = new ScalarHandler();
        Object obj = query(sql, handle, param);
        if (obj == null) {
            return 0;
        }

        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }
        return Long.parseLong(String.valueOf(obj));
    }

    /**
     * 判断符合条件的记录是是否存在
     * 
     * @param sql
     * @param param
     * @return
     * @throws BizException
     */
    // protected boolean isExist(String sql, Object... param)
    // throws Exception {
    // ExistHandler handle = new ExistHandler();
    // setLimit(0, 1);
    // return query(sql, handle, param);
    // }

    /**
     * 获得bean对象
     * 
     * @param sql
     * @param type
     * @param param
     * @return
     * @throws BizException
     */
    protected <T> T getBean(String sql, Class<T> type, Object... param)
            throws Exception {
        BeanHandler<T> handle = new BeanHandler<T>(type);
        return query(sql, handle, param);
    }

    /**
     * 获得bean对象列表
     * 
     * @param sql
     * @param type
     * @param param
     * @return
     * @throws BizException
     */
    protected <T> List<T> getBeanList(String sql, Class<T> type,
            Object... param) throws Exception {
        BeanListHandler<T> handle = new BeanListHandler<T>(type);
        return query(sql, handle, param);
    }

    /**
     * 获取String列表
     * 
     * @param sql
     * @param param
     * @return
     * @throws BizException
     */
    // protected List<String> getStringList(String sql, Object... param)
    // throws Exception {
    // StringListhandler handle = new StringListhandler();
    // List<String> result = query(sql, handle, param);
    // return result;
    // }

    /**
     * 获取String结果
     * 
     * @param sql
     * @param param
     * @return
     * @throws BizException
     */
    // protected String getString(String sql, Object... param)
    // throws Exception {
    // StringHandler handle = new StringHandler();
    // String str = query(sql, handle, param);
    // return str;
    // }

    /**
     * 将参数转换为字符串
     * 
     * @param param
     * @return
     */
    private String convertParamToString(Object[] param) {
        int len = param == null ? 0 : param.length;
        StringBuilder sb = new StringBuilder(len * 20);
        sb.append("[");
        if (param != null && param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                sb.append(param[i]).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 在模糊查询关键字两边加上like条件需要的符号
     * 
     * @param key
     * @return
     */
    protected String addLike(String key) {
        return "%" + key + "%";
    }

    /**
     * 模糊查询关键字以domain结尾，like条件左边加上必须的符号
     * 
     * @param domain
     * @return
     */
    protected String addDomainLike(String domain) {
        return "%@" + domain;
    }

    /**
     * 更新操作
     * 
     * @param sql
     * @param handle
     * @param param
     * @return
     * @throws BizException
     */
    protected int update(String sql, Object... param) throws Exception {
        prepareConn();

        long begin = System.currentTimeMillis();
        PreparedStatement ps = null;
        try {
            // 判断是否需要返回某个字段的值
            // if (StringUtils.isEmpty(returnColumn)) {
            // ps = conn.prepareStatement(sql);
            // } else {
            // ps = conn.prepareStatement(sql, new String[] {
            // returnColumn
            // });
            // }

            // 设置更新超时时间
            ps.setQueryTimeout(SQL_TIMEOUT);

            // 修订参数类型
            fixParam(param);

            // 设置参数
            run.fillStatement(ps, param);

            // 执行更新
            int result = ps.executeUpdate();

            // 如果设置了返回字段属性，取得返回的值
            // if (!StringUtils.isEmpty(returnColumn)) {
            // ResultSet rs = ps.getGeneratedKeys();
            // if (rs.next()) {
            // returnId = rs.getLong(1);
            // }
            // }
            long time = System.currentTimeMillis() - begin;
            String params = convertParamToString(param);
            // log.info(
            // "update success connTime:" + connTime + ",sql:" + sql
            // + ",param:" + params
            // + (StringUtils.isEmpty(returnColumn) ? ""
            // : ",returnId:" + returnId)
            // + ",time:" + time);

            return result;
        } catch (Throwable e) {
            long time = System.currentTimeMillis() - begin;
            String params = convertParamToString(param);
            log.error("update error connTime:" + connTime + ",sql:" + sql
                    + ",param:" + params + ",time:" + time, e);
            throw new Exception(e);
        } finally {
            returnColumn = null;
            try {
                DbUtils.closeQuietly(ps);
            } finally {
                close();
            }
        }
    }

    /**
     * 修复参数类型
     * 
     * @param param
     */
    private void fixParam(Object[] param) {
        if (param == null)
            return;
        for (int i = 0; i < param.length; i++) {
            Object obj = param[i];
            // 日期转换为Timestamp类型
            if (obj instanceof Date) {
                param[i] = new Timestamp(((Date) obj).getTime());
            }
        }
    }

    /**
     * 关闭连接
     * 
     * @throws BizException
     */
    private void close() throws Exception {
        if (conn != null && autoCommit) {
            closeTransaction();
        }
    }

    /**
     * 关闭事务
     * 
     * @throws BizException
     * @throws SQLException
     */
    public void closeTransaction() throws Exception {
        if (conn == null) {
            log.warn("closeTransaction error conn is null");
            return;
        }

        // 事务次数小于1时执行关闭事务操作
        if (commitCount <= 0) {
            try {
                DbUtils.close(conn);
                conn = null;
                commitCount = 0;
                autoCommit = true;
                log.debug("closeTransaction success connTime:" + connTime);
            } catch (SQLException e) {
                log.error("closeTransaction error connTime:" + connTime, e);
                throw new Exception(e);
            }
        } else {
            log.debug("closeTransaction conn:" + connTime + ",commitCount:"
                    + commitCount);
        }
    }

    /**
     * 回滚事务。
     * 
     * @throws BizException
     */
    public void rollbackTransaction() throws Exception {
        // 记录一次提交次数
        commitCount--;

        if (conn == null) {
            log.error("rollbackTransaction error conn is null connTime:"
                    + connTime);
            return;
        }

        if (commitCount == 0) {
            try {
                DbUtils.rollback(conn);
                log.debug("rollbackTransaction success connTime:" + connTime);
            } catch (SQLException e) {
                log.error("rollbackTransaction error connTime:" + connTime, e);
                throw new Exception(e);
            }
        } else {
            log.debug("rollbackTransaction conn:" + connTime + ",commitCount:"
                    + commitCount);
        }
    }

    /**
     * 提交事务
     * 
     * @throws BizException
     */
    public void commit() throws Exception {
        // 记录一次提交次数
        commitCount--;

        if (conn == null) {
            log.error("commit error conn is null connTime:" + connTime);
            return;
        }

        // 事务提交次数满了，执行提交操作
        if (commitCount == 0) {
            try {
                conn.commit();
                log.debug("commit success connTime:" + connTime);
            } catch (SQLException e) {
                log.error("commit error connTime:" + connTime, e);
                throw new Exception(e);
            }
        } else {
            log.debug(
                    "commit conn:" + connTime + ",commitCount:" + commitCount);
        }
    }

    /**
     * 开启事务
     * 
     * @throws SQLException
     * @throws BizException
     */
    public void startTransaction() throws Exception {
        autoCommit = false;

        // 记录事务启动次数
        commitCount++;

        if (conn != null) {
            try {
                conn.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                log.error("startTransaction error connTime:" + connTime, e);
                throw new Exception(e);
            }
        }
        log.debug("startTransaction conn:" + connTime);
    }
}

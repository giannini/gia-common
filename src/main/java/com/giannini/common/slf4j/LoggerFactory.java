package com.giannini.common.slf4j;

import org.slf4j.Logger;

public class LoggerFactory {

    private LoggerFactory() {
    }

    /**
     * 获取某个指定类的Logger
     * 
     * @param clazz
     * @return
     */
    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取某个指定名的Logger
     * 
     * @param name
     * @return
     */
    public static Logger getLogger(String name) {
        return org.slf4j.LoggerFactory.getLogger(name);
    }

    /**
     * 获取root logger
     * 
     * @return
     */
    public static Logger getRootLogger() {
        return org.slf4j.LoggerFactory.getLogger("ROOT");
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}

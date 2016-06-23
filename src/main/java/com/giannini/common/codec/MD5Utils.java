package com.giannini.common.codec;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5相关方法
 * 
 * @author giannini
 */
public final class MD5Utils {

    private static ThreadLocal<MessageDigest> md5 = new ThreadLocal<MessageDigest>() {

        @Override
        protected final MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("no md5 algo");
            }
        }
    };

    /**
     * 默认的密码字符串组合，apache校验下载的文件的正确性用的就是默认的这个组合
     */
    static char[] hexDigits = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F'
    };

    private MD5Utils() {}

    /**
     * 计算字节数组的md5值
     * 
     * @param key
     * @return
     */
    public static byte[] MD5(byte[] key) {
        MessageDigest digest = md5.get();
        digest.reset();
        digest.update(key);

        return digest.digest();
    }

    /**
     * 计算字符串的MD5值
     * 
     * @param key
     * @return
     */
    public static byte[] MD5(String key) {
        MessageDigest digest = md5.get();
        digest.reset();
        digest.update(key.getBytes());

        return digest.digest();
    }

    /**
     * 获取字符串的md5值(字符串形式)
     * 
     * @param key
     * @return
     */
    public static String getMD5(String key) {
        MessageDigest digest = md5.get();
        digest.reset();
        digest.update(key.getBytes());

        return byteArrayToString(digest.digest());
    }

    /**
     * 获取字节数组的md5值(字符串形式)
     * 
     * @param key
     * @return
     */
    public static String getMD5(byte[] key) {
        MessageDigest digest = md5.get();
        digest.reset();
        digest.update(key);

        return byteArrayToString(digest.digest());
    }

    /**
     * 获取指定文件的md5值(字符串形式)
     * 
     * @param filepath
     * @return
     * @throws Exception
     */
    public static String fileMD5(String filepath) throws Exception {

        int bufferSize = 4 * 1024;
        byte[] buffer=  new byte[bufferSize];
        DigestInputStream dis = null;
        FileInputStream fis = null;
        MessageDigest digest = md5.get();
        digest.reset();
        
        try {
            fis = new FileInputStream(filepath);
            dis = new DigestInputStream(fis, digest);
            
            // read过程中处理md5
            while (dis.read(buffer) > 0);
            
            // 获取处理完后的MessageDigest
            digest = dis.getMessageDigest();
            
            return byteArrayToString(digest.digest());

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    /**
     * md5的字节数组转换成字符串形式
     * 
     * @param array
     * @return
     */
    private static String byteArrayToString(byte[] array)  {
        char[] tempArray = new char[array.length * 2];
        int index = 0;
        for (byte b: array) {
            tempArray[index++] = hexDigits[(b & 0xf0) >> 4];
            tempArray[index++] = hexDigits[b & 0xf];
        }
        return new String(tempArray);
    }
}

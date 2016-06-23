package com.giannini.test.codec;

import org.junit.Assert;
import org.junit.Test;

import com.giannini.common.codec.MD5Utils;

public class MD5Test {

    private static String testString = "lalilulelo哎呀喂哟";

    private static String testFile = "pom.xml";

    @Test
    public void testGetMD5ByteArray() {
        byte[] a1 = MD5Utils.MD5(testString);
        byte[] a2 = MD5Utils.MD5(testString.getBytes());
        Assert.assertEquals(a1.length, a2.length);
        if (a1.length == a2.length) {
            for (int i = 0; i < a1.length; i++) {
                Assert.assertEquals(a1[i], a2[i]);
            }
        }
    }

    @Test
    public void testGetMD5String() {
        String s1 = MD5Utils.getMD5(testString);
        String s2 = MD5Utils.getMD5(testString.getBytes());
        Assert.assertEquals(s1, s2);

        byte[] a = MD5Utils.MD5(testString);
        String s3 = new String(a);
        Assert.assertNotEquals(s1, s3);
    }

    @Test
    public void testMD5File() throws Exception {
        String s = MD5Utils.fileMD5(testFile);
        Assert.assertNotNull(s);
        System.out.println(s);
    }

}

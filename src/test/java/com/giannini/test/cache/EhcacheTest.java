package com.giannini.test.cache;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.giannini.common.cache.EhcacheHelper;
import com.giannini.common.cache.ICacheService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

public class EhcacheTest {

    private static final String confPath = "conf/ehcache.xml";

    private List<ICacheService> caches = new LinkedList<ICacheService>();

    @Before
    public void init() throws Exception {
        Configuration configuration = ConfigurationFactory
                .parseConfiguration(new File(confPath));
        Map<String, CacheConfiguration> cacheConfigs = configuration
                .getCacheConfigurations();
        for (CacheConfiguration cacheConfig: cacheConfigs.values()) {
            ICacheService cache = EhcacheHelper.createEhcache(configuration,
                    cacheConfig);
            Assert.assertNotNull(cache);
            System.out.println(cache.getName());
            caches.add(cache);
        }
    }

    @Test
    public void testEhcache() throws InterruptedException {

        testSetAndGet();

        testDelete();

        testExpire();
    }

    public void testSetAndGet() {
        // 此处不做过期测试
        ICacheService cache = caches.get(1);

        // 普通set
        cache.set("hello", "world");
        Assert.assertTrue(cache.keyExists("hello"));
        Assert.assertEquals("world", cache.get("hello"));

        // setIfAbsent
        Assert.assertEquals("world", cache.setIfAbsent("hello", "new world"));
        Assert.assertNull(cache.setIfAbsent("goodbye", "world"));

        // replace
        Assert.assertEquals("world", cache.replace("hello", "new world"));
        Assert.assertNull(cache.replace("fuck", "old world"));

        clear(cache, "hello");

    }

    public void testDelete() {
        ICacheService cache = caches.get(0);
        String key1 = "pokemon", value1 = "go";
        String key2 = "pikachu", value2 = "back";

        cache.set(key1, value1);
        Assert.assertEquals(value1, cache.get(key1));

        Assert.assertFalse(cache.delete(key2));
        Assert.assertTrue(cache.delete(key1));

        Assert.assertNull(cache.setIfAbsent(key1, value2));
        Assert.assertEquals(value2, cache.get(key1));

        clear(cache, key1);

    }

    public void testExpire() throws InterruptedException {
        // 配置文件中修改cache1的time2live为10s
        ICacheService cache = caches.get(0);
        String key = "tiki", value = "taka";
        int timeToLiveSecond = 5;

        cache.set(key, value, timeToLiveSecond * 1000L);
        Thread.sleep(6000);
        Assert.assertNull(cache.get(key));
        Assert.assertFalse(cache.keyExists(key));
        
        cache.set(key, value);
        Date expiry = new Date(System.currentTimeMillis() + timeToLiveSecond * 1000L);
        cache.replace(key, value, expiry);
        Thread.sleep(6000);
        Assert.assertNull(cache.get(key));
        Assert.assertFalse(cache.keyExists(key));
        
        Date expiry2 = new Date(
                System.currentTimeMillis() + timeToLiveSecond * 1000L);
        cache.setIfAbsent(key, value, expiry2);
        Thread.sleep(6000);
        Assert.assertNull(cache.get(key));
        Assert.assertFalse(cache.keyExists(key));

        // 测试默认值10s
        cache.set(key, value);
        Thread.sleep(6000);
        Assert.assertEquals(value, cache.get(key));
        Assert.assertTrue(cache.keyExists(key));
        Thread.sleep(4500);
        Assert.assertNull(cache.get(key));
        Assert.assertFalse(cache.keyExists(key));

        clear(cache, null);

    }

    public void clear(ICacheService cache, String key) {
        cache.clear();
        if (key != null) {
            Assert.assertFalse(cache.keyExists(key));
            Assert.assertNull(cache.get(key));
        }
    }

    @After
    public void shutdown() {
        for (ICacheService cache: caches) {
            cache.dispose();
        }
        caches.clear();
    }


    public static void main(String[] args) {
        // Create a cache manager
        final CacheManager cacheManager = CacheManager
                .newInstance("conf/ehcache.xml");

        // create the cache called "hello-world"
        final Cache cache = cacheManager.getCache("memoryCache");

        // create a key to map the data to
        final String key = "greeting";

        // Create a data element
        final Element putGreeting = new Element(key, "Hello, World!");

        // Put the element into the data store
        cache.put(putGreeting);

        // Retrieve the data element
        final Element getGreeting = cache.get(key);

        // Print the value
        System.out.println(getGreeting.getObjectValue());
    }
}

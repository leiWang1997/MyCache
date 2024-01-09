package com.wang.cache.core.bs;

import com.wang.cache.api.ICache;
import com.wang.cache.core.listener.MyRemoveListener;
import com.wang.cache.core.listener.MySlowListener;
import com.wang.cache.core.support.evict.CacheEvicts;
import com.wang.cache.core.support.load.CacheLoads;
import com.wang.cache.core.support.load.MyLoad;
import com.wang.cache.core.support.persist.CachePersists;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 缓存引导类测试
 */

public class CacheBsTest {

    /**
     * 大小指定测试
     * @since 0.0.2
     */
    @Test
    public void helloTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 过期测试
     * @since 0.0.3
     */
    @Test
    public void expireTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");

        cache.expire("1", 40);
        Assert.assertEquals(2, cache.size());

        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(1, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 加载接口测试
     * @since 0.0.7
     */
    @Test
    public void loadTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(new MyLoad())
                .build();

        Assert.assertEquals(2, cache.size());
    }

    /**
     * 持久化接口测试
     * @since 0.0.7
     */
    @Test
    public void persistRdbTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(new MyLoad())
                .persist(CachePersists.<String, String>dbJson("1.rdb"))
                .build();

        Assert.assertEquals(2, cache.size());
        TimeUnit.SECONDS.sleep(6);
    }


    /**
     * 加载接口测试
     * @since 0.0.8
     */
    @Test
    public void loadDbJsonTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(CacheLoads.<String, String>dbJson("1.rdb"))
                .build();

        Assert.assertEquals(2, cache.size());
    }

    /**
     * 缓存删除监听器
     * @since 0.0.6
     */
    @Test
    public void cacheRemoveListenerTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(1)
                .addRemoveListener(new MyRemoveListener<String, String>())
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
    }

    /**
     * 慢日志接口测试
     * @since 0.0.9
     */
    @Test
    public void slowLogTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .addSlowListener(new MySlowListener())
                .build();

        cache.put("1", "2");
        cache.get("1");
    }

    /**
     * 持久化 AOF 接口测试
     * @since 0.0.10
     */
    @Test
    public void persistAofTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .persist(CachePersists.<String, String>aof("1.aof"))
                .build();

        cache.put("1", "1");
        cache.expire("1", 10);
        cache.remove("2");

        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 加载 AOF 接口测试
     * @since 0.0.10
     */
    @Test
    public void loadAofTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(CacheLoads.<String, String>aof("default.aof"))
                .build();

        Assert.assertEquals(1, cache.size());
        System.out.println(cache.keySet());
    }

    @Test
    public void lruDoubleListMapTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lruDoubleListMap())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.get("B");
        cache.put("D", "LRU");
        cache.put("E", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LinkedHashMap 实现
     * @since 0.0.12
     */
    @Test
    public void lruLinkedHashMapTest()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lruLinkedHashMap())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LRU 2Q 实现
     * @since 0.0.13
     */
    @Test
    public void lruQ2Test()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru2Q())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LRU-2 实现
     * @since 0.0.13
     */
    @Test
    public void lru2Test()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru2())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

    /**
     * 基于 LFU 实现
     * @since 0.0.14
     */
    @Test
    public void lfuTest()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lfu())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.put("D", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

}

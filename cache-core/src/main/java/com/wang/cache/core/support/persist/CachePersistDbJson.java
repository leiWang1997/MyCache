package com.wang.cache.core.support.persist;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICachePersist;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CachePersistDbJson<K,V> implements ICachePersist<K,V> {

    /**
     * 数据库路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CachePersistDbJson(String dbPath) {
        this.dbPath = dbPath;
    }


    @Override
    public void persist(ICache cache) {
        Set<Map.Entry<K,V>> entrySet = cache.entrySet();

        // 创建文件
        FileUtil.createFile(dbPath);
        // 清空文件
        FileUtil.truncate(dbPath);

        for (Map.Entry<K, V> kvEntry : entrySet) {
            K key = kvEntry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            V value = kvEntry.getValue();
            PersistRdbEntry<K, V> persistRdbEntry = new PersistRdbEntry<>();
            persistRdbEntry.setKey(key);
            persistRdbEntry.setExpire(expireTime);
            persistRdbEntry.setValue(value);

            String line = JSON.toJSONString(persistRdbEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);
        }
    }

    @Override
    public long delay() {
        return 5;
    }

    @Override
    public long period() {
        return 5;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}

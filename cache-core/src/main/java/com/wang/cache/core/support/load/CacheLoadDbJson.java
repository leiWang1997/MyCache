package com.wang.cache.core.support.load;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheLoad;
import com.wang.cache.core.support.persist.PersistRdbEntry;

import java.util.List;

public class CacheLoadDbJson<K, V> implements ICacheLoad<K, V>  {

    private static final Log log = LogFactory.getLog(CacheLoadDbJson.class);

    /**
     * 文件路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CacheLoadDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        for (String line : lines) {
            if(StringUtil.isEmpty(line)) continue;

            PersistRdbEntry<K, V> persistRdbEntry = JSON.parseObject(line, PersistRdbEntry.class);

            K key = persistRdbEntry.getKey();
            V value = persistRdbEntry.getValue();
            Long expireTime = persistRdbEntry.getExpire();

            cache.put(key, value);

            if(expireTime != null){
                cache.expire(key, expireTime);
            }
        }
    }
}

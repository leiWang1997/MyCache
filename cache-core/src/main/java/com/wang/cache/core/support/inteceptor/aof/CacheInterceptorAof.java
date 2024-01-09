package com.wang.cache.core.support.inteceptor.aof;

import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheInterceptor;
import com.wang.cache.api.ICacheInterceptorContext;
import com.wang.cache.api.ICachePersist;
import com.wang.cache.core.support.persist.CachePersistAof;
import com.wang.cache.core.support.persist.PersistAofEntry;

public class CacheInterceptorAof<K,V> implements ICacheInterceptor<K,V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorAof.class);


    @Override
    public void before(ICacheInterceptorContext context) {

    }

    @Override
    public void after(ICacheInterceptorContext context) {
        // 持久化类
        ICache<K,V> cache = context.cache();
        ICachePersist<K,V> persist = cache.persist();

        if(persist instanceof CachePersistAof) {
            CachePersistAof<K,V> cachePersistAof = (CachePersistAof<K,V>) persist;

            String methodName = context.method().getName();
            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(context.params());

            String json = JSON.toJSONString(aofEntry);

            // 直接持久化
            log.debug("AOF 开始追加文件内容：{}", json);
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}", json);
        }
    }
}

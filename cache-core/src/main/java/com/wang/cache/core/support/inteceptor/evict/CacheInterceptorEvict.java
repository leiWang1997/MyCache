package com.wang.cache.core.support.inteceptor.evict;

import com.wang.cache.api.ICacheEvict;
import com.wang.cache.api.ICacheInterceptor;
import com.wang.cache.api.ICacheInterceptorContext;

import java.lang.reflect.Method;

public class CacheInterceptorEvict<K, V> implements ICacheInterceptor<K, V> {

    @Override
    public void before(ICacheInterceptorContext context) {

    }

    @Override
    public void after(ICacheInterceptorContext context) {
        ICacheEvict<K,V> evict = context.cache().evict();

        Method method = context.method();
        final K key = (K) context.params()[0];
        if("remove".equals(method.getName())) {
            evict.removeKey(key);
        } else {
            evict.updateKey(key);
        }
    }
}

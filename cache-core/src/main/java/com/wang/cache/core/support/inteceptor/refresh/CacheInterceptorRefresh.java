package com.wang.cache.core.support.inteceptor.refresh;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheInterceptor;
import com.wang.cache.api.ICacheInterceptorContext;

/**
 * 刷新
 *
 * @author binbin.hou
 * @since 0.0.5
 */
public class CacheInterceptorRefresh<K,V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorRefresh.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
        log.debug("Refresh start");
        final ICache<K,V> cache = context.cache();
        cache.expire().refreshExpire(cache.keySet());
    }

    @Override
    public void after(ICacheInterceptorContext<K,V> context) {
    }

}

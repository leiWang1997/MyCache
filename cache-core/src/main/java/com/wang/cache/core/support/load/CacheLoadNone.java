package com.wang.cache.core.support.load;

import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheLoad;

/**
 * 加载策略-无
 * @author binbin.hou
 * @since 0.0.7
 */
public class CacheLoadNone<K,V> implements ICacheLoad<K,V> {

    @Override
    public void load(ICache<K, V> cache) {
        //nothing...
    }

}

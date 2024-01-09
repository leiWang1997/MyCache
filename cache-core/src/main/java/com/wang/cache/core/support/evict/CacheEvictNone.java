package com.wang.cache.core.support.evict;

import com.wang.cache.api.ICacheEntry;
import com.wang.cache.api.ICacheEvictContext;

/**
 * 丢弃策略
 * @author binbin.hou
 * @since 0.0.2
 */
public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    public ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }

}

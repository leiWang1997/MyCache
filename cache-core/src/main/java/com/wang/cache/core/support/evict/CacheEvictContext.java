package com.wang.cache.core.support.evict;

import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheEvictContext;

public class CacheEvictContext<K, V> implements ICacheEvictContext {

    private K key;

    private int size;

    private ICache<K, V> cache;

    public CacheEvictContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    public CacheEvictContext<K, V> key(K key) {
        this.key = key;
        return this;
    }

    public CacheEvictContext<K, V> size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public ICache<K, V> cache() {
        return cache;
    }

    @Override
    public int size() {
        return size;
    }


}

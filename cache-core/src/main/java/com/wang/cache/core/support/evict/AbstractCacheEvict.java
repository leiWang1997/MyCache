package com.wang.cache.core.support.evict;

import com.wang.cache.api.ICacheEntry;
import com.wang.cache.api.ICacheEvict;
import com.wang.cache.api.ICacheEvictContext;

public abstract class AbstractCacheEvict<K, V> implements ICacheEvict<K, V> {

    @Override
    public ICacheEntry<K, V> evict(ICacheEvictContext context){
        return doEvict(context);
    }

    public abstract ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key) {

    }

    @Override
    public void removeKey(K key) {

    }
}

package com.wang.cache.core.support.evict;

import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheEntry;
import com.wang.cache.api.ICacheEvictContext;
import com.wang.cache.core.model.CacheEntry;

import java.util.LinkedList;

public class CacheEvictLru<K, V> extends AbstractCacheEvict<K, V> {

    private final LinkedList<K> list = new LinkedList<>();


    @Override
    public ICacheEntry<K, V> doEvict(ICacheEvictContext context) {

        ICacheEntry<K, V> result = null;
        ICache<K, V> cache = context.cache();

        // 超过限制就删除
        if(cache.size() >= context.size()){
            K evictKey = list.removeLast();
            V evictValue = cache.remove(evictKey);
            result =  new CacheEntry<>(evictKey, evictValue);
        }else{
            updateKey((K)context.key());
        }


        return result;
    }

    @Override
    public void updateKey(K key) {
        this.list.remove(key);
        this.list.add(0, key);
    }

    @Override
    public void removeKey(K key) {
        this.list.remove(key);
    }
}

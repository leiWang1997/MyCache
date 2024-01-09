package com.wang.cache.core.core;


import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.wang.cache.annotation.CacheInterceptor;
import com.wang.cache.api.*;
import com.wang.cache.core.constant.enums.CacheRemoveType;
import com.wang.cache.core.exception.CacheRuntimeException;
import com.wang.cache.core.support.evict.CacheEvictContext;
import com.wang.cache.core.support.expire.CacheExpire;
import com.wang.cache.core.support.listener.remove.CacheRemoveListenerContext;
import com.wang.cache.core.support.persist.InnerCachePersist;
import com.wang.cache.core.support.proxy.CacheProxy;

import java.util.*;

public class Cache<K, V> implements ICache<K, V> {


    /**
     * map 信息
     * @since 0.0.2
     */
    private Map<K,V> map;

    /**
     * 大小限制
     * @since 0.0.2
     */
    private int sizeLimit;

    /**
     * 驱除策略
     * @since 0.0.2
     */
    private ICacheEvict<K,V> evict;

    /**
     * 过期策略
     * 暂时不做暴露
     * @since 0.0.3
     */
    private ICacheExpire<K,V> expire;

    /**
     * 加载类
     * @since 0.0.7
     */
    private ICacheLoad<K,V> load;

    /**
     * 持久化
     * @since 0.0.8
     */
    private ICachePersist<K,V> persist;

    /**
     * 删除监听类
     * @since 0.0.6
     */
    private List<ICacheRemoveListener<K,V>> removeListeners;

    /**
     * 慢日志监听类
     * @since 0.0.9
     */
    private List<ICacheSlowListener> slowListeners;

    public Cache<K, V> map(Map map){
        this.map = map;
        return this;
    }

    /**
     * 设置大小限制
     * @param sizeLimit 大小限制
     * @return this
     */
    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    /**
     * 设置驱除策略
     * @param cacheEvict 驱除策略
     * @return this
     * @since 0.0.8
     */
    public Cache<K, V> evict(ICacheEvict<K, V> cacheEvict) {
        this.evict = cacheEvict;
        return this;
    }

    public Cache<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    /**
     * 设置持久化策略
     * @param persist 持久化
     * @since 0.0.8
     */
    public void persist(ICachePersist<K, V> persist) {
        this.persist = persist;
    }

    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }

    public Cache<K, V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }

    @Override
    public ICacheExpire<K, V> expire() {
        return this.expire;
    }

    /**
     * 获取驱除策略
     * @return 驱除策略
     * @since 0.0.11
     */
    @Override
    public ICacheEvict<K, V> evict() { return this.evict; }

    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }

    @Override
    public List<ICacheSlowListener> slowListeners() { return slowListeners;
    }

    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    @Override
    public ICachePersist<K, V> persist() { return persist; }

    @Override
    @CacheInterceptor(refresh = true)
    public int size() {
        return map.size();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @CacheInterceptor(refresh = true, evict = true)
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(refresh = true, aof = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * 初始化
     * @since 0.0.7
     */
    public void init() {
        this.expire = new CacheExpire<>(this);
        this.load.load(this);

        // 初始化持久化
        if(this.persist != null) {
            new InnerCachePersist<>(this, persist);
        }
    }

    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills) {
        long expireTime = System.currentTimeMillis()+timeInMills;
        // 使用代理调用
        Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cachePoxy.expireAt(key, expireTime);
    }

    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire().expire(key, timeInMills);
        return this;
    }


    @Override
    @CacheInterceptor(evict = true)
    public V get(Object key) {
        K expireKey = (K) key;
        this.expire().refreshExpire(Collections.singletonList(expireKey));
        return map.get(key);
    }

    @Override
    @CacheInterceptor(aof = true, evict = true)
    public V put(K key, V value){
        /**
         * 1.put之前做数据淘汰，创建一个淘汰接口，方便实现各种方式的淘汰策略
         */
        CacheEvictContext<K, V> context = new CacheEvictContext();
        context.key(key).size(sizeLimit).cache(this);
        ICacheEntry<K, V> evictEntry = this.evict.evict(context);
        /**
         * 在淘汰之后对删除的数据做监听，这里没法采用aop，只能耦合，
         * 除了这里在过期数据删除的时候也需要进行删除监听
         */
        if(ObjectUtil.isNotNull(evictEntry)){
            ICacheRemoveListenerContext<K, V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(evictEntry.key())
                    .value(evictEntry.value()).type(CacheRemoveType.EVICT.code());
            for (ICacheRemoveListener<K, V> removeListener : context.cache().removeListeners()) {
                removeListener.listen(removeListenerContext);
            }
        }

        /**
         * 2.淘汰后判断缓存容量情况
         */
        if(isSizeLimit()) {
            throw new CacheRuntimeException("当前队列已满，数据添加失败！");
        }

        return map.put(key, value);
    }

    /**
     * 是否已经达到大小最大的限制
     * @return 是否限制
     * @since 0.0.2
     */
    private boolean isSizeLimit() {
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }

}

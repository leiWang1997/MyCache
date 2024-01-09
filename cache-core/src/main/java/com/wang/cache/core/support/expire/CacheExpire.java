package com.wang.cache.core.support.expire;

import com.github.houbb.heaven.util.util.CollectionUtil;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheExpire;
import com.wang.cache.api.ICacheRemoveListener;
import com.wang.cache.api.ICacheRemoveListenerContext;
import com.wang.cache.core.constant.enums.CacheRemoveType;
import com.wang.cache.core.support.listener.remove.CacheRemoveListenerContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CacheExpire<K, V> implements ICacheExpire<K, V> {

    /**
     * 单次清空的数量限制
     * @since 0.0.3
     */
    private static final int LIMIT = 100;

    /**
     * 过期 map
     *
     * 空间换时间
     * @since 0.0.3
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    /**
     * 缓存实现
     * @since 0.0.3
     */
    private final ICache<K,V> cache;

    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public CacheExpire(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    public class ExpireThread implements Runnable{

        @Override
        public void run() {
            if(expireMap.isEmpty()){
                return;
            }

            int count = 0;
            for(Map.Entry<K, Long> entry : expireMap.entrySet()){

                if(count >= LIMIT){
                    return;
                }

                expireKey(entry.getKey(), entry.getValue());
                count++;
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) return;
        for(K key : keyList){
            Long expireAt = expireMap.get(key);
            expireKey(key, expireAt);
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    public void expireKey(final K key, final Long expireAt){
        if(expireAt == null) {
            return;
        }

        if(expireAt <= System.currentTimeMillis()){
            // 过期数据删除
            expireMap.remove(key);
            V removeValue = cache.remove(key);

            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(key).value(removeValue).type(CacheRemoveType.EXPIRE.code());
            for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }
    }
}

package com.wang.cache.core.support.proxy;

import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.wang.cache.api.ICache;
import com.wang.cache.core.support.proxy.cglib.CglibProxy;
import com.wang.cache.core.support.proxy.dynamic.DynamicProxy;
import com.wang.cache.core.support.proxy.none.NoneProxy;

import java.lang.reflect.Proxy;

public class CacheProxy {

    private CacheProxy(){}

    public static <K, V> ICache<K, V> getProxy(final ICache<K, V> cache){
        if(ObjectUtil.isNull(cache)){
            return (ICache<K, V>) new NoneProxy(cache).proxy();
        }

        final Class clazz = cache.getClass();

        if(clazz.isInterface() || Proxy.isProxyClass(clazz)){
            return (ICache<K, V>) new DynamicProxy(cache).proxy();
        }

        return (ICache<K, V>) new CglibProxy(cache).proxy();
    }
}

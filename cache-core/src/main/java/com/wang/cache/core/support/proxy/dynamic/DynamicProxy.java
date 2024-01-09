package com.wang.cache.core.support.proxy.dynamic;

import com.wang.cache.api.ICache;
import com.wang.cache.core.support.proxy.ICacheProxy;
import com.wang.cache.core.support.proxy.bs.CacheProxyBs;
import com.wang.cache.core.support.proxy.bs.CacheProxyBsContext;
import com.wang.cache.core.support.proxy.bs.ICacheProxyBsContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxy implements InvocationHandler, ICacheProxy {

    private ICache target;
    public DynamicProxy(ICache target){
        this.target = target;
    }

    @Override
    public Object proxy() {
        InvocationHandler handler = new DynamicProxy(target);
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ICacheProxyBsContext context = CacheProxyBsContext.newInstance().method(method).params(args).target(target);
        return CacheProxyBs.newInstance().context(context).execute();
    }
}

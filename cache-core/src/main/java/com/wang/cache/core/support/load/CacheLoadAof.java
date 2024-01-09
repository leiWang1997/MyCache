package com.wang.cache.core.support.load;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.response.exception.CommonRuntimeException;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.wang.cache.annotation.CacheInterceptor;
import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheLoad;
import com.wang.cache.core.core.Cache;
import com.wang.cache.core.support.persist.PersistAofEntry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheLoadAof<K, V> implements ICacheLoad<K, V> {

    private static final Log log = LogFactory.getLog(CacheLoadAof.class);

    /**
     * 方法缓存
     *
     * 暂时比较简单，直接通过方法判断即可，不必引入参数类型增加复杂度。
     * @since 0.0.10
     */
    private static final Map<String, Method> METHOD_MAP = new HashMap<>();

    static {
        Method[] methods = Cache.class.getMethods();

        for (Method method : methods) {
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if(cacheInterceptor != null){
                if(cacheInterceptor.aof()){
                    METHOD_MAP.put(method.getName(), method);
                }
            }
        }
    }

    private final String dbPath;

    public CacheLoadAof(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(lines.isEmpty()){
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }
        for (String line : lines) {
            if(line.isEmpty()){
                continue;
            }
            PersistAofEntry entry = JSON.parseObject(line, PersistAofEntry.class);
            final String methodName = entry.getMethodName();
            final Object[] params = entry.getParams();

            final Method method = METHOD_MAP.get(methodName);

            try{
                method.invoke(cache, method, params);
            }catch (InvocationTargetException | IllegalAccessException var4){
                throw new CommonRuntimeException(var4);
            }
        }
        log.info("[load] 处理完成 path: {}", dbPath);
    }
}

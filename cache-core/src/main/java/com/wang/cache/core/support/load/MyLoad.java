package com.wang.cache.core.support.load;

import com.wang.cache.api.ICache;
import com.wang.cache.api.ICacheLoad;

public class MyLoad implements ICacheLoad {

    @Override
    public void load(ICache cache) {
        cache.put("1", "1");
        cache.put("2", "2");
    }
}

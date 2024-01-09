package com.wang.cache.core.support.persist;

import com.github.houbb.heaven.util.lang.ObjectUtil;

import java.util.Arrays;

public class PersistAofEntry {

    private Object[] params;

    private String methodName;

    /**
     * 新建对象实例
     * @return this
     * @since 0.0.10
     */
    public static PersistAofEntry newInstance() {
        return new PersistAofEntry();
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "PersistAofEntry{" +
                "params=" + Arrays.toString(params) +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}

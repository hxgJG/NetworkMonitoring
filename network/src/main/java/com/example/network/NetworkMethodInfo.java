package com.example.network;

import java.lang.reflect.Method;

/**
 * 封装网络监听注解的方法
 */
class NetworkMethodInfo {
    // 参数类型
    private Class<?> type;
    // 网络类型
    private NetworkType networkType;

    // 需要执行的方法
    private Method method;

    NetworkMethodInfo(Class<?> type, NetworkType networkType, Method method) {
        this.type = type;
        this.networkType = networkType;
        this.method = method;
    }

    Class<?> getType() {
        return type;
    }

    NetworkType getNetworkType() {
        return networkType;
    }

    Method getMethod() {
        return method;
    }
}

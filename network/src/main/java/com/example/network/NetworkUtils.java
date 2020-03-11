package com.example.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkUtils {

    public static NetworkType currentNetworkType;

    /**
     * 获取网络类型
     * @return NetworkType
     */
    @SuppressLint("MissingPermission")
    public static NetworkType getNetworkType() {
        ConnectivityManager manager = (ConnectivityManager)NetworkManager.getDefault().getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return NetworkType.NONE;

        NetworkInfo[] infos = manager.getAllNetworkInfo();
        boolean isAvailable = false;
        if (infos != null) {
            for (NetworkInfo info : infos) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    isAvailable = true;
                    break;
                }
            }
        }

        // 没有可用网络直接返回
        if (!isAvailable) return NetworkType.NONE;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) return NetworkType.NONE;
        int type = networkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return NetworkType.GPRS;
            case ConnectivityManager.TYPE_WIFI:
                return NetworkType.WIFI;
            default:
                return NetworkType.VALID;
        }
    }

    // 通过反射查找所有注册的方法的info
    static List<NetworkMethodInfo> findMethodList(Object obj) {
        List<NetworkMethodInfo> list = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            Network network = method.getAnnotation(Network.class);
            if (network == null) continue;
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "  allows only one parameter of type NetworkType");
            }
            NetworkMethodInfo info = new NetworkMethodInfo(parameterTypes[0], network.networkType(), method);
            list.add(info);
        }

        return list;
    }

    // 网络的匹配
    static void networkChanged(Map<Object, List<NetworkMethodInfo>> networkList, NetworkType currentNetworkType) {
        if (networkList == null || networkList.isEmpty()) return;
        Set<Object> keys = networkList.keySet();
        // 获取添加的key（例如：activity/fragment）对象
        for (Object obj: keys) {
            // 获取activity/fragment中所有的注册方法信息
            List<NetworkMethodInfo> infos = networkList.get(obj);
            if (infos != null) {
                for (NetworkMethodInfo info: infos) {
                    if (info.getType().isAssignableFrom(currentNetworkType.getClass())) {
                        NetworkType requestType = info.getNetworkType();
                        // 如果当前网络是要求的网络类型时
                        if (requestType == currentNetworkType) {
                            invokeNetworkMethod(info, obj, currentNetworkType);
                        } else if (requestType == NetworkType.VALID && (currentNetworkType == NetworkType.GPRS || currentNetworkType == NetworkType.WIFI)) {
                            // 如果要求的网络类型是VALID，说明只要有可用网络就满足条件，所以requestType != currentNetworkType时还要做此判断。
                            invokeNetworkMethod(info, obj, currentNetworkType);
                        }
                    }
                }
            }
        }
    }

    private static void invokeNetworkMethod(NetworkMethodInfo info, Object obj, NetworkType networkType) {
        try {
            info.getMethod().invoke(obj, networkType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

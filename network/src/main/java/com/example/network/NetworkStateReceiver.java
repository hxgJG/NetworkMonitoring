package com.example.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkStateReceiver extends BroadcastReceiver {
    private NetworkType networkType;
    // key: activity/fragment   value: key中所有订阅网络监听的方法的集合
    private Map<Object, List<NetworkMethodInfo>> networkList;

    public NetworkStateReceiver() {
        networkType = NetworkType.NONE;
        networkList = new HashMap<>(10);
    }

    public void register(Object obj) {
        List<NetworkMethodInfo> methodInfos = networkList.get(obj);
        // 没有保存过才添加到Map集合
        if (methodInfos == null) {
            methodInfos = NetworkUtils.findMethodList(obj);
            networkList.put(obj, methodInfos);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e("hxg", "intent is null or intent.getAction() is null");
            return;
        }

        if (NetworkManager.ANDROID_NETWORK_CHANGE_ACTION.equalsIgnoreCase(intent.getAction())) {
            NetworkType oldType = networkType;
            networkType = NetworkUtils.getNetworkType();
            if (oldType != networkType) {
                NetworkUtils.networkChanged(networkList, networkType);
            }
        }
    }

    public void unregister(Object o) {
        networkList.remove(o);
    }
}

package com.example.network;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
    private NetworkType networkType;
    // key: activity/fragment   value: key中所有订阅网络监听的方法的集合
    private Map<Object, List<NetworkMethodInfo>> networkList;

    NetworkCallbackImpl() {
        networkType = NetworkType.NONE;
        networkList = new HashMap<>(10);
    }

    void register(Object obj) {
        List<NetworkMethodInfo> methodInfos = networkList.get(obj);
        // 没有保存过才添加到Map集合
        if (methodInfos == null) {
            methodInfos = NetworkUtils.findMethodList(obj);
            networkList.put(obj, methodInfos);
        }
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.e("hxg", "network is available");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.e("hxg", "network is lost");
        networkChanged();
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        networkChanged();
    }

    private void networkChanged() {
        NetworkType oldType = networkType;
        networkType = NetworkUtils.getNetworkType();
        if (oldType != networkType) {
            NetworkUtils.networkChanged(networkList, networkType);
        }
    }

    void unregister(Object o) {
        networkList.remove(o);
    }
}

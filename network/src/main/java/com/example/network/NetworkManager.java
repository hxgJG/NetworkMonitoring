package com.example.network;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

/**
 * 网络管理类
 */
public class NetworkManager {
    // 系统网络改变广播
    static final String ANDROID_NETWORK_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static volatile NetworkManager instance;
    private Application application;
    private NetworkStateReceiver receiver;
    private NetworkCallbackImpl networkCallback;

    private NetworkManager() {
        if (Build.VERSION.SDK_INT < 21) {
            receiver = new NetworkStateReceiver();
        } else {
            networkCallback = new NetworkCallbackImpl();
        }
    }

    public static NetworkManager getDefault() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    public void register(Object o) {
        if (Build.VERSION.SDK_INT < 21) {
            receiver.register(o);
        } else {
            networkCallback.register(o);
        }
    }

    Application getApplication() {
        if (application == null) throw new IllegalStateException("application == null");
        return application;
    }

    @SuppressLint("MissingPermission")
    public void init(Application application) {
        this.application = application;
        if (Build.VERSION.SDK_INT < 21) {
            // 动态注册网络状态监听
            IntentFilter filter = new IntentFilter();
            filter.addAction(ANDROID_NETWORK_CHANGE_ACTION);
            application.registerReceiver(receiver, filter);
        } else {
            NetworkRequest request = new NetworkRequest.Builder().build();
            ConnectivityManager manager = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null && networkCallback != null) {
                manager.registerNetworkCallback(request, networkCallback);
            }
        }
    }

    public void unregister(Object o) {
        if (Build.VERSION.SDK_INT < 21) {
            receiver.unregister(o);
        } else {
            networkCallback.unregister(o);
        }
    }
}

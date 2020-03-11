package com.example.networkmonitoring;

import android.app.Application;

import com.example.network.NetworkManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.getDefault().init(this);
    }
}

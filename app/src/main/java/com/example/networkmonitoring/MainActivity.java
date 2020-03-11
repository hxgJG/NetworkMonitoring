package com.example.networkmonitoring;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.network.Network;
import com.example.network.NetworkManager;
import com.example.network.NetworkType;
import com.example.network.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private TextView networkState;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            refreshUI(msg.what, (String) msg.obj);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkState = findViewById(R.id.network_state);
        NetworkManager.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkType type = NetworkUtils.getNetworkType();
        refreshUI(100, type.name());
    }

    private void refreshUI(int msgCode, String msg) {
        switch (msgCode) {
            case 100:
                if (networkState != null) {
                    networkState.setText(msg);
                }
                break;
        }
    }

    @Network(networkType = NetworkType.GPRS)
    public void network(NetworkType networkType) {
        String typeName = networkType.name();
        Log.i("hxg", "1 NetworkType:" + typeName);
        Message message = new Message();
        message.what = 100;
        message.obj = typeName;
        handler.sendMessage(message);
    }

    @Network(networkType = NetworkType.WIFI)
    public void network2(NetworkType networkType) {
        String typeName = networkType.name();
        Log.i("hxg", "2 NetworkType:" + typeName);
        Message message = new Message();
        message.what = 100;
        message.obj = typeName;
        handler.sendMessage(message);
    }

    @Network(networkType = NetworkType.NONE)
    public void network3(NetworkType networkType) {
        String typeName = networkType.name();
        Log.i("hxg", "3 NetworkType:" + typeName);
    }

    @Network(networkType = NetworkType.VALID)
    public void network4(NetworkType networkType) {
        String typeName = networkType.name();
        Log.i("hxg", "4 NetworkType:" + typeName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getDefault().unregister(this);
    }
}

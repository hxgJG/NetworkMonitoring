package com.example.network;

/**
 * 网络类型
 */
public enum NetworkType {
    // 有网络，不知类型的网络
    VALID,
    // Wi-Fi网络
    WIFI,
    // 手机/PC网络： CM-WAP, CM-NET
    GPRS,
    // 没有网络
    NONE
}

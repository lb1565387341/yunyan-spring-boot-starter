package com.liubei.yunyan.service.auth;


import com.liubei.yunyan.config.YunYanProperties;

public interface YunYanAuthManager {
    String getAccessToken();
    void refreshToken();
    YunYanProperties getProperties();
}

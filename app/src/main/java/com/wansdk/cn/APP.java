package com.wansdk.cn;

import android.app.Application;

import com.wanappsdk.WanSdkManager;

public class APP extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        WanSdkManager.getInstance(this).init("user1");
    }
}

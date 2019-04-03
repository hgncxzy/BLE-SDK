package com.example.xzy.app;

import android.app.Application;

/**
 * Description : application 类
 * Created by xzy 2018/12/4 16:37 .
 */
public class BleApplication extends Application {
    private static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static Application getInstance() {
        return mApplication;
    }
}

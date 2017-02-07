package com.myself.multiplelogin;

import android.app.Application;

import com.tencent.stat.StatConfig;


/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Email      : jusenr@163.com
 * Company    : 葡萄科技
 * Author     : Jusenr
 * Date       : 2017/2/7 14:29.
 */

public class MyApp extends Application {
    public static final String APP_ID = "1105900061";
    public static final String APP_KEY = "RT5Njrhapk2E0CKE";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        StatConfig.setAppKey(this, "Aqc" + APP_ID);
    }
}

package com.wangxingxing.wxxplayer;

import android.app.Application;

import com.wangxingxing.wxxplayer.utils.DisplayUtils;

/**
 * author : 王星星
 * date : 2020/11/13 15:45
 * email : 1099420259@qq.com
 * description :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DisplayUtils.init(this);
    }
}

package com.wangxingxing.skinsupport.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author : 王星星
 * date : 2020/11/16 7:53
 * email : 1099420259@qq.com
 * description : Skin的SharedPreferences的辅助类,用来保存已经做过换肤的配置
 */
public class SkinPreference {

    public static final String SKIN_SHARED = "SkinPreference";

    // 配置了换肤包的路径
    public static final String KEY_SKIN_PATH = "skin-path";

    private static SkinPreference instance;

    private final SharedPreferences mSharedPreferences;

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinPreference.class) {
                if (instance == null) {
                    instance = new SkinPreference(context.getApplicationContext());
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return instance;
    }

    private SkinPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(SKIN_SHARED, Context.MODE_PRIVATE);
    }

    public void setSkin(String skinPath) {
        mSharedPreferences.edit().putString(KEY_SKIN_PATH, skinPath).apply();
    }

    public String getSkin() {
        return mSharedPreferences.getString(KEY_SKIN_PATH, null);
    }
}

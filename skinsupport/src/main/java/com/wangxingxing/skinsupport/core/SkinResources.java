package com.wangxingxing.skinsupport.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

/**
 * author : 王星星
 * date : 2020/11/13 17:52
 * email : 1099420259@qq.com
 * description : 换肤资源管理类，处理换肤操作业务
 */
public class SkinResources {

    private static final String TAG = "SkinResources";

    private static SkinResources instance;

    private Resources mSkinResources;
    private String mSkinPkgName;
    private boolean isDefaultSkin = true;

    private Resources mAppResources;

    public static SkinResources getInstance() {
        return instance;
    }

    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context);
                }
            }
        }
    }


    public int getColor(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    private int getIdentifier(int resId) {
        if (isDefaultSkin) {
            return resId;
        }
        // 在皮肤包中不一定就是 当前程序的 id
        // 获取对应id 在当前的名称 colorPrimary
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        int skinId = mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
        return skinId;
    }

    public Typeface getTypeface(int resId) {
        try {
            String skinTypefacePath = getString(resId);
            if (TextUtils.isEmpty(skinTypefacePath)) {
                return Typeface.DEFAULT;
            }

            if (isDefaultSkin) {
                return Typeface.createFromAsset(mAppResources.getAssets(), skinTypefacePath);
            }
            return Typeface.createFromAsset(mSkinResources.getAssets(), skinTypefacePath);
        } catch (RuntimeException e) {
            Log.e(TAG, "getTypeface: " + e.toString());
        }
        return Typeface.DEFAULT;
    }

    private String getString(int resId) {
        try {
            if (isDefaultSkin) {
                return mAppResources.getString(resId);
            }
            int skinId = getIdentifier(resId);
            if (skinId == 0) {
                return mAppResources.getString(resId);
            }
            return mSkinResources.getString(skinId);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "getString: " + e.toString());
        }

        return null;
    }
}
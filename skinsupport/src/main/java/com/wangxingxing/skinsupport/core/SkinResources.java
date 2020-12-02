package com.wangxingxing.skinsupport.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

/**
 * author : 王星星
 * date : 2020/11/13 17:52
 * email : 1099420259@qq.com
 * description : 换肤资源管理类，用于从app皮肤包中加载资源
 */
public class SkinResources {

    private static final String TAG = "SkinResources";

    private static SkinResources instance;

	// 加载了皮肤包后创建的Resources
    private Resources mSkinResources;
	// 皮肤包名
    private String mSkinPkgName;
    private boolean isDefaultSkin = true;

 	// App自己的Resources
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
		 	// 当皮肤包中不存在的时候
            // 依然使用原APP的对应值
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }


    // 加载了皮肤包后获取资源ID的方法
    public int getIdentifier(int resId) {
        if (isDefaultSkin) {
            // 默认的皮肤，直接用原APP的
            return resId;
        }
        // 在皮肤包中不一定就是 当前程序的 id
        // 获取对应id 在当前的名称
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        // 重新获取指定应用包下的指定资源ID
        int skinResId = mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
        return skinResId;
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

    public String getString(int resId) {
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

    public Drawable getDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }

        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 获取背景
     *
     * @param resId
     * @return 可能是Color 也可能是drawable
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if (resourceTypeName.equals("color")) {
            return getColor(resId);
        } else {
            // drawable
            return getDrawable(resId);
        }
    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    /**
     * 提交新的属性值
     * 最后通过观察者模式，通知使用这些新的属性
     *
     * @param resources
     * @param packageName
     */
    public void applySkin(Resources resources, String packageName) {
        mSkinResources = resources;
        mSkinPkgName = packageName;
        // 是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(packageName) || resources == null;
    }
}

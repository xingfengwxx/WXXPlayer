package com.wangxingxing.skinsupport;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.wangxingxing.skinsupport.core.SkinActivityLifecycle;
import com.wangxingxing.skinsupport.core.SkinPreference;
import com.wangxingxing.skinsupport.core.SkinResources;

import java.util.Observable;

/**
 * author : 王星星
 * date : 2020/11/13 17:24
 * email : 1099420259@qq.com
 * description : 换肤操作的主要入口类
 */
public class SkinManager extends Observable {

    private static final String TAG = "SkinManager";

    private static SkinManager instance;

    /**
     * Activity生命周期回调
     */
    private SkinActivityLifecycle mSkinActivityLifecycle;

    private Application mContext;

    public static SkinManager getInstance() {
        return instance;
    }

    private SkinManager(Application application) {
        mContext = application;

        // 共享首选项 用于记录当前使用的皮肤
        SkinPreference.init(application);

        // 资源管理类 用于从 app/皮肤包 中加载资源
        SkinResources.init(application);

        mSkinActivityLifecycle = new SkinActivityLifecycle();
        application.registerActivityLifecycleCallbacks(mSkinActivityLifecycle);

        // 加载皮肤
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    /**
     * 加载皮肤并应用
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    private void loadSkin(String skinPath) {
        Log.i(TAG, "loadSkin: 加载皮肤路径：" + skinPath);
        if (TextUtils.isEmpty(skinPath)) {
            // 记录使用默认皮肤
            SkinPreference.getInstance().setSkin("");
            // 清空资源管理器中皮肤资源属性
            SkinResources.getInstance().reset();
        }
    }
}

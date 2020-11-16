package com.wangxingxing.skinsupport;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.wangxingxing.skinsupport.core.SkinActivityLifecycle;
import com.wangxingxing.skinsupport.core.SkinPreference;
import com.wangxingxing.skinsupport.core.SkinResources;

import java.lang.reflect.Method;
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

        /**
         * 提供了一个应用生命周期回调的注册方法，用来对应用的生命周期进行集中管理。
         * 这个接口叫registerActivityLifecycleCallbacks，可以通过它注册
         * 自己的ActivityLifeCycleCallback，每一个Activity的生命周期都会回调到这里的对应方法。
         */
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
    public void loadSkin(String skinPath) {
        Log.i(TAG, "loadSkin: 加载皮肤路径：" + skinPath);
        if (TextUtils.isEmpty(skinPath)) {
            // 记录使用默认皮肤
            SkinPreference.getInstance().setSkin("");
            // 清空资源管理器中皮肤资源属性
            SkinResources.getInstance().reset();
        } else {
            // 反射创建AssetManager 与 Resource
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                // addAssetPath这个方法是hide的
                // 需要通过反射来调用：设置资源路径目录或压缩包
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinPath);
                Resources appResource = mContext.getResources();
                // 根据当前的显示与配置(横竖屏、语言等)创建Resources
                Resources skinResource = new Resources(assetManager, appResource.getDisplayMetrics(), appResource.getConfiguration());
                // 把对应的皮肤资源路径记录起来，供下次启动时用
                SkinPreference.getInstance().setSkin(skinPath);
                // 获取外部Apk(皮肤包) 包名
                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);;
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(skinResource, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 通知采集的View更新皮肤
        // 被观察者改变通知所有观察者
        setChanged();
        notifyObservers(null);
    }

    public void updateSkin(Activity activity) {
        Log.i(TAG, "updateSkin: ");
        mSkinActivityLifecycle.updateSkin(activity);
    }
}

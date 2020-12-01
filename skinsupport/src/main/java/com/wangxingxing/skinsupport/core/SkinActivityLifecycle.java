package com.wangxingxing.skinsupport.core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;

import androidx.core.view.LayoutInflaterCompat;

import com.wangxingxing.skinsupport.SkinManager;
import com.wangxingxing.skinsupport.util.SkinUtils;

import java.lang.reflect.Field;

/**
 * author : 王星星
 * date : 2020/11/15 9:40
 * email : 1099420259@qq.com
 * description :
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    // 对activity设置了自定义的布局加载工厂后用来保存这个factory对象
    // 后面可以通过activity找到
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mSkinLayoutInflaterFactoryMap = new ArrayMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        // 在这里做更新布局视图
        // 获得Activity的布局加载器
        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 更新状态栏
        SkinUtils.updateStatusBarColor(activity);

        // 更新字体
        Typeface typeface = SkinUtils.getSkinTypeface(activity);

        // 使用factory2 设置布局加载工程
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity, typeface);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        // 添加到缓存中
        mSkinLayoutInflaterFactoryMap.put(activity, skinLayoutInflaterFactory);
        // 添加观察者
        SkinManager.getInstance().addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // 从集合中删除并取消观察
        SkinLayoutInflaterFactory observer = mSkinLayoutInflaterFactoryMap.get(activity);
        mSkinLayoutInflaterFactoryMap.remove(observer);
        SkinManager.getInstance().deleteObserver(observer);
    }

    public void updateSkin(Activity activity) {
        SkinLayoutInflaterFactory factory = mSkinLayoutInflaterFactoryMap.get(activity);
        if (factory != null) {
            factory.update(null, null);
        }
    }
}

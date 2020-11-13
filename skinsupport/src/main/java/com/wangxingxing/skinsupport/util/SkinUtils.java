package com.wangxingxing.skinsupport.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import com.wangxingxing.skinsupport.R;
import com.wangxingxing.skinsupport.core.SkinResources;

/**
 * author : 王星星
 * date : 2020/11/13 17:27
 * email : 1099420259@qq.com
 * description : 换肤工具类
 */
public class SkinUtils {

    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            androidx.appcompat.R.attr.colorPrimaryDark
    };

    private static int[] STATUS_BAR_COLOR_ATTRS = {
            android.R.attr.statusBarColor,
            android.R.attr.navigationBarColor
    };

    private static int[] TYPEFACE_ATTRS = {
            R.attr.skinTypeface
    };

    /**
     * obtainStyledAttributes这里有点复杂
     * 要去理解下这个attribute相关的东西
     *
     * @param context
     * @param attrs
     * @return
     */
    public static int[] getResId(Context context, int[] attrs) {
        int[] resIds = new int[attrs.length];
        TypedArray a = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < attrs.length; i++) {
            resIds[i] = a.getResourceId(i, 0);
        }
        a.recycle();
        return resIds;
    }

    public static Typeface getSkinTypeface(Activity activity) {
        int skinTypefaceId = getResId(activity, TYPEFACE_ATTRS)[0];
        return SkinResources.getInstance().getTypeface(skinTypefaceId);
    }

    public static void updateStatusBarColor(Activity activity) {
        // 5.0以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        // 获得 statusBarColor 与 nanavigationBarColor (状态栏颜色)
        // 当与 colorPrimaryDark  不同时 以statusBarColor为准
        int[] statusBarColorResId = getResId(activity, STATUS_BAR_COLOR_ATTRS);
        // 如果直接在style中写入固定颜色值(而不是 @color/XXX ) 获得0
        if (statusBarColorResId[0] != 0) {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusBarColorResId[0]));
        } else {
            // 获得 colorPrimaryDark
            int colorPrimaryDarkResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (colorPrimaryDarkResId != 0) {
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(colorPrimaryDarkResId));
            }
        }
        if (statusBarColorResId[1] != 0) {
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(statusBarColorResId[1]));
        }
    }

}

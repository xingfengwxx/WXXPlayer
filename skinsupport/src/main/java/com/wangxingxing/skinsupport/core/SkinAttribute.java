package com.wangxingxing.skinsupport.core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.wangxingxing.skinsupport.util.SkinUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : 王星星
 * date : 2020/11/15 20:09
 * email : 1099420259@qq.com
 * description : 用来采集需要换肤的View和他的属性及属性值,把View和它的[属性及属性值]的集合进行封装保存
 */
public class SkinAttribute {

    private static final String TAG = "SkinAttribute";

    // 需要换肤的属性集合
    private static final List<String> mAttributes = new ArrayList<>();

    static {
        mAttributes.add("background");
        mAttributes.add("src");

        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");
    }

    private Typeface typeface;

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    // 记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    /**
     * 核心方法
     * 用于采集属性及属性值并封装保存
     *
     * @param view
     * @param attrs AttributeSet是xml节点的属性集合
     */
    public void load(View view, AttributeSet attrs) {
        List<SkinPair> skinPairList = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            // 获得属性名，判断是否属于要采集的 mAttributes.contains?
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                // 如果是color的以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                if (attributeValue.startsWith("?")) {
                    // 使用系统的属性值
                    Log.i(TAG, "attributeValue-->" + attributeValue);
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    // 正常以@开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                SkinPair skinPair = new SkinPair(attributeName, resId);
                skinPairList.add(skinPair);
            }
        }

        if (!skinPairList.isEmpty()) {
            SkinView skinView = new SkinView(view, skinPairList);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        } else if (view instanceof TextView || view instanceof SkinViewSupport) {
            // 没有属性满足 但是需要修改字体
            SkinView skinView = new SkinView(view, skinPairList);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }
    }

    public void applySkin() {
        for (SkinView skinView : mSkinViews) {
            skinView.applySkin(typeface);
        }
    }

    /**
     * 采集对象的最终封装：
     * 把View和它的[属性及属性值]的集合进行封装保存
     */
    static class SkinView {
        View view;
        // 属性及属性值集合
        List<SkinPair> skinPairs = new ArrayList<>();

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        /**
         * 核心方法：
         * 遍历这个View的[属性及属性值]集合，给控件设值
         */
        public void applySkin(Typeface typeface) {
            // 实现全部字体替换，就在这里
            //applyTypeFace(typeface);
            applySkinSupport();
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom ":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
                        // 单独实现某一种的TextView的字体替换
                        applyTypeFace(SkinResources.getInstance().getTypeface(skinPair.resId));
                        break;
                    default:
                        break;
                }
                if (left != null || right != null || top != null || bottom != null) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        // 调用自定义控件的applySkin
        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }

        // 设置字体
        private void applyTypeFace(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    /**
     * 对属性名和对应值的包装
     */
    static class SkinPair {
        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}

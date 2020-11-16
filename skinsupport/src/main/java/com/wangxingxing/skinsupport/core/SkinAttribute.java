package com.wangxingxing.skinsupport.core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.TextureView;
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
 * description :
 */
public class SkinAttribute {

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

    public void load(View view, AttributeSet attrs) {
        List<SkinPair> skinPairList = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            // 获得属性名mAttributes
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                // 如果是color的以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                if (attributeValue.startsWith("?")) {
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
     * 对View对象和它的所有属性名和对应值的集合的包装
     */
    static class SkinView {
        View view;
        List<SkinPair> skinPairs = new ArrayList<>();

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        /**
         * 遍历属性名和对应值的集合，设置属性
         */
        public void applySkin(Typeface typeface) {
            applyTypeface(typeface);
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
                            ((ImageView) view).setImageDrawable(((Drawable) background));
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
                        applyTypeface(SkinResources.getInstance().getTypeface(skinPair.resId));
                        break;
                    default:
                        break;
                }
                if (left != null || right != null || top != null || bottom != null) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySKin();
            }
        }

        private void applyTypeface(Typeface typeface) {
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

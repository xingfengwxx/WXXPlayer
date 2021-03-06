package com.wangxingxing.wxxplayer.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.wangxingxing.wxxplayer.R;
import com.wangxingxing.wxxplayer.base.BaseRecyclerAdapter;
import com.wangxingxing.wxxplayer.utils.DisplayUtils;

public class HomeSheetAdapter extends BaseRecyclerAdapter<Integer, HomeSheetAdapter.ViewHolder> {

    private static int itemWith;

    public HomeSheetAdapter() {
        super(R.layout.item_home_sheet);
        itemWith = (DisplayUtils.getScreenWidth() - DisplayUtils.dp2px(48)) / 3;
    }

    @Override
    protected void convert(ViewHolder helper, Integer item) {
        helper.ivImg.setImageResource(item);
    }

    static class ViewHolder extends BaseViewHolder {

        ImageView ivImg;
        FrameLayout vgItem;

        public ViewHolder(View view) {
            super(view);
            ivImg = view.findViewById(R.id.iv_img);
            vgItem = view.findViewById(R.id.vg_item);
            ViewGroup.LayoutParams layoutParams = vgItem.getLayoutParams();
            layoutParams.width = itemWith;
            layoutParams.height = itemWith;
            vgItem.setLayoutParams(layoutParams);
        }
    }

}
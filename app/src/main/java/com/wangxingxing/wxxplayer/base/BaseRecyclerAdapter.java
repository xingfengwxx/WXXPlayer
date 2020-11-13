package com.wangxingxing.wxxplayer.base;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public abstract class BaseRecyclerAdapter<T, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    public BaseRecyclerAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseRecyclerAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseRecyclerAdapter(int layoutResId) {
        super(layoutResId);
    }

    /*@Override
    public void addData(int position, @NonNull Collection<? extends T> newData) {
        mData.addAll(position, newData);
        notifyDataSetChanged();
    }

    @Override
    public void addData(int position, @NonNull T data) {
        mData.add(position, data);
        notifyDataSetChanged();
    }

    @Override
    public void addData(@NonNull Collection<? extends T> newData) {
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public void addData(@NonNull T data) {
        mData.add(data);
        notifyDataSetChanged();
    }*/

}
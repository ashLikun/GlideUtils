package com.ashlikun.xviewpager.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/2/18　15:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：解决kotlin使用setPrimaryItem, object 为null 的错误
 */
public class XPagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @Nullable Object object) {
        super.setPrimaryItem(container, position, object);
    }

}

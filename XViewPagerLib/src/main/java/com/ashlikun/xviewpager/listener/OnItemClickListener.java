package com.ashlikun.xviewpager.listener;

import com.ashlikun.xviewpager.view.BannerViewPager;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/10 14:23
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Banner的item点击事件
 */

public interface OnItemClickListener<T> {
    public void onItemClick(BannerViewPager banner, T data, int position);
}

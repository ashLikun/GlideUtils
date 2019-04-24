package com.ashlikun.xviewpager.listener;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 15:49
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：返回给定的页面所占ViewPager 测量宽度的比例，范围（0,1]
 */

public interface PageWidthListener {
    /**
     * 返回给定的页面所占ViewPager 测量宽度的比例，范围（0,1]
     */
    public float getPageWidth(int position);
}

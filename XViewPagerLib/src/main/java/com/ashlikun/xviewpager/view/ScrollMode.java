package com.ashlikun.xviewpager.view;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/11　15:28
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager滑动模式
 */
public enum ScrollMode {
    HORIZONTAL(0), VERTICAL(1);
    int id;

    ScrollMode(int id) {
        this.id = id;
    }

    static ScrollMode getScrollMode(int id) {
        for (ScrollMode scrollMode : values()) {
            if (scrollMode.id == id) {
                return scrollMode;
            }
        }
        throw new IllegalArgumentException();
    }
}

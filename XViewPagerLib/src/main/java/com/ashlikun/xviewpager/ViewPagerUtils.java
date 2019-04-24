package com.ashlikun.xviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/3 17:38
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：ViewPager的工具
 */

public class ViewPagerUtils {


    /**
     * 设置viewpager 之间的切换速度
     */
    public static void initSwitchTime(Context context, ViewPager viewPager, int time) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(context);
            scroller.setScrollDuration(time);
            field.set(viewPager, scroller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    /**
     * 无线循环的时候根据position返回真实的position
     *
     * @param position
     * @param realCount 真实的个数
     * @return
     */

    public static int getRealPosition(int position, int realCount) {
        if (realCount == 0) {
            return 0;
        }
        int realPosition = position % realCount;
        return realPosition;
    }
}

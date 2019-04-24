package com.ashlikun.xviewpager.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 17:09
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */


public class MzTransformer implements ViewPager.PageTransformer {
    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.9f;

    @Override
    public void transformPage(View view, float position) {
        //setScaleY只支持api11以上
        if (position < -1) {
            view.setScaleY(MIN_SCALE);
        }
        //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
        else if (position <= 1) {
            // [-1,1]
            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            //每次滑动后进行微小的移动目的是为了防止在三星的某些手机上出现两边的页面为显示的情况
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            view.setScaleY(MIN_SCALE);
        }
    }
}

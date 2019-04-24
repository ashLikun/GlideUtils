package com.ashlikun.xviewpager.anim;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/11　15:30
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：垂直的ViewPager对应的Transformer
 */
public class VerticalTransformer implements ViewPager.PageTransformer {
    private float yPosition;

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }

    public float getPosition() {
        return yPosition;
    }
}

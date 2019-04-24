package com.ashlikun.xviewpager.indicator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/8/6　9:22
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：默认的Indicator，只是一个圆点切换
 */
public class DefaultIndicator extends IBannerIndicator {

    public DefaultIndicator(Context context) {
        this(context, null);
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
    }

    @Override
    public DefaultIndicator setPages(List<Object> datas, int selectIndex) {
        super.setPages(datas, selectIndex);
        return this;
    }


    /**
     * 底部指示器资源图片
     *
     * @param
     */
    @Override
    public DefaultIndicator notifyDataSetChanged(int selectIndex) {
        removeAllViews();
        pointViews.clear();
        if (datas == null) {
            return this;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(space, 0, space, 0);
        for (int count = 0; count < datas.size(); count++) {
            // 翻页指示的点
            View pointView = new ImageView(getContext());
            if (selectIndex == count) {
                pointView.setBackground(selectDraw);
            } else {
                pointView.setBackground(noSelectDraw);
            }
            pointViews.add(pointView);
            addView(pointView, params);
        }
        return this;
    }


    @Override
    public void onPointScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPointSelected(int selectIndex) {
        if (selectIndex < pointViews.size()) {
            pointViews.get(selectIndex).setBackground(selectDraw);
        }
        for (int i = 0; i < pointViews.size(); i++) {
            if (selectIndex != i) {
                pointViews.get(i).setBackground(noSelectDraw);
            }
        }
    }
}

package com.ashlikun.xviewpager.indicator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/8/6　9:22
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：带缩放改变的Indicator，根据滑动多少缩放多少，只缩放大小
 */
public class ZoomIndicator extends IBannerIndicator {
    private static final float ALPHA_MAX = 1.0f;

    private static final float SCALE_MIN = 1.0f;
    private static final int ANIM_OUT_TIME = 400;
    private static final int ANIM_IN_TIME = 300;


    private float mAlpha_min = 0.8f;
    private float mScale_max = 1.4f;
    private int lastPosition = -1;

    public ZoomIndicator(Context context) {
        this(context, null);
    }

    public ZoomIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (height * mScale_max * 1.1f));
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {

        setGravity(Gravity.CENTER);
        setClipChildren(false);
    }

    @Override
    public ZoomIndicator setPages(List<Object> datas, int selectIndex) {
        super.setPages(datas, selectIndex);
        return this;
    }


    /**
     * 底部指示器资源图片
     *
     * @param
     */
    @Override
    public ZoomIndicator notifyDataSetChanged(int selectIndex) {
        removeAllViews();
        pointViews.clear();
        if (datas == null) {
            return this;
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        lastPosition = selectIndex;
        if (pointViews.size() > 0) {
            View view = pointViews.get(selectIndex);
            if (view != null) {
                targetViewAnim(view, true);
            }
        }
        return this;
    }


    @Override
    public void onPointScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPointSelected(int selectIndex) {
        View view;
        if (selectIndex < pointViews.size()) {
            view = pointViews.get(selectIndex);
            view.setBackground(selectDraw);
            targetViewAnim(view, true);
        }
        if (pointViews.size() > 1 && lastPosition >= 0 && lastPosition < pointViews.size()) {
            view = pointViews.get(lastPosition);
            view.setBackground(noSelectDraw);
            targetViewAnim(view, false);
            lastPosition = selectIndex;
        }

    }

    /**
     * 用于小圆点的放大缩小
     *
     * @param view
     * @param isMax 是不是变大
     */
    private void targetViewAnim(final View view, final boolean isMax) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = null;
        ObjectAnimator scaleY = null;
        ObjectAnimator alpha = null;
        if (isMax) {
            scaleX = ObjectAnimator.ofFloat(view, "scaleX", SCALE_MIN, mScale_max);
            scaleY = ObjectAnimator.ofFloat(view, "scaleY", SCALE_MIN, mScale_max);
            alpha = ObjectAnimator.ofFloat(view, "alpha", mAlpha_min, ALPHA_MAX);
            animatorSet.setDuration(ANIM_OUT_TIME);
        } else {
            scaleX = ObjectAnimator.ofFloat(view, "scaleX", mScale_max, SCALE_MIN);
            scaleY = ObjectAnimator.ofFloat(view, "scaleY", mScale_max, SCALE_MIN);
            alpha = ObjectAnimator.ofFloat(view, "alpha", ALPHA_MAX, mAlpha_min);
            animatorSet.setDuration(ANIM_IN_TIME);
        }
        animatorSet.play(scaleX).with(scaleY).with(alpha);

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

    }
}

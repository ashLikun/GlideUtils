package com.ashlikun.xviewpager.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.ViewPagerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/8/6　9:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Banner指示器的View接口，其他view只需实现这个接口就可以了
 * 继承自LinearLayout
 */
public abstract class IBannerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    /**
     * 资源，必须要有大小
     */
    protected Drawable selectDraw;
    protected Drawable noSelectDraw;
    /**
     * 间距
     */
    protected int space = 3;
    protected List<Object> datas;
    protected List<View> pointViews = new ArrayList<>();

    public IBannerIndicator(@NonNull Context context) {
        this(context, null);
    }

    public IBannerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IBannerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IBannerIndicator);
        space = (int) a.getDimension(R.styleable.IBannerIndicator_ind_space, ViewPagerUtils.dip2px(context, space));
        selectDraw = a.getDrawable(R.styleable.IBannerIndicator_ind_select);
        noSelectDraw = a.getDrawable(R.styleable.IBannerIndicator_ind_no_select);
        if (selectDraw == null) {
            selectDraw = getResources().getDrawable(R.drawable.banner_circle_select);
        }
        if (noSelectDraw == null) {
            noSelectDraw = getResources().getDrawable(R.drawable.banner_circle_default);
        }
        a.recycle();
        initView(context, attrs);
    }

    protected abstract void initView(Context context, AttributeSet attrs);

    /**
     * 底部指示器资源图片
     *
     * @param
     */
    public abstract IBannerIndicator notifyDataSetChanged(int selectIndex);

    /**
     * 添加数据
     *
     * @param datas
     */
    public IBannerIndicator setPages(List<Object> datas, int selectIndex) {
        this.datas = datas;
        this.notifyDataSetChanged(selectIndex);
        return this;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public Drawable getSelectDraw() {
        return selectDraw;
    }

    public Drawable getNoSelectDraw() {
        return noSelectDraw;
    }

    public int getSpace() {
        return space;
    }

    public IBannerIndicator setSelectDraw(Drawable selectDraw, int selectIndex) {
        if (selectDraw != null) {
            this.selectDraw = selectDraw;
            this.notifyDataSetChanged(selectIndex);
        }
        return this;
    }

    public IBannerIndicator setNoSelectDraw(Drawable noSelectDraw, int selectIndex) {
        if (noSelectDraw != null) {
            this.noSelectDraw = noSelectDraw;
            this.notifyDataSetChanged(selectIndex);
        }
        return this;
    }

    @Override
    public final void onPageScrollStateChanged(int state) {

    }

    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (datas == null) {
            return;
        }
        position = ViewPagerUtils.getRealPosition(position, getItemCount());
        if (position >= getItemCount()) {
            return;
        }
        onPointScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public final void onPageSelected(int position) {
        if (datas == null) {
            return;
        }
        position = ViewPagerUtils.getRealPosition(position, getItemCount());
        if (position >= getItemCount()) {
            return;
        }
        onPointSelected(position);
    }

    public abstract void onPointSelected(int selectIndex);

    public abstract void onPointScrolled(int position, float positionOffset, int positionOffsetPixels);

}

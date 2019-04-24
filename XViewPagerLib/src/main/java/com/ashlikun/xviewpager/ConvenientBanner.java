package com.ashlikun.xviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ashlikun.xviewpager.indicator.DefaultIndicator;
import com.ashlikun.xviewpager.indicator.IBannerIndicator;
import com.ashlikun.xviewpager.indicator.TransIndicator;
import com.ashlikun.xviewpager.indicator.ZoomIndicator;
import com.ashlikun.xviewpager.listener.OnBannerPageChangeListener;
import com.ashlikun.xviewpager.listener.OnItemClickListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 17:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：封装带有指示器的banner
 */

public class ConvenientBanner extends RelativeLayout {
    /**
     * 内部viewpager的id
     */
    public static final int VIEWPAGER_ID = 10086;

    private BannerViewPager viewPager;
    private ViewPagerScroller scroller;

    private IBannerIndicator indicator;

    public ConvenientBanner(Context context) {
        this(context, null);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("ResourceType")
    private void init(Context context, AttributeSet attrs) {
        viewPager = new BannerViewPager(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        viewPager.setCanLoop(a.getBoolean(R.styleable.ConvenientBanner_banner_canLoop, true));
        viewPager.setOneDataOffLoopAndTurning(a.getBoolean(R.styleable.ConvenientBanner_banner_isOneDataOffLoopAndTurning, true));
        viewPager.setTurningTime(a.getInt(R.styleable.ConvenientBanner_banner_turningTime, (int) BannerViewPager.DEFAULT_TURNING_TIME));
        viewPager.setAutoTurning(a.getBoolean(R.styleable.ConvenientBanner_banner_isAutoTurning, true));
        viewPager.setCanTouchScroll(a.getBoolean(R.styleable.ConvenientBanner_banner_isCanTouchScroll, true));
        int style = a.getInt(R.styleable.ConvenientBanner_ind_style, 1);
        if (style == 1) {
            indicator = new DefaultIndicator(context, attrs);
        } else if (style == 2) {
            indicator = new ZoomIndicator(context, attrs);
        } else if (style == 3) {
            indicator = new TransIndicator(context, attrs);
        }

        indicator.setSpace((int) a.getDimension(R.styleable.ConvenientBanner_ind_space, ViewPagerUtils.dip2px(context, 3)));
        indicator.setSelectDraw(a.getDrawable(R.styleable.ConvenientBanner_ind_select), 0);
        indicator.setNoSelectDraw(a.getDrawable(R.styleable.ConvenientBanner_ind_no_select), 0);
        float ratio = a.getFloat(R.styleable.ConvenientBanner_banner_ratio, BannerViewPager.DEFAULT_RATIO);
        int orientation = a.getInt(R.styleable.ConvenientBanner_banner_orientation, 0);
        a.recycle();
        viewPager.setRatio(ratio);
        viewPager.setOrientation(orientation);
        viewPager.setId(VIEWPAGER_ID);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(viewPager, params);
        addIndicatorView();
        initViewPagerScroll();

    }


    private void addIndicatorView() {
        if (indicator.getLayoutParams() == null) {
            LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            int dp10 = ViewPagerUtils.dip2px(getContext(), 10);
            params2.addRule(RelativeLayout.ALIGN_BOTTOM, VIEWPAGER_ID);
            params2.setMargins(dp10, dp10, dp10, dp10);
            indicator.setLayoutParams(params2);
        }
        addView(indicator);
        viewPager.addOnPageChangeListener(indicator);
    }

    /**
     * 设置Indicator
     *
     * @param ind
     */
    public void setIndicator(IBannerIndicator ind) {
        if (indicator != null) {
            removeView(indicator);
            ind.setSpace(indicator.getSpace());
            ind.setSelectDraw(indicator.getSelectDraw(), getCurrentItem());
            ind.setNoSelectDraw(indicator.getNoSelectDraw(), getCurrentItem());
        }
        this.indicator = ind;
        addIndicatorView();
    }

    public IBannerIndicator getIndicator() {
        return indicator;
    }

    /**
     * 设置banner的数据
     *
     * @param datas
     */
    public ConvenientBanner setPages(final List datas) {
        if (datas == null) {
            return this;
        }

        viewPager.setPages(datas);
        indicator.setPages(datas, getCurrentItem());
        return this;
    }

    public ConvenientBanner setPages(final ViewPageHelperListener holderCreator, final List datas) {
        if (datas == null) {
            return this;
        }
        viewPager.setPages(holderCreator, datas);
        indicator.setPages(datas, getCurrentItem());
        return this;
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.notifyDataSetChanged();
        indicator.notifyDataSetChanged(getCurrentItem());
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        if (indicator != null) {
            indicator.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }


    /**
     * 指示器的方向
     *
     * @return
     */
    public ConvenientBanner setIndicatorGravity(int gravity) {
        indicator.setGravity(Gravity.CENTER_VERTICAL | gravity);
        return this;
    }

    /***
     * 是否开启了翻页
     *
     * @return
     */
    public boolean isTurning() {
        return viewPager.isTurning();
    }

    /***
     * 开始翻页
     *
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        viewPager.startTurning(autoTurningTime);
        return this;
    }

    public void stopTurning() {
        viewPager.stopTurning();
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }


    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    viewPager.getContext());
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanTouchScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanTouchScroll(manualPageable);
    }


    /**
     * 获取当前的页面index
     *
     * @return
     */
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealPosition();
        }
        return -1;
    }

    /**
     * 设置当前的页面index
     *
     * @param index
     */
    public void setCurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }


    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 还原原始的position翻页监听
     * {@link com.ashlikun.xviewpager.listener.OnBannerPageChangeListener}
     *
     * @param listener
     */
    public void setOnBannerChangeListener(OnBannerPageChangeListener listener) {
        viewPager.setOnBannerChangeListener(listener);
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public BannerViewPager getViewPager() {
        return viewPager;
    }

    public void setCanLoop(boolean canLoop) {
        viewPager.setCanLoop(canLoop);
    }

    public List getDatas() {
        return viewPager.getDatas();
    }

    /**
     * 设置比例
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        viewPager.setRatio(ratio);
    }

    /**
     * 设置方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        viewPager.setOrientation(orientation);
    }
}

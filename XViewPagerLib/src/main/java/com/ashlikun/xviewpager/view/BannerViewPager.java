package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ashlikun.xviewpager.R;
import com.ashlikun.xviewpager.ViewPagerUtils;
import com.ashlikun.xviewpager.listener.OnItemClickListener;
import com.ashlikun.xviewpager.listener.PageWidthListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/2 17:20
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：一个普通的没有指示器的banner,
 * 1:可以用作广告栏
 * 2:可以用作启动页
 */

public class BannerViewPager extends NestViewPager {
    //判断点击的最大移动距离
    private static final float SENS = 5;
    public static final long DEFAULT_TURNING_TIME = 5000;
    public static final float DEFAULT_RATIO = 16 / 9.0f;
    OnPageChangeListener mOuterPageChangeListener;
    private OnItemClickListener onItemClickListener;
    private CusPageAdapter mAdapter;
    //是否可以触摸滚动
    private boolean isCanTouchScroll = true;
    //是否可以循环
    private boolean canLoop = true;
    //自动轮播的间隔
    private long turningTime = DEFAULT_TURNING_TIME;

    //是否可以自动滚动,外部使用
    private boolean isAutoTurning = true;
    //是否可以自动滚动,内部标识
    private boolean turning = false;
    //是否只有一条数据的时候禁用翻页
    private boolean isOneDataOffLoopAndTurning = true;
    //是否可以自动滚动，内部用于判断触摸屏幕，与view进入焦点
    private boolean isNeibuAutoTurning = false;
    private float downX = 0, downY = 0;

    private AdSwitchTask adSwitchTask;
    private PageWidthListener pageWidthListener;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerViewPager);
        canLoop = a.getBoolean(R.styleable.BannerViewPager_banner_canLoop, canLoop);
        isOneDataOffLoopAndTurning = a.getBoolean(R.styleable.BannerViewPager_banner_isOneDataOffLoopAndTurning, isOneDataOffLoopAndTurning);
        turningTime = a.getInteger(R.styleable.BannerViewPager_banner_turningTime, (int) turningTime);
        ratio = a.getFloat(R.styleable.BannerViewPager_banner_ratio, DEFAULT_RATIO);
        orientation = a.getInt(R.styleable.BannerViewPager_banner_orientation, 0);
        isCanTouchScroll = a.getBoolean(R.styleable.BannerViewPager_banner_isCanTouchScroll, isCanTouchScroll);
        isAutoTurning = a.getBoolean(R.styleable.BannerViewPager_banner_isCanTouchScroll, isAutoTurning);
        a.recycle();
        setTurningTime(turningTime);
        addOnPageChangeListener(onPageChangeListener);
        adSwitchTask = new AdSwitchTask(this);

    }

    public void setTurningTime(long turningTime) {
        this.turningTime = turningTime;
        if (turningTime > 0) {
            turning = true;
            isNeibuAutoTurning = true;
        }
    }

    public int getFristItem() {
        if (mAdapter == null) {
            return 0;
        }
        return canLoop ? mAdapter.getRealCount() : 0;
    }

    public int getLastItem() {
        return mAdapter.getRealCount() - 1;
    }

    public boolean isCanTouchScroll() {
        return isCanTouchScroll;
    }

    public void setCanTouchScroll(boolean isCanScroll) {
        this.isCanTouchScroll = isCanScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onItemClickListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = ev.getX();
                    downY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 获取当前手指位置
                    float endY = ev.getY();
                    float endX = ev.getX();
                    float distanceX = Math.abs(endX - downX);
                    float distanceY = Math.abs(endY - downY);
                    if (distanceX > 10 && distanceX > distanceY) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        requestDisallowInterceptTouchEvent(true);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (Math.abs(downX - ev.getX()) < SENS
                            && Math.abs(downY - ev.getY()) < SENS) {
                        if (getDatas() != null && getRealPosition() < getDatas().size()) {
                            onItemClickListener.onItemClick(this, getDatas().get(getRealPosition()), getRealPosition());
                        }
                    }
                    getParent().requestDisallowInterceptTouchEvent(false);
                    requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        if (isCanTouchScroll && !isOneDataOffLoopAndTurning()) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanTouchScroll && !isOneDataOffLoopAndTurning()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return onItemClickListener != null;
        }
    }

    /**
     * 触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isCanTouchScroll && isAutoTurning && !isOneDataOffLoopAndTurning()) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                // 开始翻页
                if (isNeibuAutoTurning) {
                    startTurning(turningTime);
                }
            } else if (action == MotionEvent.ACTION_DOWN) {
                // 停止翻页
                if (isNeibuAutoTurning) {
                    stopTurning();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public CusPageAdapter getAdapter() {
        return mAdapter;
    }

    public int getRealPosition() {
        return ViewPagerUtils.getRealPosition(getCurrentItem(), mAdapter != null ? mAdapter.getRealCount() : 0);
    }

    /**
     * adapter真实的个数
     *
     * @return
     */
    public int getRealItemCount() {
        return mAdapter != null ? mAdapter.getRealCount() : 0;
    }

    /**
     * adapter的最大item个数，是假的
     *
     * @return
     */
    public int getItemCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }


    /**
     * 还原原始的position翻页监听
     * {@link com.ashlikun.xviewpager.listener.OnBannerPageChangeListener}
     *
     * @param listener
     */
    public void setOnBannerChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }


    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
            int realPosition = ViewPagerUtils.getRealPosition(position, mAdapter.getRealCount());
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int realPosition = ViewPagerUtils.getRealPosition(position, mAdapter.getRealCount());

            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrolled(realPosition,
                        positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public boolean isCanLoop() {
        return canLoop;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        if (canLoop == false) {
            setCurrentItem(getRealPosition(), false);
        }
        if (mAdapter == null) {
            return;
        }
        mAdapter.setCanLoop(canLoop);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 手动停止
     */
    public BannerViewPager stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
        return this;
    }

    /**
     * 手动开始
     */
    public BannerViewPager startTurning() {
        if (turningTime > 0) {
            return startTurning(turningTime);
        }
        return this;
    }

    /**
     * 手动开始
     */
    public BannerViewPager startTurning(long turningTime) {
        if (!isAutoTurning || turningTime <= 0) {
            return this;
        }
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        isNeibuAutoTurning = true;
        this.turningTime = turningTime;
        turning = true;
        if (getRealItemCount() > 0 && !isOneDataOffLoopAndTurning()) {
            postDelayed(adSwitchTask, turningTime);
        }
        return this;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置banner的数据
     * 第二次后调用的
     *
     * @param datas
     */
    public BannerViewPager setPages(final List datas) {
        if (mAdapter == null) {
            throw new RuntimeException("没有发现holderCreator，请调用双参数的setPages");
        }
        setPages(null, datas);
        return this;
    }

    /**
     * 设置banner的数据
     * 第一次调用的，必须要有ViewPageHelperListener
     *
     * @param datas
     */
    public BannerViewPager setPages(final ViewPageHelperListener holderCreator, final List datas) {
        if (datas == null) {
            return this;
        }
        if (mAdapter == null) {
            mAdapter = new CusPageAdapter<>(this, holderCreator, datas);
            mAdapter.setCanLoop(canLoop);
            setAdapter(mAdapter);
        } else {
            mAdapter.setCanLoop(canLoop);
            mAdapter.setDatas(datas);
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.setPageWidthListener(pageWidthListener);
        setCurrentItem(getFristItem(), false);

        if (turning) {
            startTurning();
        }
        return this;
    }

    public void setPageWidthListener(PageWidthListener pageWidthListener) {
        this.pageWidthListener = pageWidthListener;
        if (mAdapter != null) {
            mAdapter.setPageWidthListener(pageWidthListener);
        }
    }

    public List<Object> getDatas() {
        if (mAdapter == null) {
            return null;
        }
        return mAdapter.datas;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (canLoop && isNeibuAutoTurning) {
            startTurning();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (canLoop && isNeibuAutoTurning) {
            stopTurning();
        }
    }

    /**
     * 是否只有一条数据的时候禁用自动翻页
     *
     * @return
     */
    public boolean isOneDataOffLoopAndTurning() {
        return isOneDataOffLoopAndTurning && getDatas() != null && getDatas().size() <= 1;
    }

    /**
     * 设置是否只有一条数据的时候禁用翻页
     *
     * @param oneDataOffLoopAndTurning
     */
    public void setOneDataOffLoopAndTurning(boolean oneDataOffLoopAndTurning) {
        this.isOneDataOffLoopAndTurning = oneDataOffLoopAndTurning;
    }

    /**
     * 设置自动滚动
     *
     * @param isAutoTurning
     */
    public void setAutoTurning(boolean isAutoTurning) {
        this.isAutoTurning = isAutoTurning;
        startTurning();
    }

    /**
     * 自动滚动的倒计时
     */
    static class AdSwitchTask implements Runnable {

        private final WeakReference<BannerViewPager> reference;

        AdSwitchTask(BannerViewPager bannerViewPager) {
            this.reference = new WeakReference(bannerViewPager);
        }

        @Override
        public void run() {
            BannerViewPager bannerViewPager = reference.get();
            if (bannerViewPager != null && bannerViewPager.turning) {
                int page = bannerViewPager.getCurrentItem() + 1;
                if (page >= bannerViewPager.getItemCount()) {
                    page = bannerViewPager.getFristItem();
                }
                bannerViewPager.setCurrentItem(page);
                bannerViewPager.postDelayed(bannerViewPager.adSwitchTask, bannerViewPager.turningTime);
            }
        }
    }
}

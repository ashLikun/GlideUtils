package com.ashlikun.xviewpager.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.ashlikun.xviewpager.fragment.FragmentPagerAdapter;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/11/14　15:10
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：viewpager放在RecyclerView里面
 * 一般用于相当于这个view是第二页
 */
public class ViewPagerToRecyclerView extends FrameLayout {
    static final String TAG = "ViewPagerToRecyclerView";
    /**
     * DOWN事件坐标
     */
    private int mInitialTouchX;
    private int mInitialTouchY;
    /**
     * 最近一次事件坐标
     */
    private int mLastTouchX;
    private int mLastTouchY;
    /**
     * 能够识别的最小滑动
     */
    private int mTouchSlop;
    /**
     * 最小加速度
     */
    private int mMinFlingVelocity;
    /**
     * 最大加速度
     */
    private int mMaxFlingVelocity;
    /**
     * 外部recyclerview
     */
    private RecyclerView outRecyclerView;
    /**
     * 内部滚动控件
     */
    private View currentScrollView;
    /**
     * 事件由外部处理变成内部处理的临界点
     */
    private boolean isEnterFirst = true;
    /**
     * 测量速度工具类
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 处理fling事件工具类
     */
    private ViewFlinger mViewFlinger;

    /**
     * 离父view顶部的距离
     */
    private int mParentTopSize;
    /**
     * 是否展开的监听
     */
    private ToTopListener topListener;
    /**
     * 滚动的手指触摸id
     */
    private int mScrollPointerId = -1;

    public ViewPagerToRecyclerView(Context context) {
        this(context, null);
    }

    public ViewPagerToRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * 设置外围的RecyclerView
     *
     * @param outRecyclerView
     */
    public void setOutRecyclerView(RecyclerView outRecyclerView) {
        this.outRecyclerView = outRecyclerView;
    }

    /**
     * 离父view顶部的距离
     *
     * @param mParentTopSize
     */
    public void setParentTopSize(int mParentTopSize) {
        this.mParentTopSize = mParentTopSize;
    }

    /**
     * 设置是否到达顶部的监听 与{@link #setParentTopSize} 设置一个就可以
     *
     * @param topListener
     */
    public void setTopListener(ToTopListener topListener) {
        this.topListener = topListener;
    }

    public ViewPager getViewPager() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ViewPager) {
                return (ViewPager) getChildAt(i);
            }
        }
        return null;
    }

    private void initView(Context context, AttributeSet attrs) {
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mViewFlinger = new ViewFlinger();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewPager().removeOnPageChangeListener(onPageChangeListener);
        getViewPager().addOnPageChangeListener(onPageChangeListener);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int actionIndex = event.getActionIndex();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mInitialTouchX = (int) (event.getX() + 0.5f);
                mInitialTouchY = (int) (event.getY() + 0.5f);
                mScrollPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mInitialTouchX = mLastTouchX = (int) (event.getX(actionIndex) + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (event.getY(actionIndex) + 0.5f);
                mScrollPointerId = event.getPointerId(actionIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id "
                            + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                final int y = (int) (event.getY(index) + 0.5f);
                final int dy = y - mInitialTouchY;
                //垂直方向拦截事件
                if (Math.abs(dy) > mTouchSlop) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.clear();
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        checkOutRecyclerView();
        MotionEvent vtev = MotionEvent.obtain(event);
        final int actionIndex = event.getActionIndex();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = event.getPointerId(0);
                mInitialTouchX = (int) (event.getX() + 0.5f);
                mInitialTouchY = (int) (event.getY() + 0.5f);
                vtev.offsetLocation(0, 0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = event.getPointerId(actionIndex);
                mInitialTouchX = mLastTouchX = (int) (event.getX(actionIndex) + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (event.getY(actionIndex) + 0.5f);
                break;
            case MotionEvent.ACTION_MOVE:
                final int index = event.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id "
                            + mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }
                final int x = (int) (event.getX(index) + 0.5f);
                final int y = (int) (event.getY(index) + 0.5f);
                int dx = mInitialTouchX - x;
                int dy = mInitialTouchY - y;
                //判断滑动事件由外部recyclerview处理还是内部recyclerview处理
                boolean isIn = !isTop() || (isTop() && (mLastTouchY - y) < 0 && canChildScrollUp());
                if (isIn) {
                    //外部处理
                    if ((isTop() && (mLastTouchY - y) < 0 && canChildScrollUp())) {
                        mInitialTouchX = (int) (event.getX() + 0.5f);
                        mInitialTouchY = (int) (event.getY() + 0.5f);
                        dx = 0;
                        dy = -1;
                    }

                    outRecyclerView.scrollBy(dx, dy);
                    vtev.offsetLocation(dx, dy);
                } else {
                    //内部处理
                    if (isEnterFirst) {
                        if (event.getAction() != MotionEvent.ACTION_DOWN) {
                            event.setAction(MotionEvent.ACTION_DOWN);
                        }
                        isEnterFirst = false;
                    }
                    getScrollView().onTouchEvent(event);
                }
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (isTop() || !isEnterFirst) {
                    getScrollView().onTouchEvent(event);
                } else {
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                    final float yvel = -mVelocityTracker.getYVelocity(mScrollPointerId);
                    //处理fling事件
                    fling(0, (int) yvel);
                }
                isEnterFirst = true;
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                break;
        }
        if (!isEnterFirst && mVelocityTracker != null) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return true;
    }

    public boolean isTop() {
        if (topListener != null) {
            return topListener.isTop();
        }
        return getTop() == mParentTopSize;
    }

    private void checkOutRecyclerView() {
        if (outRecyclerView == null) {
            outRecyclerView = getParentRecyclerView((ViewGroup) getParent());
        }
    }

    private RecyclerView getParentRecyclerView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return null;
        } else if (viewGroup instanceof RecyclerView) {
            return (RecyclerView) viewGroup;
        } else {
            return getParentRecyclerView((ViewGroup) viewGroup.getParent());
        }
    }

    private void fling(int velocityX, int velocityY) {
        if (Math.abs(velocityX) < mMinFlingVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < mMinFlingVelocity) {
            velocityY = 0;
        }
        velocityX = Math.max(-mMaxFlingVelocity, Math.min(velocityX, mMaxFlingVelocity));
        velocityY = Math.max(-mMaxFlingVelocity, Math.min(velocityY, mMaxFlingVelocity));
        if (mViewFlinger != null) {
            mViewFlinger.stop();
        }
        mViewFlinger.fling(velocityX, velocityY);
    }

    /**
     * 内部控件是否可以下拉
     *
     * @return true 可以下拉
     */
    public boolean canChildScrollUp() {
        View view = getScrollView();
        return !view.canScrollVertically(-1);
    }

    private View getScrollView() {
        if (currentScrollView == null) {
            PagerAdapter pagerAdapter = getViewPager().getAdapter();
            if (pagerAdapter instanceof FragmentPagerAdapter) {
                Fragment fragment = ((FragmentPagerAdapter) pagerAdapter).getCurrentFragment();
                if (fragment instanceof ViewPagerItemListener) {
                    currentScrollView = ((ViewPagerItemListener) fragment).getScrollableView();
                }
            }
        }
        return currentScrollView;
    }

    static final Interpolator sQuinticInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    /**
     * 处理fling事件，仿照RecyclerView
     */
    class ViewFlinger implements Runnable {

        private int mLastFlingX;
        private int mLastFlingY;
        private OverScroller mScroller;

        public ViewFlinger() {
            mScroller = new OverScroller(getContext(), sQuinticInterpolator);
        }

        @Override
        public void run() {
            checkOutRecyclerView();
            if (outRecyclerView == null) {
                stop();
                return;
            }
            if (mScroller.computeScrollOffset()) {
                //可以滚动
                final int x = mScroller.getCurrX();
                final int y = mScroller.getCurrY();
                int dx = x - mLastFlingX;
                int dy = y - mLastFlingY;
                mLastFlingX = x;
                mLastFlingY = y;
                outRecyclerView.scrollBy(dx, dy);
                ViewCompat.postOnAnimation(outRecyclerView, this);
            }
        }

        public void fling(int velocityX, int velocityY) {
            checkOutRecyclerView();
            if (outRecyclerView == null) {
                return;
            }
            mLastFlingX = mLastFlingY = 0;
            mScroller.fling(0, 0, velocityX, velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ViewCompat.postOnAnimation(outRecyclerView, this);
        }

        public void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentScrollView = null;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * ViewPager内部的控件需要实现的接口
     */
    public interface ViewPagerItemListener {
        /**
         * 当前滚动的控件
         * 一般就是RecyclerView
         *
         * @return
         */
        View getScrollableView();
    }

    public interface ToTopListener {

        /**
         * 是否到顶部了，该ViewPager内部滚动了
         *
         * @return true:到顶部了
         */
        boolean isTop();
    }
}

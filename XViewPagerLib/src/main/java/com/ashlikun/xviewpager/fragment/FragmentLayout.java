package com.ashlikun.xviewpager.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.LruCache;
import android.util.SparseArray;
import android.widget.FrameLayout;

import com.ashlikun.xviewpager.R;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/10　17:02
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：用于fragment一次加载一个的，加载过了不用重新加载
 * 利用hind和show
 * 生命周期和ViewPager使用的一样
 * 必须依赖 {@link FragmentPagerAdapter}
 */
public class FragmentLayout extends FrameLayout {
    /**
     * 缓存时候的Fragment
     */
    protected SparseArray<Fragment> mFragments = new SparseArray();
    private FragmentPagerAdapter mAdapter;
    private int currentPosition = 0;
    private LruCache<Integer, Integer> lruCache;
    /**
     * 最大缓存个数
     */
    protected int maxCache = Integer.MAX_VALUE;

    public FragmentLayout(@NonNull Context context) {
        this(context, null);
    }

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentLayout);
        setOffscreenPageLimit(a.getInt(R.styleable.FragmentLayout_fl_maxCache, maxCache));
        a.recycle();
    }

    public int getItemCount() {
        return mFragments.size();
    }

    public FragmentPagerAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(final FragmentPagerAdapter adapter) {
        //清空已有的
        if (mAdapter != null) {
            for (int i = 0; i < getItemCount(); i++) {
                Fragment f = geFragment(i);
                if (f != null) {
                    adapter.destroyItem(FragmentLayout.this, i, f);
                }
            }
            adapter.finishUpdate(FragmentLayout.this);
            mFragments.clear();
        }
        lruCache = new LruCache<Integer, Integer>(maxCache) {
            @Override
            protected void entryRemoved(boolean evicted, Integer key, Integer oldValue, Integer newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (evicted) {
                    Fragment ff = geFragment(key);
                    if (ff != null) {
                        adapter.destroyItem(FragmentLayout.this, key, ff);
                        adapter.finishUpdate(FragmentLayout.this);
                        mFragments.remove(key);
                    }
                }
            }
        };
        this.mAdapter = adapter;
        showFragment(currentPosition);

    }

    /**
     * 显示一个fragment
     *
     * @param position
     */
    private void showFragment(int position) {
        Fragment f = geFragment(position);
        FragmentTransaction ft = mAdapter.getFragmentManager().beginTransaction();
        //隐藏其他的
        for (int i = 0; i < getItemCount(); i++) {
            Fragment cacheFragment = geFragment(mFragments.keyAt(i));
            if (cacheFragment != null) {
                ft.hide(cacheFragment);
            }
        }
        //是否已经添加了
        if (f != null) {
            mAdapter.setPrimaryItem(this, position, f);
            ft.show(f).commitNowAllowingStateLoss();
        } else {
            if (getItemCount() > 0) {
                ft.commitNowAllowingStateLoss();
            }
            f = (Fragment) mAdapter.instantiateItem(this, position);
            mFragments.put(position, f);
            mAdapter.setPrimaryItem(this, position, f);
            mAdapter.finishUpdate(this);
        }
        lruCache.put(position, position);
    }

    /**
     * 隐藏一个fragment
     *
     * @param position
     */
    private void hindFragment(int position) {
        Fragment f = geFragment(position);
        if (f != null) {
            //已经添加了
            if (f.getUserVisibleHint()) {
                f.setUserVisibleHint(false);
            }
            mAdapter.getFragmentManager().beginTransaction().hide(f).commitNowAllowingStateLoss();
        }
    }

    /**
     * 设置当前显示的fragment
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (currentPosition != position) {
            currentPosition = position;
            showFragment(currentPosition);
        }
    }

    public int getCurrentItem() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * 最大缓存个数，在setAdapter之前设置
     *
     * @param limit
     */
    public void setOffscreenPageLimit(int limit) {
        if (limit <= 0) {
            limit = 1;
        }
        maxCache = limit;
    }

    /**
     * 获取缓存的fragment
     * 前提是开启缓存
     *
     * @param position
     * @return
     */
    public Fragment geFragment(int position) {
        return mFragments.get(position);
    }
}

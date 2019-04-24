package com.ashlikun.xviewpager.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author　　: 李坤
 * 创建时间: 2018/6/15 0015 下午 1:31
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：viewpager显示fragment的适配器,用路由模式去寻找fragment
 * 可以设置缓存fragment，第一次会使用Arouter去发现Fragment，后续就会缓存起来
 * <p>
 * 如果开启缓存，可能会被检测到Fragment内存泄漏
 */

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * 传递给fragment的参数
     */
    public static final String POSITION = "fpa_POSITION";

    protected List<FragmentPagerItem> pagerItems;
    /**
     * 是否缓存Fragment
     */
    private boolean isCache = false;
    protected final FragmentManager fragmentManager;
    /**
     * 缓存时候的Fragment
     */
    protected SparseArray<Fragment> mCacheFragment = new SparseArray<>();

    protected Fragment mCurrentPrimaryItem = null;

    private FragmentPagerAdapter(Builder builder) {
        super(builder.fm);
        fragmentManager = builder.fm;
        this.pagerItems = builder.items;
        setCache(builder.isCache);
    }


    public List<FragmentPagerItem> getPagerItems() {
        if (pagerItems == null) {
            pagerItems = new ArrayList<>();
        }
        return pagerItems;
    }

    public FragmentPagerItem getPagerItem(int position) {
        return pagerItems.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pagerItems.get(position).title;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mCacheFragment.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (!isCache) {
            mCacheFragment.remove(position);
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (isCache) {
            fragment = getCacheFragment(position);
        }
        if (fragment == null) {
            FragmentPagerItem item = pagerItems.get(position);
            //添加一个告诉fragment当前是第几页
            item.addParam(POSITION, position);
            fragment = (Fragment) ARouter.getInstance()
                    .build(item.path)
                    .with(item.param)
                    .navigation();
        }
        return fragment;
    }

    /**
     * 查找这个id对应 的position
     *
     * @param id
     * @return
     */
    public int findIdPosition(String id) {
        try {
            for (int i = 0; i < getCount(); i++) {
                if (TextUtils.equals(id, getPagerItem(i).getId())) {
                    return i;
                }
            }
        } catch (Exception e) {

        }
        return -1;
    }

    @Override
    public int getCount() {
        return pagerItems == null || pagerItems.isEmpty() ? 0 : pagerItems.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            mCurrentPrimaryItem = fragment;
        }
    }

    public <T extends Fragment> T getCurrentFragment() {
        if (mCurrentPrimaryItem == null) {
            return null;
        }
        return (T) mCurrentPrimaryItem;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    /**
     * 获取缓存的fragment
     * 前提是开启缓存
     *
     * @param position
     * @return
     */
    public <T extends Fragment> T getCacheFragment(int position) {
        if (mCacheFragment == null) {
            return null;
        }
        return (T) mCacheFragment.get(position);
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        if (isCache) {
            mCacheFragment.clear();
        }
    }

    /**
     * 清空缓存
     */
    public void removeCache(int position) {
        if (isCache) {
            mCacheFragment.remove(position);
        }
    }

    public int getCacheSize() {
        return mCacheFragment != null ? mCacheFragment.size() : 0;
    }

    /**
     * 构建者
     */
    public static class Builder {
        FragmentManager fm;
        List<FragmentPagerItem> items = new ArrayList<>();
        private boolean isCache;

        private Builder(FragmentManager fm) {
            this.fm = fm;
        }

        public static Builder get(FragmentManager fm) {
            return new Builder(fm);
        }

        public Builder setItems(List<FragmentPagerItem> items) {
            this.items = items;
            return this;
        }

        public Builder setCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Builder addItem(FragmentPagerItem item) {
            items.add(item);
            return this;
        }

        public FragmentPagerItem addItem(String itemPath) {
            FragmentPagerItem item = FragmentPagerItem.get(itemPath);
            item.builder = this;
            items.add(item);
            return item;
        }

        public FragmentPagerAdapter build() {
            return new FragmentPagerAdapter(this);
        }
    }
}
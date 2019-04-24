package com.ashlikun.xviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/11　10:05
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class FragmentUtils {
    /**
     * 初始化时候先清空全部，防止内存不足时候再次创建
     */
    public static void removeAll(FragmentManager fm) {
        List<Fragment> ff = fm.getFragments();
        if (ff != null && !ff.isEmpty()) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : ff) {
                ft.remove(f);
            }
            ft.commitNowAllowingStateLoss();
        }
    }
}

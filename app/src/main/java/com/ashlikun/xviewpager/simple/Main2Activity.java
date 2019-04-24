package com.ashlikun.xviewpager.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ashlikun.xviewpager.FragmentUtils;
import com.ashlikun.xviewpager.fragment.FragmentPagerAdapter;
import com.ashlikun.xviewpager.view.NestViewPager;

/**
 * 作者　　: 李坤
 * 创建时间: 2018/6/22 0022　下午 3:06
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class Main2Activity extends AppCompatActivity {

    NestViewPager fragmentLayout = null;
    FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fragmentLayout = findViewById(R.id.fragmentLayout);
        FragmentUtils.removeAll(getSupportFragmentManager());
        adapter = FragmentPagerAdapter.Builder.get(getSupportFragmentManager())
                .addItem("/Fragment/test").setId("1").ok()
                .addItem("/Fragment/test").setId("2").ok()
                .addItem("/Fragment/test").setId("3").ok()
                .addItem("/Fragment/test").setId("4").ok()
                .addItem("/Fragment/test").setId("5").ok()
                .addItem("/Fragment/test").setId("6").ok()
                .addItem("/Fragment/test").setId("7").ok()
                .setCache(true)
                .build();
//        fragmentLayout.setOffscreenPageLimit(4);
//        fragmentLayout.setScrollMode(ScrollMode.VERTICAL);
//        fragmentLayout.setScrollMode(ScrollMode.HORIZONTAL);
        fragmentLayout.setAdapter(adapter);
    }


    public void onClick1(View view) {
        fragmentLayout.setCurrentItem(0);
    }

    public void onClick2(View view) {
        fragmentLayout.setCurrentItem(1);
    }

    public void onClick3(View view) {
        fragmentLayout.setCurrentItem(2);
    }

    public void onClick4(View view) {
        fragmentLayout.setCurrentItem(3);
    }

    public void onClick5(View view) {
        fragmentLayout.setCurrentItem(4);
    }

    public void onClick6(View view) {
        fragmentLayout.setCurrentItem(5);
    }

    public void onClick7(View view) {
        fragmentLayout.setCurrentItem(6);
    }
}

package com.ashlikun.xviewpager.simple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ashlikun.glideutils.GlideUtils;
import com.ashlikun.xviewpager.ConvenientBanner;
import com.ashlikun.xviewpager.indicator.TransIndicator;
import com.ashlikun.xviewpager.listener.OnItemClickListener;
import com.ashlikun.xviewpager.listener.ViewPageHelperListener;
import com.ashlikun.xviewpager.view.BannerViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ViewPageHelperListener<String> {
    BannerViewPager bannerViewPager;
    ConvenientBanner convenientBanner;
    private static final Object[] RESURL = {
            "http://img.mukewang.com/54bf7e1f000109c506000338-590-330.jpg",
            "http://upload.techweb.com.cn/2015/0114/1421211858103.jpg",
            "http://img1.cache.netease.com/catchpic/A/A0/A0153E1AEDA115EAE7061A0C7EBB69D2.jpg",
            "http://image.tianjimedia.com/uploadImages/2015/202/27/57RF8ZHG8A4T_5020a2a4697650b89" +
                    "c394237ba9ffbb45fe8555a2cbec-6O6nmI_fw658.jpg"};
    private static final Object[] RESURL2 = {
            "http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerViewPager = findViewById(R.id.bannerViewPager);
//        bannerViewPager.setRefreshLayout(findViewById(R.id.swipeRefresh));
        convenientBanner = findViewById(R.id.convenientBanner);
        convenientBanner.setIndicator(new TransIndicator(this));
        bannerViewPager.setPages(this, new ArrayList(Arrays.asList(RESURL)));
        convenientBanner.setPages(this, new ArrayList(Arrays.asList(RESURL2)));
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BannerViewPager banner, Object data, int position) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
//        if (bannerViewPager.getRealItemCount() == RESURL.length) {
//            bannerViewPager.setPages(new ArrayList(Arrays.asList(RESURL2)));
//            convenientBanner.setPages(new ArrayList(Arrays.asList(RESURL2)));
//        } else {
//            bannerViewPager.setPages(new ArrayList(Arrays.asList(RESURL)));
//            convenientBanner.setPages(new ArrayList(Arrays.asList(RESURL)));
//        }
    }

    @Override
    public View createView(Context context, BannerViewPager banner, String data, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        GlideUtils.show(imageView, data);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "aaaaa", Toast.LENGTH_LONG).show();
            }
        });
        return imageView;
    }

}

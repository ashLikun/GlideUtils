package com.ashlikun.xviewpager.simple;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.glideutils.GlideLoad;
import com.ashlikun.glideutils.GlideUtils;

public class MainActivity extends AppCompatActivity {
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
        GlideUtils.init(getApplicationContext(),null);
        GlideUtils.setErrorRes(R.mipmap.ic_launcher);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        GlideLoad.with(imageView)
                .load("https://vinkalife-1255635395.cos.ap-hongkong.myqcloud.com/app/img/model/model-2.png")
                .show(imageView);
    }


}

package com.ashlikun.glideutils;

import android.content.Context;

import com.ashlikun.glideutils.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/29　17:42
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：GlideModule
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        try {
            registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否读取清单文件配置，4.0后就不需要了
     *
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        if (GlideUtils.diskLruCacheFactory == null) {
            GlideUtils.diskLruCacheFactory = new InternalCacheDiskCacheFactory(context);
        }
        builder.setDiskCache(GlideUtils.diskLruCacheFactory);
//        builder.setMemoryCache(new LruResourceCache())
        RequestOptions REQUEST_OPTIONS = new RequestOptions();
        REQUEST_OPTIONS.error(GlideUtils.getErrorRes());
        REQUEST_OPTIONS.placeholder(GlideUtils.getPlaceholderRes());
        REQUEST_OPTIONS.sizeMultiplier(0.8f);
        REQUEST_OPTIONS.format(DecodeFormat.PREFER_RGB_565);
        REQUEST_OPTIONS.diskCacheStrategy(DiskCacheStrategy.ALL);
        builder.setDefaultRequestOptions(REQUEST_OPTIONS);
    }
}
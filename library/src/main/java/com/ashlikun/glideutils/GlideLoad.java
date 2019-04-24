package com.ashlikun.glideutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.ashlikun.glideutils.okhttp.ProgressListener;
import com.ashlikun.glideutils.okhttp.ProgressManage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/28 14:44
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public final class GlideLoad {


    private Context context;
    private Activity activity;
    private FragmentActivity activityF;
    private Fragment fragment;

    private ImageView imageView;
    private RequestOptions requestOptions;
    private Object path;
    private ProgressListener progressListener;
    private RequestListener requestListener;


    private GlideLoad() {
    }

    public static GlideLoad with(Activity activity) {
        GlideLoad glideLoad = new GlideLoad();
        glideLoad.activity = activity;
        return glideLoad;
    }

    public static GlideLoad with(ImageView imageView) {
        GlideLoad glideLoad = new GlideLoad();
        glideLoad.imageView = imageView;
        return glideLoad;
    }

    public static GlideLoad with(Context context) {
        GlideLoad glideLoad = new GlideLoad();
        glideLoad.context = context;
        return glideLoad;
    }

    public static GlideLoad with(FragmentActivity activityF) {
        GlideLoad glideLoad = new GlideLoad();
        glideLoad.activityF = activityF;
        return glideLoad;
    }

    public static GlideLoad with(Fragment fragment) {
        GlideLoad glideLoad = new GlideLoad();
        glideLoad.fragment = fragment;
        return glideLoad;
    }


    public GlideLoad options(RequestOptions requestOptions) {
        this.requestOptions = requestOptions;
        return this;
    }


    public GlideLoad progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public GlideLoad requestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    public GlideLoad load(Object path) {
        this.path = path;
        return this;
    }

    public ViewTarget<ImageView, Drawable> show(ImageView view) {
        this.imageView = view;
        RequestBuilder<Drawable> requestBuilder = show();
        return requestBuilder.into(view);
    }

    public Target<Drawable> show(Target view) {
        RequestBuilder<Drawable> requestBuilder = show();
        return requestBuilder.into(view);
    }

    private RequestBuilder<Drawable> show() {
        RequestBuilder<Drawable> requestBuilder = getRequest();
        if (progressListener != null) {
            ProgressManage.add(path, progressListener);
            requestBuilder.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (requestListener != null) {
                        requestListener.onLoadFailed(e, model, target, isFirstResource);
                    }
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                    ProgressManage.remove(path);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (requestListener != null) {
                        requestListener.onResourceReady(resource, model, target, dataSource, isFirstResource);
                    }
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                    ProgressManage.remove(path);
                    return false;
                }
            });
        } else {
            if (requestListener != null) {
                requestBuilder.listener(requestListener);
            }
        }
        if (requestOptions != null) {
            return requestBuilder.apply(requestOptions);
        } else {
            return requestBuilder;
        }
    }

    private RequestBuilder<Drawable> getRequest() {
        if (activityF != null) {
            return Glide.with(activityF).load(GlideUtils.getHttpFileUrl(path));
        } else if (activity != null) {
            return Glide.with(activity).load(GlideUtils.getHttpFileUrl(path));
        } else if (fragment != null) {
            return Glide.with(fragment).load(GlideUtils.getHttpFileUrl(path));
        } else if (context != null) {
            return Glide.with(context).load(GlideUtils.getHttpFileUrl(path));
        } else if (imageView != null) {
            return Glide.with(imageView).load(GlideUtils.getHttpFileUrl(path));
        }
        return null;
    }

    public RequestOptions scaleType(ImageView imageView) {
        if (imageView == null || imageView.getScaleType() == null || requestOptions == null) {
            return requestOptions;
        }
        Transformation scaleTypeTf = null;
        switch (imageView.getScaleType()) {
            case CENTER_CROP:
                scaleTypeTf = new CenterCrop();
                break;
            case CENTER_INSIDE:
                scaleTypeTf = new CenterInside();
                break;
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
                scaleTypeTf = new FitCenter();
                break;
            case FIT_XY:
                scaleTypeTf = new CenterInside();
                break;
            case CENTER:
            case MATRIX:
                break;
        }
        if (scaleTypeTf == null) {
            return requestOptions;
        }
        Map<Class<?>, Transformation<?>> map = requestOptions.getTransformations();
        ArrayList<Transformation> arrayList = new ArrayList<>();
        arrayList.add(scaleTypeTf);
        for (Map.Entry<Class<?>, Transformation<?>> a : map.entrySet()) {
            arrayList.add(a.getValue());
        }
        requestOptions.getTransformations().clear();
        requestOptions.transforms(arrayList.toArray(new Transformation[0]));
        return requestOptions;
    }
}

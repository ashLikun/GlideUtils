package com.ashlikun.glideutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.ashlikun.glideutils.okhttp.ProgressListener;
import com.ashlikun.glideutils.okhttp.ProgressManage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

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
    //错误图的缩放类型
    private ImageView.ScaleType errorScaleType = GlideUtils.getErrorScaleType();
    //展位图的缩放类型
    private ImageView.ScaleType placeholderScaleType = GlideUtils.getPlaceholderScaleType();
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


    public GlideLoad errorScaleType(ImageView.ScaleType errorScaleType) {
        this.errorScaleType = errorScaleType;
        return this;
    }

    public GlideLoad placeholderScaleType(ImageView.ScaleType placeholderScaleType) {
        this.placeholderScaleType = placeholderScaleType;
        return this;
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
        //设置缩放类型
        if (imageView != null) {
            ImageView.ScaleType old = imageView.getScaleType();
            if (old != placeholderScaleType) {
                imageView.setScaleType(placeholderScaleType);
                imageView.setTag(999998988, true);
                imageView.setTag(999998989, old);
            }
        }
        return requestBuilder.into(view);
    }

    public Target<Drawable> show(Target view) {
        RequestBuilder<Drawable> requestBuilder = show();
        Target<Drawable> target = requestBuilder.into(view);
        ImageView imageView = getImageView(target);
        if (imageView != null) {
            ImageView.ScaleType old = imageView.getScaleType();
            if (old != placeholderScaleType) {
                imageView.setScaleType(placeholderScaleType);
                imageView.setTag(999998988, true);
                imageView.setTag(999998989, old);
            }
        }
        return target;
    }

    private RequestBuilder<Drawable> show() {
        RequestBuilder<Drawable> requestBuilder = getRequest();
        if (progressListener != null) {
            ProgressManage.add(path, progressListener);
        }

        requestBuilder.listener(new RequestListener<Drawable>() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                //设置加载失败图为
                ImageView imageView = getImageView(target);
                if (imageView != null) {
                    ImageView.ScaleType old = imageView.getScaleType();
                    if (old != errorScaleType) {
                        imageView.setScaleType(errorScaleType);
                        imageView.setTag(999998988, true);
                        imageView.setTag(999998989, old);
                    }
                }
                if (requestListener != null) {
                    requestListener.onLoadFailed(e, model, target, isFirstResource);
                }
                if (progressListener != null) {
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                }
                ProgressManage.remove(path);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //还原图片缩放类型
                ImageView imageView = getImageView(target);
                boolean isFailedSetScale = false;
                if (imageView.getTag(999998988) != null) {
                    isFailedSetScale = (boolean) imageView.getTag(999998988);
                }
                ImageView.ScaleType oldScaleType = null;
                if (imageView.getTag(999998989) != null) {
                    oldScaleType = (ImageView.ScaleType) imageView.getTag(999998989);
                }
                if (isFailedSetScale && oldScaleType != null) {
                    //还原
                    imageView.setScaleType(oldScaleType);
                    imageView.setTag(999998988, null);
                    imageView.setTag(999998989, null);
                }
                if (requestListener != null) {
                    requestListener.onResourceReady(resource, model, target, dataSource, isFirstResource);
                }
                if (progressListener != null) {
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                }
                ProgressManage.remove(path);
                return false;
            }
        });
        if (requestOptions != null) {
            return requestBuilder.apply(requestOptions);
        } else {
            return requestBuilder;
        }
    }

    private ImageView getImageView(Target<Drawable> target) {
        if (target instanceof ImageViewTarget) {
            return (ImageView) ((ImageViewTarget) target).getView();
        } else if (target instanceof CustomViewTarget) {
            View view = ((ImageViewTarget) target).getView();
            if (view instanceof ImageView) {
                return imageView = ((ImageView) view);
            }
        }
        return null;
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


}

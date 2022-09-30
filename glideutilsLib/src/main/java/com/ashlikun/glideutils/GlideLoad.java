package com.ashlikun.glideutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ashlikun.glideutils.okhttp.ProgressListener;
import com.ashlikun.glideutils.okhttp.ProgressManage;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.List;

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
    private TransitionOptions transitionOptions;
    private Object path;
    //错误图的缩放类型
    private ImageView.ScaleType errorScaleType = GlideUtils.getErrorScaleType();
    //展位图的缩放类型
    private ImageView.ScaleType placeholderScaleType = GlideUtils.getPlaceholderScaleType();

    private ProgressListener progressListener;
    private List<RequestListener> requestListener;

    private GlideRequest<Drawable> requestBuilder = null;

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

    public GlideLoad transition(TransitionOptions transitionOptions) {
        this.transitionOptions = transitionOptions;
        return this;
    }

    public GlideLoad progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public GlideLoad listener(RequestListener listener) {
        this.requestListener = new ArrayList<>();
        return addListener(listener);
    }

    public GlideLoad addListener(RequestListener listener) {
        if (listener != null) {
            if (requestListener == null) {
                this.requestListener = new ArrayList<>();
            }
            this.requestListener.add(listener);
        }
        return this;
    }

    public GlideLoad load(Object path) {
        this.path = path == null ? "" : path;
        return this;
    }

    public ViewTarget<ImageView, Drawable> show(ImageView view) {
        this.imageView = view;
        GlideRequest<Drawable> requestBuilder = builder();
        //设置缩放类型
        setScaleType();
        return requestBuilder.into(view);
    }

    public Target<Drawable> show(Target view) {
        GlideRequest<Drawable> requestBuilder = builder();
        Target<Drawable> target = requestBuilder.into(view);
        getImageView(target);
        setScaleType();
        return target;
    }

    public void setScaleType() {
        if (imageView != null) {
            ImageView.ScaleType old = imageView.getScaleType();
            if (old != placeholderScaleType) {
                imageView.setScaleType(placeholderScaleType);
                imageView.setTag(999998988, true);
                imageView.setTag(999998989, old);
            }
        }
    }

    /**
     * 构建一个GlideRequest 可以再次扩展
     *
     * @return
     */
    public GlideRequest<Drawable> builder() {
        if (requestBuilder != null) {
            return requestBuilder;
        }
        requestBuilder = getRequest();
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
                if (progressListener != null) {
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                    ProgressManage.remove(path);
                }

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //还原图片缩放类型
                ImageView imageView = getImageView(target);
                if (imageView != null) {
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
                }
                if (progressListener != null) {
                    progressListener.onProgress(ProgressManage.getTotalBytesRead(path), ProgressManage.getContentLength(path), true);
                    ProgressManage.remove(path);
                }
                return false;
            }
        });
        if (requestListener != null) {
            for (RequestListener listener : requestListener) {
                requestBuilder.addListener(listener);
            }
        }
        if (requestOptions != null) {
            requestBuilder = requestBuilder.apply(requestOptions);
        }
        if (transitionOptions != null) {
            requestBuilder = requestBuilder.transition(transitionOptions);
        }
        return requestBuilder;
    }

    private ImageView getImageView(Target<Drawable> target) {
        if (imageView == null) {
            return imageView = GlideUtils.getImageView(target);
        } else {
            return imageView;
        }
    }

    private GlideRequest<Drawable> getRequest() {
        if (activityF != null) {
            return GlideApp.with(activityF).load(path);
        } else if (activity != null) {
            return GlideApp.with(activity).load(path);
        } else if (fragment != null) {
            return GlideApp.with(fragment).load(path);
        } else if (context != null) {
            return GlideApp.with(context).load(path);
        } else if (imageView != null) {
            return GlideApp.with(imageView).load(path);
        }
        return null;
    }


}

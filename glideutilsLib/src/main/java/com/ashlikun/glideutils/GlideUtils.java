package com.ashlikun.glideutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.lang.reflect.Field;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.OkHttpClient;


/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/29 16:59
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：Glide的封装
 * Object的参数可以是资源id，网络文件，本地文件，url
 */

public class GlideUtils {

    private static boolean DEBUG;
    private static Context context;
    private static int errorRes = R.drawable.material_default_image_1_1;
    private static int placeholderRes = R.color.glide_placeholder_color;
    //错误图的缩放类型
    private static ImageView.ScaleType errorScaleType = ImageView.ScaleType.CENTER_INSIDE;
    //展位图的缩放类型
    private static ImageView.ScaleType placeholderScaleType = ImageView.ScaleType.CENTER_INSIDE;

    static DiskLruCacheFactory diskLruCacheFactory = null;
    public static volatile Call.Factory internalClient;

    public static void init(Context context, OkHttpClient okHttpClient) {
        GlideUtils.context = context;
        internalClient = okHttpClient;
    }

    /**
     * 设置缓存,在application设置
     */
    public static void setDiskLruCacheFactory(DiskLruCacheFactory diskLruCacheFactory) {
        GlideUtils.diskLruCacheFactory = diskLruCacheFactory;
    }

    /**
     * 是否调试
     */
    public static void setDEBUG(boolean DEBUG) {
        GlideUtils.DEBUG = DEBUG;
    }

    public static boolean isDEBUG() {
        return DEBUG;
    }


    public static int getErrorRes() {
        return errorRes;
    }

    /**
     * 设置默认的错误图片
     *
     * @param errorRes
     */
    public static void setErrorRes(int errorRes) {
        GlideUtils.errorRes = errorRes;
    }

    public static int getPlaceholderRes() {
        return placeholderRes;
    }

    /**
     * 设置默认的展位图
     *
     * @param placeholderRes
     */
    public static void setPlaceholderRes(int placeholderRes) {
        GlideUtils.placeholderRes = placeholderRes;
    }

    public static ImageView.ScaleType getErrorScaleType() {
        return errorScaleType;
    }

    public static void setErrorScaleType(ImageView.ScaleType errorScaleType) {
        GlideUtils.errorScaleType = errorScaleType;
    }

    public static ImageView.ScaleType getPlaceholderScaleType() {
        return placeholderScaleType;
    }

    public static void setPlaceholderScaleType(ImageView.ScaleType placeholderScaleType) {
        GlideUtils.placeholderScaleType = placeholderScaleType;
    }

    /**
     * 下载
     */
    public static void downloadBitmap(Object context, String url, OnDownloadCallback downloadCallbacl) {
        downloadFile(context, url, downloadCallbacl);
    }

    /**
     * 加载缓存
     */
    public static void downloadCache(Object context, String url, OnDownloadCallback downloadCallbacl) {
        File file = getCache(url);
        if (file == null || !file.exists()) {
            //没有缓存，加载缓存
            downloadFile(context, url, downloadCallbacl);
        } else {
            //已经有缓存就使用缓存回调
            if (downloadCallbacl != null) {
                downloadCallbacl.onCall(file);
            }
        }
    }

    /**
     * 下载文件
     */
    public static void downloadFile(Object context, String url, final OnDownloadCallback downloadCallbacl) {
        load(context).download(url == null ? "" : url).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                if (downloadCallbacl != null) {
                    downloadCallbacl.onCall(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (downloadCallbacl != null) {
                    downloadCallbacl.onCall(null);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    /**
     * 下载文件
     */
    public static void getFile(Object context, String url, Target<File> target) {
        if (target == null) {
            target = new OnCustomTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                }
            };
        }
        load(context).asFile().load(url == null ? "" : url).into(target);
    }

    /**
     * 下载Bitmap
     */
    public static void getBitmap(Object context, String url, Target<Bitmap> target) {
        if (target == null) {
            target = new OnCustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                }
            };
        }
        load(context).asBitmap().load(url == null ? "" : url).into(target);
    }

    /**
     * 下载Draweable  一般如果不知道是Bitmap还是Drawable就用这个
     */
    public static void getDrawable(Object context, final String url, Target<Drawable> target) {
        if (target == null) {
            target = new OnCustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                }
            };
        }
        load(context).asDrawable().load(url == null ? "" : url).into(target);
    }

    /**
     * 是否是网络url或者文件
     */
    public static boolean isHttpOrFileUrl(String url) {
        if (url != null) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return true;
            } else if (url.startsWith("/storage") || url.startsWith("storage") || url.startsWith("/data") || url.startsWith("data")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取中间裁剪的圆角图
     *
     * @return
     */
    public static RequestOptions getRoundedOptions(int radius) {
        return new RequestOptions().transform(new CenterCrop(),
                new RoundedCornersTransformation(radius, 0));
    }

    /**
     * 获取中间裁剪的圆形
     *
     * @return
     */
    public static RequestOptions getCircleOptions() {
        return new RequestOptions().transform(new CenterCrop(),
                new CircleCrop());
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public static double getCacheSize(Context context) {
        try {
            return getFolderSize(getCacheDir(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        if (diskLruCacheFactory == null) {
            diskLruCacheFactory = new InternalCacheDiskCacheFactory(context);
        }
        //反射获取私有属性
        try {
            Field field = DiskLruCacheFactory.class.getDeclaredField("cacheDirectoryGetter");
            field.setAccessible(true);
            DiskLruCacheFactory.CacheDirectoryGetter cacheDirectoryGetter = (DiskLruCacheFactory.CacheDirectoryGetter) field.get(diskLruCacheFactory);
            return cacheDirectoryGetter.getCacheDirectory();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这个图片是否有缓存
     * 只能在子线程
     *
     * @param url
     * @return
     */
    public static boolean isCache(String url) {
        return getCache(url) != null;
    }

    /**
     * 获取缓存文件
     * 只能在子线程
     * onlyRetrieveFromCache 是否只是从缓存中获取
     */
    public static File getCache(String url) {
        try {
            if (diskLruCacheFactory == null) {
                diskLruCacheFactory = new InternalCacheDiskCacheFactory(context);
            }
            DiskCache wrapper = diskLruCacheFactory.build();
            return wrapper.get(new GlideUrl(url));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static ImageView getImageView(Target<Drawable> target) {
        if (target instanceof ImageViewTarget) {
            return (ImageView) ((ImageViewTarget) target).getView();
        } else if (target instanceof CustomViewTarget) {
            View view = ((CustomViewTarget) target).getView();
            if (view instanceof ImageView) {
                return ((ImageView) view);
            }
        } else if (target instanceof ViewTarget) {
            View view = ((ViewTarget) target).getView();
            if (view instanceof ImageView) {
                return ((ImageView) view);
            }
        }
        return null;
    }

    public static GlideRequests load(Object context) {
        if (context instanceof FragmentActivity) {
            return GlideApp.with((FragmentActivity) context);
        } else if (context instanceof Activity) {
            return GlideApp.with((Activity) context);
        } else if (context instanceof Fragment) {
            return GlideApp.with((Fragment) context);
        } else if (context instanceof ImageView) {
            return GlideApp.with((ImageView) context);
        } else if (context instanceof Context) {
            return GlideApp.with((Context) context);
        }
        throw new NullPointerException("context error");
    }
}

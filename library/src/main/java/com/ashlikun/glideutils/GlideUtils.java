package com.ashlikun.glideutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
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
    private static int errorRes = R.drawable.material_default_image_1_1;
    private static int placeholderRes = R.color.glide_placeholder_color;
    //错误图的缩放类型
    private static ImageView.ScaleType errorScaleType = ImageView.ScaleType.CENTER_INSIDE;
    //展位图的缩放类型
    private static ImageView.ScaleType placeholderScaleType = ImageView.ScaleType.CENTER_INSIDE;

    static DiskLruCacheFactory diskLruCacheFactory = null;
    public static volatile Call.Factory internalClient;

    public static void init(OkHttpClient okHttpClient) {
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
     * 给ImageView 设置网络图片（圆形）
     */
    public static ViewTarget<ImageView, Drawable> showCircle(ImageView imageView, Object path) {
        return show(imageView, path, getCircleOptions());
    }

    public static ViewTarget<ImageView, Drawable> show(ImageView imageView, Object path) {
        return show(imageView, path, null);
    }

    public static ViewTarget<ImageView, Drawable> show(ImageView view, Object path, RequestOptions requestOptions) {
        return GlideLoad.with(view)
                .load(path)
                .options(requestOptions)
                .show(view);
    }

    public static Target<Drawable> show(Context context, Target view, Object path, RequestOptions requestOptions) {
        return GlideLoad.with(context)
                .load(path)
                .options(requestOptions)
                .show(view);
    }

    public static ViewTarget<ImageView, Drawable> show(Fragment fragment, ImageView view, Object path, RequestOptions requestOptions) {
        return GlideLoad.with(fragment)
                .load(path)
                .options(requestOptions)
                .show(view);
    }

    public static Target<Drawable> show(Fragment fragment, Target view, Object path, RequestOptions requestOptions) {
        return GlideLoad.with(fragment)
                .load(path)
                .options(requestOptions)
                .show(view);
    }


    /**
     * 下载
     */
    public static void downloadBitmap(final Context context, final String url, final OnDownloadCallback downloadCallbacl) {
        GlideApp.with(context).download(getHttpFileUrl(url)).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                downloadCallbacl.onCall(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                downloadCallbacl.onCall(null);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    /**
     * 下载文件
     */
    public static void getFile(final Context context, final String url, final Target<File> downloadCallbacl) {
        GlideApp.with(context).asFile().load(getHttpFileUrl(url)).into(downloadCallbacl);
    }

    /**
     * 下载Bitmap
     */
    public static void getBitmap(final Context context, final String url, final Target<Bitmap> downloadCallbacl) {
        GlideApp.with(context).asBitmap().load(getHttpFileUrl(url)).into(downloadCallbacl);
    }

    /**
     * 下载Draweable  一般如果不知道是Bitmap还是Drawable就用这个
     */
    public static void getDrawable(final Context context, final String url, final Target<Drawable> downloadCallbacl) {
        GlideApp.with(context).asDrawable().load(getHttpFileUrl(url)).into(downloadCallbacl);
    }

    /**
     * 对网络资源文件判断路径 如果是已http开头的就返回这个值 否则在前面加上ORG
     */
    public static Object getHttpFileUrl(Object url) {
        if (url instanceof String) {
            String res = "";
            if (url != null) {
                if (((String) url).startsWith("http://") || ((String) url).startsWith("https://")) {
                    res = (String) url;
                } else if (((String) url).startsWith("/storage") || ((String) url).startsWith("storage") || ((String) url).startsWith("/data") || ((String) url).startsWith("data")) {
                    res = (String) url;
                }
            }
            return res;
        }
        return url;
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
    public static boolean isCache(Context context, String url) {
        return getCache(context, url) != null;
    }

    /**
     * 获取缓存文件
     * 只能在子线程
     * onlyRetrieveFromCache 是否只是从缓存中获取
     *
     * @param context
     * @param url
     * @return
     */
    public static File getCache(Context context, String url) {
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

}

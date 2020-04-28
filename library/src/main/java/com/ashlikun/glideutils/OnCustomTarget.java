package com.ashlikun.glideutils;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;

/**
 * 作者　　: 李坤
 * 创建时间: 2017/12/29　17:06
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：简化 {@link CustomTarget}
 */

public abstract class OnCustomTarget<T> extends CustomTarget<T> {

    public OnCustomTarget(int width, int height) {
        super(width, height);
    }

    public OnCustomTarget(int size) {
        super(size, size);
    }

    public OnCustomTarget() {
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }
}

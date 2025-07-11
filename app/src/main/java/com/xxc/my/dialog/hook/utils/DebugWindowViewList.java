package com.xxc.my.dialog.hook.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.xxc.my.dialog.hook.app.MyApplication;
import com.xxc.my.dialog.hook.dialog.DialogPriority;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * hook替换WindowManagerGlobal中的mViews
 */
public class DebugWindowViewList extends ArrayList<View> {

    public DebugWindowViewList(int initialCapacity) {
        super(initialCapacity);
    }

    public DebugWindowViewList() {
    }

    public DebugWindowViewList(@NonNull Collection<? extends View> c) {
        super(c);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        int index = super.indexOf(o);
        Log.d(MyApplication.TAG, "indexOf: " + index);
        return index;
    }

    @Override
    public boolean add(View view) {
        tryFindWindow(view);
        return super.add(view);
    }

    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi", "BlockedPrivateApi", "DiscouragedPrivateApi"})
    private void tryFindWindow(View view) {
        try {
            Class<?> decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
            Field windowField = decorViewClazz.getDeclaredField("mWindow");
            windowField.setAccessible(true);
            Window window = (Window) windowField.get(view);
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 强制将flag还原
     *
     * @param attrs {@link WindowManager.LayoutParams}
     * @param flags flag，强制设置为0
     * @param mask  mask
     */
    public void clearFlag(WindowManager.LayoutParams attrs, int flags, int mask) {
        // final WindowManager.LayoutParams attrs = getAttributes();
        attrs.flags = (attrs.flags & ~mask) | (flags & mask);
    }
}
package com.xxc.my.dialog.hook.utils;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;


import com.xxc.my.dialog.hook.app.MyApplication;

import java.util.ArrayList;
import java.util.Collection;

public class DebugWindowLayoutParamList extends ArrayList<WindowManager.LayoutParams> {

    public DebugWindowLayoutParamList(int initialCapacity) {
        super(initialCapacity);
    }

    public DebugWindowLayoutParamList() {
    }

    public DebugWindowLayoutParamList(
            @NonNull Collection<? extends WindowManager.LayoutParams> c) {
        super(c);
    }

    @Override
    public void add(int index, WindowManager.LayoutParams element) {
        Log.d(MyApplication.TAG, "add: " + index);
        super.add(index, clearFlag(element, 0, WindowManager.LayoutParams.FLAG_SECURE));
    }

    /**
     * 强制将flag还原
     *
     * @param attrs {@link WindowManager.LayoutParams}
     * @param flags flag，强制设置为0
     * @param mask  mask
     */
    public WindowManager.LayoutParams clearFlag(WindowManager.LayoutParams attrs, int flags, int mask) {
        // final WindowManager.LayoutParams attrs = getAttributes();
        attrs.flags = (attrs.flags & ~mask) | (flags & mask);
        return attrs;
    }
}
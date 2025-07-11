package com.xxc.my.dialog.hook.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class DebugViewRootImplList<T> extends ArrayList<T> {

    public DebugViewRootImplList(int initialCapacity) {
        super(initialCapacity);
    }

    public DebugViewRootImplList() {
    }

    public DebugViewRootImplList(@NonNull Collection<? extends T> c) {
        super(c);
    }

}

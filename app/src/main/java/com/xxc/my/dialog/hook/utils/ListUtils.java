package com.xxc.my.dialog.hook.utils;

import java.util.Collection;

public class ListUtils {

    public static boolean isEmpty(Collection<?> c) {
        return null == c || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }
}

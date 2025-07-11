package com.xxc.my.dialog.hook.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.DialogShadow;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xxc.my.dialog.hook.dialog.DialogManager;
import com.xxc.my.dialog.hook.utils.DebugViewRootImplList;
import com.xxc.my.dialog.hook.utils.DebugWindowLayoutParamList;
import com.xxc.my.dialog.hook.utils.DebugWindowViewList;

import java.lang.reflect.Field;
import java.util.ArrayList;

import me.weishu.reflection.Reflection;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        // init(this);
        // 在 application启动的时候启动Dialog管理器，在主线程空闲时进行Dialog弹窗展示
        Looper.getMainLooper().getQueue().addIdleHandler(DialogManager.getInstance());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Reflection.unseal(base);
    }

    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi", "BlockedPrivateApi"})
    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(new SimpleLifeCycleCallBack() {

            @Override
            public void onActivityCreated(@NonNull Activity activity,
                    @Nullable Bundle savedInstanceState) {
                // 这个WindowManager 其实是 WindowManagerImpl
                WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                tryHookWindowManagerImpl(windowManager);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });
    }

    @SuppressLint({"PrivateApi", "SoonBlockedPrivateApi", "BlockedPrivateApi", "DiscouragedPrivateApi"})
    private static void tryHookWindowManagerImpl(WindowManager windowManager) {
        try {
            Class<?> wmi = Class.forName("android.view.WindowManagerImpl");
            Field globalField = wmi.getDeclaredField("mGlobal");
            globalField.setAccessible(true);
            Object global = globalField.get(windowManager);
            Class<?> windowManagerGlobalClazz = Class.forName("android.view.WindowManagerGlobal");
            Field viewsField = windowManagerGlobalClazz.getDeclaredField("mViews");
            Field viewRootField = windowManagerGlobalClazz.getDeclaredField("mRoots");
            Field paramsField = windowManagerGlobalClazz.getDeclaredField("mParams");
            viewsField.setAccessible(true);
            viewRootField.setAccessible(true);
            paramsField.setAccessible(true);
            ArrayList<View> views = (ArrayList<View>) viewsField.get(global);
            ArrayList<?> viewRoots = (ArrayList<?>) viewRootField.get(global);
            ArrayList<WindowManager.LayoutParams> params = (ArrayList<WindowManager.LayoutParams>) paramsField.get(global);

            DebugWindowViewList viewList = null != views ? new DebugWindowViewList(views) : new DebugWindowViewList();
            DebugViewRootImplList<?> viewRootImplList = null != views ? new DebugViewRootImplList<>(viewRoots) : new DebugViewRootImplList();
            DebugWindowLayoutParamList paramList = null != params ? new DebugWindowLayoutParamList(params) : new DebugWindowLayoutParamList();

            viewsField.set(global, viewList);
            viewRootField.set(global, viewRootImplList);
            paramsField.set(global, paramList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.tangerine.wechatchatroomhelper.Hooker;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by 24481 on 2018/12/21.
 */

public class WechatLogHooker {

    public static void doHook(final XC_LoadPackage.LoadPackageParam lpparam) {

        Class clazz = XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.y",lpparam.classLoader);
        XposedHelpers.setStaticIntField(clazz, "level", -1);

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "b", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: b " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.i(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: d " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.d(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "e", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: e " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.e(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "f", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: f " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.e(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "i", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: i " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.i(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "l", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: l " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.i(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "v", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: v " + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.v(TAG, params == null ? message : String.format(message, params));
            }
        });

        findAndHookMethod("com.tencent.mm.sdk.platformtools.y", lpparam.classLoader, "w", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String TAG = "Xposed: w" + param.args[0];
                String message = (String) param.args[1];
                Object[] params = (Object[]) param.args[2];
                Log.w(TAG, params == null ? message : String.format(message, params));
            }
        });
    }
}

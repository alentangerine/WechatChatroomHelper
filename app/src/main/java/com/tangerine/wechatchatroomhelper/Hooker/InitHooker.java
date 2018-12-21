package com.tangerine.wechatchatroomhelper.Hooker;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.tangerine.wechatchatroomhelper.Entry;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 */

public class InitHooker {

    public static void doHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        //对LauncherUI的onCreate挂钩以获取一个实例用于启动群消息所属Activity，使用SharedPreferences保存的隐藏群聊名单也在此初始化
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Entry.LaunchUIInstance = (Activity) param.thisObject;
                SharedPreferences sp = AndroidAppHelper.currentApplication().getSharedPreferences("hidelist", Context.MODE_PRIVATE);
                int size = sp.getInt("size", 0);
                for (int i = 0; i < sp.getInt("size", 0); ++i) {
                    Entry.hideList.add(sp.getString(String.valueOf(i), ""));
                }
            }
        });

        //一旦LauncherUI的onStop触发则保存隐藏群聊名单
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader, "onStop", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SharedPreferences sp = AndroidAppHelper.currentApplication().getSharedPreferences("hidelist", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.putInt("size", Entry.hideList.size());
                for (int i = 0; i < Entry.hideList.size(); ++i) {
                    editor.putString(String.valueOf(i), Entry.hideList.get(i));
                }
                editor.commit();
            }
        });

        //拿到订阅号的Activity，点击群助手的Item时手动finish，从而使主界面onResume。否则在群助手页面点击聊天item将由于当前Activity没有onPause而无法跳转
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.BizConversationUI", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Entry.BizUIInstance = (Activity) param.thisObject;
            }
        });

    }

}

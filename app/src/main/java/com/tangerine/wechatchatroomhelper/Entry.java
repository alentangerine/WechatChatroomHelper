package com.tangerine.wechatchatroomhelper;

import android.app.Activity;
import android.widget.BaseAdapter;

import com.tangerine.wechatchatroomhelper.Adapter.ChatRoomAdapter;
import com.tangerine.wechatchatroomhelper.Hooker.InitHooker;
import com.tangerine.wechatchatroomhelper.Hooker.ViewHooker;
import com.tangerine.wechatchatroomhelper.Hooker.WechatDatabaseHooker;
import com.tangerine.wechatchatroomhelper.Hooker.WechatLogHooker;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 */

public class Entry implements IXposedHookLoadPackage {

    static public ArrayList<String> hideList = new ArrayList();
    static public BaseAdapter conversationAdapter;     //主页面上的Adapter，用于调用对消息时间和内容进行初始化的函数
    static public ChatRoomAdapter chatroomAdapter = new ChatRoomAdapter();
    static public Activity LaunchUIInstance;            //主页面Activity的实例
    static public Activity BizUIInstance;               //订阅号页面Activity的实例

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.contains("com.tencent.mm")) {
            InitHooker.doHook(loadPackageParam);
            WechatDatabaseHooker.doHook(loadPackageParam);
            ViewHooker.doHook(loadPackageParam);
            //WechatLogHooker.doHook(loadPackageParam);
        }
    }
}

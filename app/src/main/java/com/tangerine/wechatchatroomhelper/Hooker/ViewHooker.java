package com.tangerine.wechatchatroomhelper.Hooker;

import android.app.AndroidAppHelper;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tangerine.wechatchatroomhelper.Entry;
import com.tangerine.wechatchatroomhelper.Model.ChatItemViewModel;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 */

public class ViewHooker {

    static int count = -1;

    public static void doHook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        //hook getCount强制+1，留出位置给新的view
        XposedHelpers.findAndHookMethod("com.tencent.mm.cf.a.g", loadPackageParam.classLoader, "getCount", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                count = (int) param.getResult();
                param.setResult(count+1);
            }
        });

        //hook onChattingItemClick用于设置启动群消息助手
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.f", loadPackageParam.classLoader, "onItemClick", AdapterView.class, View.class, int.class, long.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("from onItemClick: You click position " + String.valueOf(param.args[2]) + ", id " +String.valueOf(param.args[3]));
                if ((int) param.args[2] == count + 11) {
                    Intent intent = new Intent(Entry.LaunchUIInstance, XposedHelpers.findClass("com.tencent.mm.ui.conversation.BizConversationUI", loadPackageParam.classLoader));
                    Bundle b = new Bundle();
                    b.putString("enterprise_biz_display_name", "群消息助手");
                    intent.putExtras(b);
                    Entry.LaunchUIInstance.startActivity(intent);
                }
            }
        });

        //hook conversation的getView，当绘制最底部时直接设置流程为绘制消息助手
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.h", loadPackageParam.classLoader, "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int id = (int) param.args[0];
                if (id == count) {
                    View result;
                    ChatItemViewModel itemViewModel;
                    if (param.args[1] == null) {
                        itemViewModel = new ChatItemViewModel(AndroidAppHelper.currentApplication().getApplicationContext(), loadPackageParam, null);
                        result = itemViewModel.getItem();
                        result.setTag(itemViewModel.getViewHolder());
                    } else {
                        result = (View) param.args[1];
                        itemViewModel = new ChatItemViewModel(AndroidAppHelper.currentApplication().getApplicationContext(), loadPackageParam, (ViewGroup) result);
                        itemViewModel.setNickname("消息助手");
                        itemViewModel.setTime("现在");
                        itemViewModel.setContent("新消息提示banner");
                        itemViewModel.setRedPoint(View.GONE);
                    }
                    param.setResult(result);
                }
            }
        });

        //hook onCreateContextView 增加id = 11的选项 “收入聊天助手”，11是逆向得到的Magic Number
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.g", loadPackageParam.classLoader, "onCreateContextMenu", ContextMenu.class, View.class, ContextMenu.ContextMenuInfo.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String talker = (String) XposedHelpers.getObjectField(param.thisObject, "talker");
                if (talker.contains("@chatroom")) {
                    ContextMenu menu = (ContextMenu) param.args[0];
                    menu.add(((AdapterView.AdapterContextMenuInfo) param.args[2]).position, 11, 0, "收入群消息助手");
                }
            }
        });

        //hook onMMMenuItemSelected，为新增的聊天助手添加相关功能
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.g$1", loadPackageParam.classLoader, "onMMMenuItemSelected", MenuItem.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                MenuItem item = (MenuItem) param.args[0];
                if (item.getItemId() == 11) {
                    Object fatherInstance = XposedHelpers.getObjectField(param.thisObject, "vQT");
                    String talker = (String) XposedHelpers.getObjectField(fatherInstance, "talker");
                    Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(), "隐藏将在该群收到新消息后生效，你也可以手动点进该窗口触发收纳", Toast.LENGTH_SHORT).show();
                    Entry.hideList.add(talker);
                }
            }
        });

        //由于群助手实际上与订阅号共用一个Activity，需要通过Bundle中的内容判断是否需要修改点击事件
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.BizConversationUI$a", loadPackageParam.classLoader, "onActivityCreated", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("title is " + XposedHelpers.getObjectField(param.thisObject, "vPt"));
                if (((String) XposedHelpers.getObjectField(param.thisObject, "vPt")).contains("群消息助手")) {
                    ListView l = (ListView) XposedHelpers.getObjectField(param.thisObject, "vPr");
                    XposedHelpers.callMethod(l, "setAdapter", new Class[]{ListAdapter.class}, Entry.chatroomAdapter);
                    l.setClickable(true);
                    l.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                    l.setOnItemClickListener(Entry.chatroomAdapter.new mOnItemClickListener());
                    l.setOnItemLongClickListener(Entry.chatroomAdapter.new mOnItemLongClickListener());

                }
            }
        });

        //拿到主页的adapter并保存
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.conversation.ConversationWithAppBrandListView", loadPackageParam.classLoader, "setAdapter", ListAdapter.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Entry.conversationAdapter = (BaseAdapter) param.args[0];
            }
        });
    }
}

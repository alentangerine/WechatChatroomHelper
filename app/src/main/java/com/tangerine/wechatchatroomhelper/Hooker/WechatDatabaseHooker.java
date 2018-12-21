package com.tangerine.wechatchatroomhelper.Hooker;

import android.database.Cursor;

import com.tangerine.wechatchatroomhelper.Entry;
import com.tangerine.wechatchatroomhelper.Model.ChatInfoModel;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 */

public class WechatDatabaseHooker {

    static Class CursorFactory;
    static Class CancellationSignal;
    static Class SQLiteDatabase;

    public static void doHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        CursorFactory = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase$CursorFactory", loadPackageParam.classLoader);
        CancellationSignal = XposedHelpers.findClass("com.tencent.wcdb.support.CancellationSignal", loadPackageParam.classLoader);
        SQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", loadPackageParam.classLoader);
        queryHook(loadPackageParam);
    }

    public static void queryHook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", loadPackageParam.classLoader, "rawQueryWithFactory", CursorFactory, String.class, String[].class, String.class, CancellationSignal, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String arg3 = (String) param.args[1];

                if(arg3.contains("select unReadCount, status, isSend") && arg3.contains("from rconversation") && arg3.contains("and rconversation.username != 'qmessage'")) {

                    //当查询语句是查询所有聊天信息时，拼接一条查询隐藏名单的信息并刷新隐藏名单，同时Listview刷新
                    StringBuilder queryHidingChatroom = new StringBuilder("select unReadCount, status, isSend, conversationTime, rcontact.nickname, rconversation.username, content, msgType,flag, digest, digestUser, attrflag, editingMsg, atCount, unReadMuteCount, UnReadInvite from rconversation, rcontact where rcontact.username = rconversation.username and (1 != 1");
                    for (String s: Entry.hideList) {
                        queryHidingChatroom.append(" or rconversation.username = '");
                        queryHidingChatroom.append(s);
                        queryHidingChatroom.append("'");
                    }
                    queryHidingChatroom.append(") order by flag desc");

                    Cursor cursor = (Cursor) XposedHelpers.callMethod(param.thisObject, "rawQueryWithFactory", new Class[]{CursorFactory, String.class, String[].class, String.class, CancellationSignal}, param.args[0], queryHidingChatroom.toString(), param.args[2], param.args[3], param.args[4]);

                    Entry.chatroomAdapter.clearData();
                    if(cursor.moveToFirst()) {
                        do {
                            if (Entry.hideList.contains(cursor.getString(cursor.getColumnIndex("username")))) {
                                ChatInfoModel chatInfo = new ChatInfoModel(cursor, loadPackageParam);
                                Entry.chatroomAdapter.addData(chatInfo);
                            }
                        } while (cursor.moveToNext());
                        Entry.chatroomAdapter.notifyDataSetChanged();
                    }

                    //在返回的结果中剔除隐藏名单，这个结果用于显示首页
                    param.args[1] = "select unReadCount, status, isSend, conversationTime, username, content, msgType, flag, digest, digestUser, attrflag, editingMsg, atCount, unReadMuteCount, UnReadInvite from rconversation where  ( parentRef is null  or parentRef = '' )  and ( 1 != 1  or rconversation.username like '%@im.chatroom' or rconversation.username like '%@chatroom' or rconversation.username like '%@openim' or rconversation.username not like '%@%' )  and rconversation.username != 'qmessage'";
                    for (String s: Entry.hideList) {
                        param.args[1] += " and rconversation.username != '";
                        param.args[1] += s;
                        param.args[1] += "'";
                    }
                    param.args[1] += " order by flag desc ";
                }
            }
        });
    }
}

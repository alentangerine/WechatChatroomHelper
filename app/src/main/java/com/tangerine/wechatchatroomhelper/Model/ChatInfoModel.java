package com.tangerine.wechatchatroomhelper.Model;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;

import com.tangerine.wechatchatroomhelper.Entry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 * 定义了从数据库中查询到的聊天信息，并提供了一些初始化显示相关内容的方法
 */

public class ChatInfoModel {

    private CharSequence field_username;
    private CharSequence field_nickname;
    private CharSequence field_content;
    private CharSequence field_digest;
    private CharSequence field_digestUser;
    private CharSequence field_editingMsg;
    private CharSequence field_msgType;
    private long field_conversationTime;
    private long field_flag;
    private int field_isSend;
    private int field_status;
    private int field_attrflag;
    private int field_atCount;
    private int field_unReadMuteCount;
    private int field_UnReadInvite;
    private int field_unReadCount;

    private Class adapterClass;
    private Class beanClass;
    private Constructor beanConstructor;
    private XC_LoadPackage.LoadPackageParam loadPackageParam;
    private Context ctx;

    public ChatInfoModel(Cursor cursor, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        field_username = cursor.getString(cursor.getColumnIndex("username"));
        field_nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        field_content = cursor.getString(cursor.getColumnIndex("content"));
        field_digest = cursor.getString(cursor.getColumnIndex("digest"));
        field_digestUser = cursor.getString(cursor.getColumnIndex("digestUser"));
        field_editingMsg = cursor.getString(cursor.getColumnIndex("editingMsg"));
        field_msgType = cursor.getString(cursor.getColumnIndex("msgType"));
        field_conversationTime = cursor.getLong(cursor.getColumnIndex("conversationTime"));
        field_isSend = cursor.getInt(cursor.getColumnIndex("isSend"));
        field_status = cursor.getInt(cursor.getColumnIndex("status"));
        field_flag = cursor.getLong(cursor.getColumnIndex("flag"));
        field_attrflag = cursor.getInt(cursor.getColumnIndex("attrflag"));
        field_atCount = cursor.getInt(cursor.getColumnIndex("atCount"));
        field_unReadMuteCount = cursor.getInt(cursor.getColumnIndex("unReadMuteCount"));
        field_UnReadInvite = cursor.getInt(cursor.getColumnIndex("UnReadInvite"));
        field_unReadCount = cursor.getInt(cursor.getColumnIndex("unReadCount"));
        this.loadPackageParam = loadPackageParam;
        this.ctx = AndroidAppHelper.currentApplication().getApplicationContext();

        try {
            adapterClass = XposedHelpers.findClass("com.tencent.mm.ui.conversation.h", loadPackageParam.classLoader);
            beanClass = (Class) ((ParameterizedType)adapterClass.getGenericSuperclass()).getActualTypeArguments()[1];
            beanConstructor = beanClass.getConstructor(new Class[]{String.class});
        } catch (Exception e) {

        }

    }

    public CharSequence getNickname() {
        if (field_nickname == null || field_nickname.equals("")) {
            return "群聊";
        } else {
            return field_nickname;
        }
    }

    public CharSequence getContent() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
        CharSequence result = field_content;
        Object bean = beanConstructor.newInstance(field_username);

        beanClass.getField("field_editingMsg").set(bean, field_editingMsg);
        beanClass.getField("field_atCount").set(bean, field_atCount);
        beanClass.getField("field_unReadCount").set(bean, field_unReadCount);
        beanClass.getField("field_unReadMuteCount").set(bean, field_unReadMuteCount);
        beanClass.getField("field_msgType").set(bean, field_msgType);
        beanClass.getField("field_username").set(bean, field_username);
        beanClass.getField("field_content").set(bean, field_content);
        beanClass.getField("field_digest").set(bean, field_digest);
        beanClass.getField("field_digestUser").set(bean, field_digestUser);
        beanClass.getField("field_isSend").set(bean, field_isSend);
        beanClass.getField("field_UnReadInvite").set(bean, field_UnReadInvite);
        beanClass.getField("field_atCount").set(bean, field_atCount);

        int textSize = (int)(AndroidAppHelper.currentApplication().getResources().getDisplayMetrics().density * 13);
        result = (CharSequence) XposedHelpers.callMethod(Entry.conversationAdapter, "b", new Class[]{beanClass, int.class, boolean.class}, bean, textSize, true);
        return result;
    }

    public CharSequence getConversationTime() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
        CharSequence result = "";
        Object obj = beanConstructor.newInstance("");
        beanClass.getField("field_status").set(obj, 0);
        beanClass.getField("field_conversationTime").set(obj, field_conversationTime);
        result = (CharSequence) XposedHelpers.callMethod(Entry.conversationAdapter, "h", new Class[]{beanClass}, obj);
        return result;
    }

    public int getUnReadMuteCount() {
        return field_unReadMuteCount;
    }

    public int getUnReadCount() {
        return field_unReadCount;
    }

    public long getBackgroundFlag() throws InstantiationException, IllegalAccessException, InvocationTargetException{
        long result = 0;
        Class clazz = XposedHelpers.findClass("com.tencent.mm.plugin.messenger.foundation.a.a.a", this.loadPackageParam.classLoader);
        Object obj = beanConstructor.newInstance("");
        result = (long)XposedHelpers.callStaticMethod(clazz, "a", new Class[]{beanClass, int.class, long.class}, obj, 4, 0);
        return result;
    }

    public void setConversationAvatar(ImageView imageView) {
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.a$b", this.loadPackageParam.classLoader), "a", new Class[]{ImageView.class, String.class}, imageView, this.field_username);
    }

    public XC_LoadPackage.LoadPackageParam getLoadPackageParam() {
        return this.loadPackageParam;
    }

    public Context getContext() {
        return this.ctx;
    }

    public String getUsername() {
        return field_username.toString();
    }
}

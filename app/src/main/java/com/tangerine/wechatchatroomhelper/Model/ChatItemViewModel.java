package com.tangerine.wechatchatroomhelper.Model;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 24481 on 2018/12/21.
 * 定义了一个聊天的Item，通过遍历子节点获取所有可显示的View，分别进行设置
 */

public class ChatItemViewModel {


    private ViewGroup itemView;
    private Context ctx;
    private XC_LoadPackage.LoadPackageParam lpparam;

    private ViewGroup avatarContainer;
    private ViewGroup textContainer;

    private ImageView avatar;
    private TextView unReadCount;
    private ImageView unMuteReadIndicators;
    private View nickname;
    private View time;
    private View sendStatus;
    private View content;
    private View muteImage;
    private ImageView unknownImageView1110;
    private ImageView talkroomMicIdle;
    private ImageView locationShareIcon;
    private ChatInfoModel chatInfoModel;

    public ChatItemViewModel(Context ctx, XC_LoadPackage.LoadPackageParam loadPackageParam, ViewGroup v) {
        this.ctx = ctx;
        this.lpparam = loadPackageParam;

        if (v == null) {
            this.itemView = (ViewGroup) View.inflate(ctx, (int) 0x7f040261, null);
        } else {
            this.itemView = v;
        }

        avatarContainer = (ViewGroup) itemView.getChildAt(0);

        avatar = (ImageView) avatarContainer.getChildAt(0);
        unReadCount = (TextView) avatarContainer.getChildAt(1);
        unMuteReadIndicators = (ImageView) avatarContainer.getChildAt(2);

        textContainer = (ViewGroup) itemView.getChildAt(1);

        nickname = ((ViewGroup) ((ViewGroup) (textContainer.getChildAt(0))).getChildAt(0)).getChildAt(0);

        time = ((ViewGroup) (textContainer.getChildAt(0))).getChildAt(1);

        sendStatus = ((ViewGroup) ((ViewGroup) (textContainer.getChildAt(1))).getChildAt(0)).getChildAt(0);
        content = ((ViewGroup) (((ViewGroup) (textContainer.getChildAt(1))).getChildAt(0))).getChildAt(1);

        unknownImageView1110 = (ImageView) ((ViewGroup) (((ViewGroup) (textContainer.getChildAt(1))).getChildAt(1))).getChildAt(0);
        muteImage = ((ViewGroup) (((ViewGroup) (textContainer.getChildAt(1))).getChildAt(1))).getChildAt(1);
        talkroomMicIdle = (ImageView) ((ViewGroup) (((ViewGroup) (textContainer.getChildAt(1))).getChildAt(1))).getChildAt(2);
        locationShareIcon = (ImageView) ((ViewGroup) (((ViewGroup) (textContainer.getChildAt(1))).getChildAt(1))).getChildAt(3);

        unReadCount.setVisibility(View.GONE);
        unMuteReadIndicators.setVisibility(View.GONE);
        sendStatus.setVisibility(View.GONE);
        muteImage.setVisibility(View.GONE);
        talkroomMicIdle.setVisibility(View.GONE);
        locationShareIcon.setVisibility(View.GONE);

        //布局默认为正常，因此字体大小比值默认设置成1*，根据程序的设定设置显示颜色，对显示时间进行右对齐处理
        Class clazz = XposedHelpers.findClass("com.tencent.mm.cb.a", loadPackageParam.classLoader);

        XposedHelpers.callMethod(content, "setTextSize", new Class[]{int.class, float.class}, 0, AndroidAppHelper.currentApplication().getApplicationContext().getResources().getDimensionPixelSize((int) 2131427841));
        ColorStateList contentColor = (ColorStateList) XposedHelpers.callStaticMethod(clazz, "h", new Class[]{Context.class, int.class}, this.ctx, 2131690069);
        XposedHelpers.callMethod(content, "setTextColor", new Class[]{ColorStateList.class}, contentColor);

        XposedHelpers.callMethod(time, "setTextSize", new Class[]{int.class, float.class}, 0, AndroidAppHelper.currentApplication().getApplicationContext().getResources().getDimensionPixelSize((int) 2131427843));
        ColorStateList timeColor = (ColorStateList) XposedHelpers.callStaticMethod(clazz, "h", new Class[]{Context.class, int.class}, this.ctx, 2131690109);
        XposedHelpers.callMethod(time, "setTextColor", new Class[]{ColorStateList.class}, timeColor);
        XposedHelpers.callMethod(time, "setGravity", new Class[]{int.class}, 5);

        XposedHelpers.callMethod(nickname, "setTextSize", new Class[]{int.class, float.class}, 0, AndroidAppHelper.currentApplication().getApplicationContext().getResources().getDimensionPixelSize((int) 2131427791));
        ColorStateList nicknameColor = (ColorStateList) XposedHelpers.callStaticMethod(clazz, "h", new Class[]{Context.class, int.class}, this.ctx, 2131690212);
        XposedHelpers.callMethod(nickname, "setTextColor", new Class[]{ColorStateList.class}, nicknameColor);

    }

    public Object getViewHolder() throws InstantiationException, IllegalAccessException {
        Class holderClass = XposedHelpers.findClass("com.tencent.mm.ui.conversation.h$f", lpparam.classLoader);
        Object viewHolder = holderClass.newInstance();

        XposedHelpers.setObjectField(viewHolder, "doU", avatar);
        XposedHelpers.setObjectField(viewHolder, "hZk", unReadCount);
        XposedHelpers.setObjectField(viewHolder, "vQz", locationShareIcon);
        XposedHelpers.setObjectField(viewHolder, "vSF", sendStatus);
        XposedHelpers.setObjectField(viewHolder, "vSG", unknownImageView1110);
        XposedHelpers.setObjectField(viewHolder, "veJ", nickname);
        XposedHelpers.setObjectField(viewHolder, "veK", time);
        XposedHelpers.setObjectField(viewHolder, "veL", content);
        XposedHelpers.setObjectField(viewHolder, "veM", muteImage);
        XposedHelpers.setObjectField(viewHolder, "veN", talkroomMicIdle);
        XposedHelpers.setObjectField(viewHolder, "veO", unMuteReadIndicators);
        return viewHolder;
    }

    public View getItem() {
        return this.itemView;
    }

    public void setRedPoint(int Type) {
        unMuteReadIndicators.setVisibility(Type);
    }

    public void setAvatar() {
        //TODO 设置显示图标
        //this.avatar.setImageResource(R.drawable.default_hd_avatar);
    }

    public void setNickname(CharSequence c) {
        XposedHelpers.callMethod(nickname, "setText", new Class[]{CharSequence.class}, c);
    }

    public void setTime(CharSequence c) {
        XposedHelpers.callMethod(time, "setText", new Class[]{CharSequence.class}, c);
    }


    public void setContent(CharSequence c) {
        XposedHelpers.callMethod(content, "setText", new Class[]{CharSequence.class}, c);
    }


    public void initByChatInfoModel(ChatInfoModel chatInfo) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        this.chatInfoModel = chatInfo;
        chatInfo.setConversationAvatar(this.avatar);
        if (chatInfo.getUnReadCount() > 0) {
            this.setRedPoint(View.VISIBLE);
        }
        setNickname(chatInfo.getNickname());
        setTime(chatInfo.getConversationTime());
        setContent(chatInfo.getContent());


    }
}

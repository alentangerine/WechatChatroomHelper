package com.tangerine.wechatchatroomhelper.Adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.tangerine.wechatchatroomhelper.Entry;
import com.tangerine.wechatchatroomhelper.Model.ChatInfoModel;
import com.tangerine.wechatchatroomhelper.Model.ChatItemViewModel;

import java.util.ArrayList;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by 24481 on 2018/12/21.
 * 自定义的Adapter，用于显示群助手页面
 */

public class ChatRoomAdapter extends BaseAdapter{


    ArrayList<ChatInfoModel> mData = new ArrayList<>();

    public void addData(ChatInfoModel c) {
        mData.add(c);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View result = null;
        ChatItemViewModel itemViewModel;
        ChatInfoModel chatInfoModel = mData.get(position);

        try {
            if (convertView == null) {
                itemViewModel = new ChatItemViewModel(chatInfoModel.getContext(), chatInfoModel.getLoadPackageParam(), null);
                itemViewModel.initByChatInfoModel(chatInfoModel);
                result = itemViewModel.getItem();
                result.setTag(itemViewModel.getViewHolder());
            } else {
                result = convertView;
                itemViewModel = new ChatItemViewModel(chatInfoModel.getContext(), chatInfoModel.getLoadPackageParam(), (ViewGroup) result);
                itemViewModel.initByChatInfoModel(chatInfoModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public class mOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String username = mData.get(position).getUsername();
            Entry.BizUIInstance.finish();
            XposedHelpers.callMethod(Entry.LaunchUIInstance, "startChatting", new Class[]{String.class, Bundle.class, boolean.class}, username, null, true);
        }
    }

    public class mOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(mData.get(position).getContext(), "移出群助手！", Toast.LENGTH_SHORT).show();
            Entry.hideList.remove(mData.get(position).getUsername());
            Entry.chatroomAdapter.notifyDataSetChanged();
            return true;
        }
    }
}

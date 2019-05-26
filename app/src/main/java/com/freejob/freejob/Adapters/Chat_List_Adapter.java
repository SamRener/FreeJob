package com.freejob.freejob.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freejob.freejob.Items.Chat;
import com.freejob.freejob.R;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class Chat_List_Adapter extends BaseAdapter{
    List<Chat> chats = new ArrayList<>();
    Context ctx;

    public Chat_List_Adapter(List<Chat> chats, Context ctx) {
        this.chats = chats;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            rowView = LayoutInflater.from(ctx).inflate(R.layout.chat_list_item, null);
            ImageView profile = rowView.findViewById(R.id.CLT_profile);

            AutofitTextView name = rowView.findViewById(R.id.CLT_name);
            name.setText(chats.get(position).getClient().getName());
            TextView lastMessage = rowView.findViewById(R.id.CLT_lastMessage);
            TextView LM_timestamp = rowView.findViewById(R.id.CLT_LM_timestamp);
            int cont = 1;

           while (chats.get(position).getMessages().size() >= cont){
                if(chats.get(position).getMessages().get(cont -1).getIsNew())
                {
                    lastMessage.setText(chats.get(position).getMessages().get(cont -1).getMessage());
                    rowView.findViewById(R.id.CLT_newMarker).setVisibility(View.VISIBLE);
                   
                }
                else lastMessage.setText(chats.get(position).getMessages().get(cont -1).getMessage());
                cont++;
            }
        }
        return rowView;
    }
}

package com.freejob.freejob.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freejob.freejob.Items.Message;
import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Message_List_Adapter extends BaseAdapter {

    List<Message> messages;
    Context ctx;
    User sender;

    public Message_List_Adapter(List<Message> messages, Context ctx, User sender) {
        this.messages = messages;
        this.ctx = ctx;
        this.sender = sender;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){

            rowView = LayoutInflater.from(ctx).inflate(R.layout.message_list_item, null);
            LinearLayout layout = rowView.findViewById(R.id.MSI_layout);

            if(!messages.get(position).getSender().getUuid()
                    .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 1.0f;
                params.gravity = Gravity.LEFT;

                layout.setLayoutParams(params);
            }
            else{
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 1.0f;
                params.gravity = Gravity.RIGHT;

                layout.setLayoutParams(params);
            }

            TextView message = rowView.findViewById(R.id.MSI_message);
            message.setText(messages.get(position).getMessage());

            TextView timestamp = rowView.findViewById(R.id.MSI_timestamp);
//            timestamp.setText(messages.get(position).getTimestamp().getDate());
        }
        return rowView;
    }
}

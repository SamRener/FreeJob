package com.freejob.freejob.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freejob.freejob.Activities.ChatActivity;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.Address;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class Schedule_List_Adapter extends BaseAdapter {

    private Context ctx;
    private List<Request> requests = new ArrayList<>();
    private DatabaseReference reference;

    public Schedule_List_Adapter(Context context, List<Request> requests) {
        this.ctx = context;
        this.requests = requests;
        reference = new CommonMethods().getRef(context);
    }


    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(ctx).inflate(R.layout.sch_request_list_item, null);
            ImageView client_image = rowView.findViewById(R.id.srli_client_image);
            AutofitTextView client_name = rowView.findViewById(R.id.srli_service_client_name);
            TextView service_date = rowView.findViewById(R.id.srli_request_date);
            TextView service_value = rowView.findViewById(R.id.srli_request_price);
            AutofitTextView service_local = rowView.findViewById(R.id.srli_location);
            ImageButton chat = rowView.findViewById(R.id.srli_client_chat);
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(requests.get(position).getClient().getUuid()))
                    {
                        intent.putExtra("uid",requests.get(position).getClient().getUuid());
                        ctx.startActivity(intent);
                    }
                    else{
                        intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ctx.startActivity(intent);
                    }

                }
            });
            service_date.setText(requests.get(position).getData());
            if (requests.get(position).getValue() != null)
                service_value.setText("R$ " + String.valueOf(requests.get(position).getValue()));
            else service_value.setText("Ainda não definido");
            setLocation(service_local, requests.get(position).getAddress());
            client_name.setText(requests.get(position).getClient().getName()+" "+requests.get(position).getClient().getMiddlename());
        }
        return rowView;
    }

    private void setLocation(AutofitTextView service_local, Address address) {
        service_local.setText(address.getLogradouro()+", "+address.getNumero()+" - "+address.getBairro()+" - "+address.getCidade()+" - "+address.getEstado());
    }


}

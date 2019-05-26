package com.freejob.freejob.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freejob.freejob.Activities.ChatActivity;
import com.freejob.freejob.Activities.ClientActivity;
import com.freejob.freejob.Activities.PaymentActivity;
import com.freejob.freejob.Classes.CommonMethods;
import com.freejob.freejob.Items.Request;
import com.freejob.freejob.R;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class Request_List_Adapter extends BaseAdapter {

    private Context ctx;
    private List<Request> requests = new ArrayList<>();
    private DatabaseReference reference;

    public Request_List_Adapter(Context context, List<Request> requests) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            rowView = LayoutInflater.from(ctx).inflate(R.layout.request_list_item, null);
            ImageView service_image = rowView.findViewById(R.id.rli_service_image);
            AutofitTextView service_worker_name = rowView.findViewById(R.id.rli_service_worker_name);
            TextView service_date = rowView.findViewById(R.id.rli_request_date);
            TextView service_value = rowView.findViewById(R.id.rli_request_price);
            AutofitTextView service_status = rowView.findViewById(R.id.rli_status);
            LinearLayout status_layout = rowView.findViewById(R.id.rli_status_layout);

            service_date.setText(requests.get(position).getData());
            if(requests.get(position).getValue() != null) service_value.setText("R$ "+String.valueOf(requests.get(position).getValue()));
            else service_value.setText("Ainda não definido");
            setStatus(service_status, status_layout, requests.get(position));

            switch (requests.get(position).getType()){
                case "Costureiro":
                    service_image.setImageResource(R.drawable.costura);
                    break;
                case "Pedreiro":
                    service_image.setImageResource(R.drawable.constructor);
                    break;
                case "Freteiro":
                    service_image.setImageResource(R.drawable.transporter);
                    break;
                case "Motorista":
                    service_image.setImageResource(R.drawable.driver);
                    break;
                case "Diarista":
                    service_image.setImageResource(R.drawable.maid);
                    break;
                case "Encanador":
                    service_image.setImageResource(R.drawable.plumber);
                    break;
                case "Jardineiro":
                    service_image.setImageResource(R.drawable.gardner);
                    break;
                case "Pintor":
                    service_image.setImageResource(R.drawable.painter);
                    break;
            }
            if(requests.get(position).getWorker() != null)
            service_worker_name.setText(requests.get(position).getType()+": "+requests.get(position).getWorker().getName()+" "+requests.get(position).getWorker().getMiddlename());
            else service_worker_name.setText(requests.get(position).getType());
        }
        return rowView;
    }

    private void setStatus(AutofitTextView service_status, LinearLayout status_layout, final Request request) {
        if(request.getWorker() == null){
            if(request.getPossibleWorkers().size() > 0)
            {
                service_status.setText("Status Atual: Aguardando seleção de Trabalhadores");
                status_layout.setClickable(true);
                status_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ctx.startActivity(new Intent(ctx, ClientActivity.class));
                    }
                });
            }
            else{
                service_status.setText("Status Atual: Aguardando Trabalhadores...");
                status_layout.setClickable(true);
                status_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ctx, "Estamos passando seu pedido para os trabalhadores! Aguarde...", Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
        if(request.isAccepted()) {
            service_status.setText("Status Atual: Em Andamento");
            status_layout.setClickable(true);
            status_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    intent.putExtra("uid",request.getWorker().getUuid());
                    ctx.startActivity(intent);
                }
            });
        }
        if(request.isPayed()){
            service_status.setText("Status Atual: Aguardando Pagamento...");
            status_layout.setClickable(true);
            status_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, PaymentActivity.class);

                    Gson gson = new Gson();
                    String json = gson.toJson(request);

                    intent.putExtra("REQUEST", json);
                    ctx.startActivity(intent);
                }
            });
        }
        if(request.isFinished()) service_status.setText("Status Atual: Finalizado");
    }
}

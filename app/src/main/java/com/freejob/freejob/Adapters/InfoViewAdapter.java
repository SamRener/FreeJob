package com.freejob.freejob.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.freejob.freejob.Items.User;
import com.freejob.freejob.R;
import com.freejob.freejob.Transforms.CircleTransform;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import me.grantland.widget.AutofitTextView;

public class InfoViewAdapter implements GoogleMap.InfoWindowAdapter {

    Context ctx;
    View view;
    public User user;
    public InfoViewAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        view = LayoutInflater.from(ctx).inflate(R.layout.info_view_layout, null);
        ImageView image = view.findViewById(R.id.IV_image);
        Picasso.get()
                .load(user.getUri())
                .transform(new CircleTransform())
                .into(image);
        AutofitTextView name = view.findViewById(R.id.IV_name);
        name.setText(user.getName()+" "+user.getMiddlename());

        AutofitTextView work_type = view.findViewById(R.id.IV_work_type);
        work_type.setText(marker.getSnippet());

        AutofitTextView rating = view.findViewById(R.id.IV_rating);
        switch (Math.round(Float.parseFloat(user.getRating()))){
            case 1: rating.setText("Incapaz: "+user.getRating()+"☆"); break;
            case 2: rating.setText("Amador: "+user.getRating()+"☆"); break;
            case 3: rating.setText("Aceitável: "+user.getRating()+"☆"); break;
            case 4: rating.setText("Semi-Pro: "+user.getRating()+"☆"); break;
            case 5: rating.setText("Profissional: "+user.getRating()+"☆"); break;
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

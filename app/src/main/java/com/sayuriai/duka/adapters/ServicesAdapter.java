package com.sayuriai.duka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sayuriai.duka.R;
import com.sayuriai.duka.models.Service;

import java.util.List;
import java.util.Objects;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>{

    private Context mContext;
    private List<Service> services ;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ServicesAdapter() {
    }

    public ServicesAdapter(Context mContext, List<Service> services) {
        this.mContext = mContext;
        this.services = services;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.all_services_layout, parent, false );
        mAuth = FirebaseAuth.getInstance ();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ServicesAdapter.ServiceViewHolder holder, int position) {
        holder.name.setText(services.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder{

        CardView card;
        TextView name;
        ServiceViewHolder(View itemView){
            super(itemView);

            name  = itemView.findViewById(R.id.service_name);
            card  = itemView.findViewById(R.id.card);

        }
    }
}

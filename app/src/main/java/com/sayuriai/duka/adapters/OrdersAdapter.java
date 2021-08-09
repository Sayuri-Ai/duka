package com.sayuriai.duka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.sayuriai.duka.R;
import com.sayuriai.duka.models.Item;
import com.sayuriai.duka.models.Order;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class OrdersAdapter  extends RecyclerView.Adapter<OrdersAdapter.OrderViewModel>{

    private Context mContext;
    private List<Order> orders ;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public OrdersAdapter() {
    }

    public OrdersAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @NonNull
    @NotNull
    @Override
    public OrdersAdapter.OrderViewModel onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.all_orders_layout, parent, false );
        mAuth = FirebaseAuth.getInstance ();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        return new OrdersAdapter.OrderViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrdersAdapter.OrderViewModel holder, int position) {
        holder.title.setText(orders.get(position).getItem().getName());
        holder.cost.setText(orders.get(position).getItem().getName());
        holder.desc.setText(orders.get(position).getItem().getDescription());
        Glide.with(mContext)
                .load(orders.get(position).getItem().getImage())
                .placeholder(R.drawable.sampleitem)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }



    static class OrderViewModel extends RecyclerView.ViewHolder{


        TextView title, cost, desc;
        ImageView image;
        OrderViewModel(View itemView){
            super(itemView);
            title  = itemView.findViewById(R.id.title);
            cost = itemView.findViewById(R.id.cost);
            image = itemView.findViewById(R.id.image);
            desc  = itemView.findViewById(R.id.desc);
        }
    }
}

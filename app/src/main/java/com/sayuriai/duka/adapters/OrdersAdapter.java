package com.sayuriai.duka.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private int custom_layout;
    private DatabaseReference ordersRef;


    public OrdersAdapter() {
    }

    public OrdersAdapter(Context mContext, List<Order> orders, int custom_layout) {
        this.mContext = mContext;
        this.orders = orders;
        this.custom_layout = custom_layout;
    }

    @NonNull
    @NotNull
    @Override
    public OrdersAdapter.OrderViewModel onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( custom_layout, parent, false );
        mAuth = FirebaseAuth.getInstance ();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        return new OrdersAdapter.OrderViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrdersAdapter.OrderViewModel holder, int position) {
        if(custom_layout != R.layout.all_received_orders_layout){
            holder.confirm_delivery.setVisibility(View.INVISIBLE);
        }else{
            holder.confirm_delivery.setVisibility(View.VISIBLE);
            holder.confirm_delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderId = orders.get(position).getId();
                    ordersRef.child(orderId).child("status").setValue("COMPLETED").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Order Delivered Successfully!", Toast.LENGTH_SHORT).show();
                                String delivered = "DELIVERED";
                                holder.confirm_delivery.setText(delivered);
                            }
                        }
                    });
                }
            });
        }
        holder.date.setText(orders.get(position).getDate());
        holder.title.setText(orders.get(position).getItem().getName());
        holder.cost.setText(orders.get(position).getItem().getCost());
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


        TextView title, cost, desc,date;
        ImageView image;
        Button confirm_delivery;
        OrderViewModel(View itemView){
            super(itemView);
            title  = itemView.findViewById(R.id.title);
            cost = itemView.findViewById(R.id.cost);
            image = itemView.findViewById(R.id.image);
            desc  = itemView.findViewById(R.id.desc);
            confirm_delivery = itemView.findViewById(R.id.confirm_delivery);
            date = itemView.findViewById(R.id.date);
        }
    }
}

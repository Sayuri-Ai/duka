package com.sayuriai.duka.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.sayuriai.duka.R;
import com.sayuriai.duka.models.Item;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewModel>{

    private Context mContext;
    private List<Item> items ;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public ItemsAdapter() {
    }

    public ItemsAdapter(Context mContext, List<Item> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public ItemViewModel onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.all_items_layout, parent, false );
        mAuth = FirebaseAuth.getInstance ();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        return new ItemsAdapter.ItemViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemsAdapter.ItemViewModel holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.cost.setText(items.get(position).getCost());
        Glide.with(mContext)
                .load(items.get(position).getImage())
                .placeholder(R.drawable.sampleitem)
                .into(holder.image);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card.setChecked(!holder.card.isChecked());
                Bundle bundle = new Bundle();
                bundle.putString("ITEM_ID", items.get(position).getId());
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_item,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    static class ItemViewModel extends RecyclerView.ViewHolder{

        MaterialCardView card;
        TextView name, cost;
        ImageView image;
        ItemViewModel(View itemView){
            super(itemView);
            name  = itemView.findViewById(R.id.product_name);
            cost = itemView.findViewById(R.id.product_cost);
            image = itemView.findViewById(R.id.product_image);
            card  = itemView.findViewById(R.id.card);
        }
    }
}


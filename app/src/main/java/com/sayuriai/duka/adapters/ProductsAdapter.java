package com.sayuriai.duka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.sayuriai.duka.R;
import com.sayuriai.duka.models.Product;
import com.sayuriai.duka.models.Service;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    private Context mContext;
    private List<Product> products ;
    private FirebaseAuth mAuth;
    private String currentUserID;
    Set<Product> chosenProducts = new HashSet<Product>();

    public ProductsAdapter() {
    }

    public ProductsAdapter(Context mContext, List<Product> products) {
        this.mContext = mContext;
        this.products = products;
    }

    @NonNull
    @NotNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext () ).inflate ( R.layout.all_products_layout, parent, false );
        mAuth = FirebaseAuth.getInstance ();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        return new ProductsAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductsAdapter.ProductViewHolder holder, int position) {
        holder.name.setText(products.get(position).getName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card.setChecked(!holder.card.isChecked());
                if(holder.card.isChecked()){
                    chosenProducts.add(products.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Set<Product> getChosenProducts() {
        return chosenProducts;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView card;
        TextView name;
        ProductViewHolder(View itemView){
            super(itemView);
            name  = itemView.findViewById(R.id.product_name);
            card  = itemView.findViewById(R.id.card);
        }
    }
}

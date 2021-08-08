package com.sayuriai.duka.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sayuriai.duka.R;
import com.sayuriai.duka.adapters.ProductsAdapter;
import com.sayuriai.duka.models.Product;
import com.sayuriai.duka.models.Service;
import com.sayuriai.duka.utils.ProductsList;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Product> productsList = new ArrayList<>();
    private DatabaseReference productsRef, usersRef;
    private FirebaseAuth mAuth;
    private  String currentUserID;

    public ChooseProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseProductFragment newInstance(String param1, String param2) {
        ChooseProductFragment fragment = new ChooseProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_product, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        assert currentUserID != null;
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        Button nextButton = view.findViewById(R.id.next);
        RecyclerView recyclerView = view.findViewById(R.id.product_list);

        Activity activity = getActivity();
        if(productsList.size() == 0){
            productsList = new ProductsList().getproductsList();
        }
        ProductsAdapter productsAdapter = new ProductsAdapter(getContext(), productsList);

        recyclerView.setHasFixedSize ( true );
        GridLayoutManager layoutManager = new GridLayoutManager ( getContext(), 2,GridLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager ( layoutManager );
        recyclerView.setAdapter(productsAdapter);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Save the data to the database and transition to home page
                Set<Product> chosenProducts = productsAdapter.getChosenProducts();
                for(Product product: chosenProducts){
                    String product_category = product.getName();
                    usersRef.child("Catalog").child("Products").child(product_category).setValue(null);
                }
                //  Transition to services page
                Navigation.findNavController(view).navigate(R.id.action_nav_choose_product_to_nav_choose_service);
            }
        });
    }
}
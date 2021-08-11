package com.sayuriai.duka.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sayuriai.duka.R;
import com.sayuriai.duka.models.Item;
import com.sayuriai.duka.models.Order;
import com.sayuriai.duka.models.User;
import com.sayuriai.duka.utils.Status;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView item_name, item_desc, fullname, address, phone, item_cost;
    private ImageView item_image;
    private Button place_order;
    private DatabaseReference itemsRef, usersRef, catalogRef, ordersRef;
    private FirebaseAuth mAuth;
    private String currentUserID, ITEM_ID, sellerUserID;
    private Order order;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(String param1, String param2) {
        CheckoutFragment fragment = new CheckoutFragment();
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
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        ITEM_ID = getArguments().getString("ITEM_ID");
        sellerUserID = getArguments().getString("SELLER_ID");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        assert currentUserID != null;
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        catalogRef =  FirebaseDatabase.getInstance().getReference().child("Catalog");
        ordersRef =  FirebaseDatabase.getInstance().getReference().child("Orders");
        item_name = view.findViewById(R.id.item_name);
        item_cost = view.findViewById(R.id.item_cost);
        item_desc = view.findViewById(R.id.item_desc);
        item_image = view.findViewById(R.id.item_image);
        fullname = view.findViewById(R.id.fullname);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        place_order = view.findViewById(R.id.place_order);
        order = new Order();

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(view);
            }
        });

        usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String buyer_fullname = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                    String buyer_phone = Objects.requireNonNull(snapshot.child("phonenumber").getValue()).toString();
                    String buyer_address = Objects.requireNonNull(snapshot.child("address").getValue()).toString();
                    fullname.setText(buyer_fullname);
                    address.setText(buyer_address);
                    phone.setText(buyer_phone);
                    User buyer = new User(buyer_fullname,buyer_phone,currentUserID,buyer_address);
                    order.setBuyer(buyer);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        catalogRef.child(ITEM_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String cost = Objects.requireNonNull(snapshot.child("cost").getValue()).toString();
                    String image = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    String desc = Objects.requireNonNull(snapshot.child("description").getValue()).toString();
                    String uid = Objects.requireNonNull(snapshot.child("uid").getValue()).toString();
                    item_name.setText(name);
                    item_cost.setText(cost);
                    item_desc.setText(desc);
                    Glide.with(requireContext())
                            .load(image)
                            .placeholder(R.drawable.sampleitem)
                            .into(item_image);
                    Item item = new Item(ITEM_ID);
                    item.setName(name);
                    item.setCost(cost);
                    item.setDescription(desc);
                    item.setImage(image);
                    order.setItem(item);
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String seller_fullname = Objects.requireNonNull(snapshot.child("fullname").getValue()).toString();
                                String seller_phone = Objects.requireNonNull(snapshot.child("phonenumber").getValue()).toString();
                                User seller = new User(seller_fullname,seller_phone,uid);
                                order.setSeller(seller);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                }else{
                    Toast.makeText(requireContext(),"No item with that id",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void createOrder(View view){

        order.setStatus("UPCOMING");
        Calendar calFordDate = Calendar.getInstance ();
        SimpleDateFormat currentDate = new SimpleDateFormat ("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format (calFordDate.getTime ());
        order.setDate(saveCurrentDate);

        String order_id = ordersRef.push().getKey();
        order.setId(order_id);
        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("buyer", order.getBuyer());
        ordersMap.put("seller", order.getSeller());
        ordersMap.put("key", order.getId());
        ordersMap.put("date", order.getDate());
        ordersMap.put("item", order.getItem());
        ordersMap.put("status", order.getStatus());
        assert order_id != null;
        ordersRef.child(order_id).updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(), "Order created successfully", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_nav_checkout_to_nav_orders);
                }
            }
        });



    }
}
package com.sayuriai.duka.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sayuriai.duka.R;
import com.sayuriai.duka.adapters.OrdersAdapter;
import com.sayuriai.duka.models.Item;
import com.sayuriai.duka.models.Order;
import com.sayuriai.duka.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpcomingOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingOrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Order> upcomingOrdersList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private OrdersAdapter ordersAdapter;
    private Activity activity;
    private LinearLayout no_order;
    private RecyclerView recyclerView;


    public UpcomingOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpcomingOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingOrdersFragment newInstance(String param1, String param2) {
        UpcomingOrdersFragment fragment = new UpcomingOrdersFragment();
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
        return inflater.inflate(R.layout.fragment_upcoming_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        assert currentUserID != null;

        ordersRef =  FirebaseDatabase.getInstance().getReference().child("Orders");

        if(upcomingOrdersList.size() == 0 ){
            getOrdersList();
        }
        recyclerView = view.findViewById(R.id.upcoming_orders_list);
        ordersAdapter = new OrdersAdapter(getContext(), upcomingOrdersList, R.layout.all_orders_layout);
        no_order = view.findViewById(R.id.no_order_layout);

        activity = getActivity();

        recyclerView.setHasFixedSize ( true );
        GridLayoutManager layoutManager = new GridLayoutManager ( getContext(), 1,GridLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager ( layoutManager );
        recyclerView.setAdapter(ordersAdapter);
    }
    private void getOrdersList() {

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> orders = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : orders.entrySet()) {
                        Map singleOrder = (Map) entry.getValue();

                        HashMap<String, Object> buyer = (HashMap<String, Object>) Objects.requireNonNull(singleOrder.get("buyer"));
                        HashMap<String, Object> seller = (HashMap<String, Object>) Objects.requireNonNull(singleOrder.get("seller"));
                        HashMap<String, Object> item = (HashMap<String, Object>) Objects.requireNonNull(singleOrder.get("item"));
                        if(Objects.requireNonNull(buyer.get("uid")).toString().equals(currentUserID)) {
                            String order_id = Objects.requireNonNull(singleOrder.get("key")).toString();
                            String status = Objects.requireNonNull(singleOrder.get("status")).toString();
                            String date = Objects.requireNonNull(singleOrder.get("date")).toString();

                            Order order = new Order();
                            order.setId(order_id);
                            order.setDate(date);
                            User buyer_ = new User(Objects.requireNonNull(buyer.get("uid")).toString());
                            User seller_ = new User(Objects.requireNonNull(seller.get("uid")).toString());
                            order.setBuyer(buyer_);
                            order.setSeller(seller_);

                            Item item_ = new Item(Objects.requireNonNull(item.get("id")).toString());
                            item_.setImage(Objects.requireNonNull(item.get("image")).toString());
                            item_.setName(Objects.requireNonNull(item.get("name")).toString());
                            item_.setDescription(Objects.requireNonNull(item.get("description")).toString());
                            item_.setCost(Objects.requireNonNull(item.get("cost")).toString());

                            order.setItem(item_);

                            if (status.toUpperCase().equals("UPCOMING")) {
                                upcomingOrdersList.add(order);
                            }
                        }

                    }
                    if(upcomingOrdersList.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        no_order.setVisibility(View.VISIBLE);
                        showLongToast("No completed Orders yet");
                    }else {
                        ordersAdapter.notifyDataSetChanged();
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    no_order.setVisibility(View.VISIBLE);
                    showLongToast("No Orders added yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        hideKeyboard(activity);

    }
    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showShortToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
    private void showLongToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }
}
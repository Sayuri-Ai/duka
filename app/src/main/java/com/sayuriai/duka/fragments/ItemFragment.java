package com.sayuriai.duka.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sayuriai.duka.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView item_name, item_desc, item_cost;
    private ImageView  item_image;
    private TextInputEditText address;
    private Button add_to_cart;
    private  String ITEM_ID;
    private DatabaseReference itemsRef, usersRef, catalogRef;
    private FirebaseAuth mAuth;
    private String currentUserID, sellerUserID;
    private Activity activity;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
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
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        assert getArguments() != null;
        ITEM_ID = getArguments().getString("ITEM_ID");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();
        assert currentUserID != null;
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        catalogRef =  FirebaseDatabase.getInstance().getReference().child("Catalog");
        item_name = view.findViewById(R.id.product_name);
        item_cost = view.findViewById(R.id.product_cost);
        item_desc = view.findViewById(R.id.product_description);
        item_image = view.findViewById(R.id.product_image);
        address = view.findViewById(R.id.address);
        add_to_cart = view.findViewById(R.id.add_to_cart);

        usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("address")){
                        String add = Objects.requireNonNull(snapshot.child("address").getValue()).toString();
                        address.setText(add);
                    }
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
                    sellerUserID = Objects.requireNonNull(snapshot.child("uid").getValue()).toString();
                    item_name.setText(name);
                    item_cost.setText(cost);
                    item_desc.setText(desc);
                    Glide.with(requireContext())
                            .load(image)
                            .placeholder(R.drawable.sampleitem)
                            .into(item_image);
                }else{
                    Toast.makeText(requireContext(),"No item with that id",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sellerUserID.equals(currentUserID)){
                    Toast.makeText(requireContext(), "Can't purchase your items", Toast.LENGTH_LONG).show();
                }else {
                    String ad = address.getText().toString();
                    if (TextUtils.isEmpty(ad)) {
                        Toast.makeText(requireContext(), "Please type your delivery or pickup  address", Toast.LENGTH_SHORT).show();
                    } else {
                        usersRef.child(currentUserID).child("address").setValue(ad).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ITEM_ID", ITEM_ID);
                                    bundle.putString("SELLER_ID", sellerUserID);
                                    Navigation.findNavController(view).navigate(R.id.action_nav_item_to_nav_checkout, bundle);
                                }
                            }
                        });

                    }
                }
            }
        });

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
    @Override
    public void onStart() {
        super.onStart();

        hideKeyboard(activity);

    }

    private void sendToWhatsApp(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    public void composeEmail(String[] addresses, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity((requireActivity()).getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
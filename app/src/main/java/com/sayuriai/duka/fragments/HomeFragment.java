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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sayuriai.duka.R;
import com.sayuriai.duka.activities.MainActivity;
import com.sayuriai.duka.adapters.ItemsAdapter;
import com.sayuriai.duka.models.Item;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Activity activity;
    private List<Item> catalogList = new ArrayList<>();
    private DatabaseReference itemsRef, usersRef, catalogRef;
    private ItemsAdapter itemsAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        mAuth = FirebaseAuth.getInstance();
        activity = getActivity();
        FirebaseUser currentUser = mAuth.getCurrentUser ();
        usersRef = FirebaseDatabase.getInstance ().getReference ().child ( "Users" );

        if (currentUser == null) {
            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_login);
        }else{
            currentUserID = currentUser.getUid();
            usersRef = usersRef.child(currentUserID);
            ((MainActivity) requireActivity()).updateUserUI(currentUser);

            catalogRef =  FirebaseDatabase.getInstance().getReference().child("Catalog");

            RecyclerView recyclerView = view.findViewById(R.id.catalog_list);


            if(catalogList.size() == 0 ){
                getCatalogList();
            }

            itemsAdapter = new ItemsAdapter(getContext(), catalogList);

            recyclerView.setHasFixedSize ( true );
            GridLayoutManager layoutManager = new GridLayoutManager ( getContext(), 2,GridLayoutManager.VERTICAL, false );
            recyclerView.setLayoutManager ( layoutManager );
            recyclerView.setAdapter(itemsAdapter);
        }
    }
    private void getCatalogList() {

        catalogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Map<String, Object> users = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : users.entrySet()) {
                        Map singleItem = (Map) entry.getValue();

                        String product_id = Objects.requireNonNull(singleItem.get("key")).toString();
                        String product_name =Objects.requireNonNull(singleItem.get("name")).toString();
                        String product_cost = Objects.requireNonNull(singleItem.get("cost")).toString();
                        String product_image = Objects.requireNonNull(singleItem.get("image")).toString();
                        Item item = new Item(product_id);
                        item.setName(product_name);
                        item.setCost(product_cost);
                        item.setImage(product_image);
                        catalogList.add(item);
                    }
                    itemsAdapter.notifyDataSetChanged();
                } else {
                    showLongToast("No Items added yet");
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
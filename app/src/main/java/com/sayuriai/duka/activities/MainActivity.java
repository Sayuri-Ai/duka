package com.sayuriai.duka.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Application;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sayuriai.duka.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MaterialToolbar materialToolbar;
    private CircleImageView NavProfileImage;
    private TextView NavProfileFullName,NavProfileEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference profileUserRef;
    private AppBarConfiguration mAppBarConfiguration;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialToolbar = findViewById(R.id.topAppBar);
        navigationView = findViewById ( R.id.navigation_view );
        drawerLayout = findViewById ( R.id.drawable_layout );
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_orders, R.id.nav_healthy, R.id.nav_quotes)
                .setDrawerLayout(drawerLayout)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        View navView = navigationView.inflateHeaderView ( R.layout.navigation_view_header_layout );
        NavProfileImage = navView.findViewById ( R.id.nav_profile_image );
        NavProfileFullName =  navView.findViewById ( R.id.nav_user_full_name );
        NavProfileEmail =  navView.findViewById ( R.id.nav_user_email );


    }

    @Override
    protected void onStart() {
        FirebaseApp.initializeApp(getApplicationContext());
        super.onStart();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.nav_home:
//                Navigation.findNavController(view).navigate(R.id.action_nav_login_to_nav_home);
//                drawerLayout.close();
//                //close drawer
//                return true;
//            case R.id.nav_profile:
//                drawerLayout.close();
//                    return true;
//            case R.id.nav_catalog:
//                drawerLayout.close();
//                return true;
//            case R.id.nav_settings:
//                drawerLayout.close();
//                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateUserUI(FirebaseUser firebaseUser){
        if(firebaseUser == null){
            String dummyName = "Joe Doe";
            String dummyEmail = "example@gmail.com";
            NavProfileFullName.setText(dummyName);
            NavProfileEmail.setText(dummyEmail);
        }else {

            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            currentUserID = firebaseUser.getUid();
            profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
            profileUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        //Dummy data to initialize the variables

                        String myProfileName = "profile name";
                        String myEmail = "email";

                        if (dataSnapshot.hasChild("profileimage")) {
                            String myProfileImage = Objects.requireNonNull(dataSnapshot.child("profileimage").getValue()).toString();
                            Glide.with(getApplicationContext())
                                    .load(myProfileImage)
                                    .placeholder(R.drawable.avatar)
                                    .into(NavProfileImage);
                        }
                        if (dataSnapshot.hasChild("firstname")) {
                            myProfileName = Objects.requireNonNull(dataSnapshot.child("firstname").getValue()).toString();
                        }
                        if (dataSnapshot.hasChild("surname")) {
                            myProfileName += " " + Objects.requireNonNull(dataSnapshot.child("surname").getValue()).toString();
                        }
                        if (dataSnapshot.hasChild("email")) {
                            myEmail = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                        }

                        // Picasso.get ().load(myProfileImage).placeholder(R.drawable.profilenew).noFade().fit().into(NavProfileImage);
                        NavProfileFullName.setText(myProfileName);
                        NavProfileEmail.setText(myEmail);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
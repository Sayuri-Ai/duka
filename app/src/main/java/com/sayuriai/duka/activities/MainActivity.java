package com.sayuriai.duka.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private String currentUserID;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //YocoSDK.initialise(this);
        materialToolbar = findViewById(R.id.topAppBar);
        navigationView = findViewById ( R.id.navigation_view );
        drawerLayout = findViewById ( R.id.drawable_layout );
        setSupportActionBar(materialToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled ( true );
        getSupportActionBar ().setDisplayShowHomeEnabled(true);
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
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled ( true );
            bottomNavigation = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigation, navController);
        }

        View navView = navigationView.inflateHeaderView ( R.layout.navigation_view_header_layout );
        NavProfileImage = navView.findViewById ( R.id.nav_profile_image );
        NavProfileFullName =  navView.findViewById ( R.id.nav_user_full_name );
        NavProfileEmail =  navView.findViewById ( R.id.nav_user_email );

        //Handle visibility of the application bottom navigation
        assert navController != null;
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId()== R.id.nav_home
                        || destination.getId()== R.id.nav_orders
                        ||destination.getId()== R.id.nav_quotes
                        || destination.getId()== R.id.nav_healthy

                ){
                    bottomNavigation.setVisibility(View.VISIBLE);
                }
                else{
                    bottomNavigation.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        FirebaseApp.initializeApp(getApplicationContext());
        super.onStart();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

        return false;

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController  navController = navHostFragment.getNavController();
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
                                    .placeholder(R.drawable.profile)
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
    /**
     *  Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return  true;
    }

    /**
     * Setup the main menu bar
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
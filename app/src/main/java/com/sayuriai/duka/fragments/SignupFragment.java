package com.sayuriai.duka.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sayuriai.duka.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    TextInputEditText mName, mSurname, mEmail, mPhone, mPassword, mConfirmPassword;
    Button next;
    private DatabaseReference usersRef;
    TextView login;

    private ProgressDialog loadingBar;
    private ProgressBar progressBar;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        mAuth = FirebaseAuth.getInstance();
        mName = view.findViewById(R.id.firstname);
        mSurname = view.findViewById(R.id.surname);
        mEmail =  view.findViewById(R.id.email);
        mPhone =  view.findViewById(R.id.phone);
        mPassword =  view.findViewById(R.id.password);
        mConfirmPassword =  view.findViewById(R.id.confirm_password);
        next =  view.findViewById(R.id.next);
        usersRef = FirebaseDatabase.getInstance ().getReference ().child ("Users");
        login = view.findViewById(R.id.login);
        loadingBar = new ProgressDialog(requireContext());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_signup_to_nav_login);
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserToDatabase(v);
            }
        });



    }

    private void saveUserToDatabase(View view){

        String email = Objects.requireNonNull(mEmail.getText()).toString().trim().toLowerCase(Locale.US);
        String password = Objects.requireNonNull(mPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(mConfirmPassword.getText()).toString().trim();

        String name = Objects.requireNonNull(mName.getText()).toString().trim();
        String surname = Objects.requireNonNull(mSurname.getText()).toString().trim();
        String phone = Objects.requireNonNull(mPhone.getText()).toString().trim();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(requireContext(), "Please type your email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(requireContext(), "Please type your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(surname)){
            Toast.makeText(requireContext(), "Please type your surname", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(requireContext(), "Please type your phonenumber", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(requireContext(), "Please type your password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(requireContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(requireContext(), "Password and confirm password does not match", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            String date = currentDate.format(calFordDate.getTime());
            String fullname = name + " " + surname;
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("firstname", name);
            userMap.put("surname", surname);
            userMap.put("fullname", fullname);
            userMap.put("email", email);
            userMap.put("phonenumber", phone);
            userMap.put("date",date);

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are create your account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            createAccount(email,password,userMap,view);
        }


    }
    private void createAccount(String email, String password, HashMap<String, Object> userMap, View view) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                            sendEmailVerification();
                            Log.d(TAG, "createUserWithEmail:success");
                            System.out.println("SUCCESS");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String currentUserID = user.getUid();
                            usersRef.child(currentUserID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        //proceed to the next fragment to get OTP
                                        Navigation.findNavController(view).navigate(R.id.action_nav_signup_to_nav_confirmation_code);

                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(requireContext(), "Error occurred while saving data: " + message, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingBar.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            System.out.println("FAILURE");
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(requireContext(), "Verification email sent",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
}
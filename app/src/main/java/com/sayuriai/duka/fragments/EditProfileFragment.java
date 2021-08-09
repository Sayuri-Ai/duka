package com.sayuriai.duka.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sayuriai.duka.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Activity activity;
    private TextInputEditText firstnameEditText,surnameEditText, titleEditText,emailEditText,phoneEditText;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference UserRef;
    private CircleImageView profileimage;
    private Button save;
    private StorageReference UserPostImageRef;
    private final static int Gallery_Pick = 1;
    private  String profile = null;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        currentUserID = user.getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firstnameEditText = view.findViewById(R.id.firstNameEditText);
        surnameEditText = view.findViewById(R.id.surnameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        profileimage =  view.findViewById(R.id.my_profile_pic);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        UserPostImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        activity = getActivity();
        save = view.findViewById(R.id.save_button);
        populateDatabase();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase(v);
            }
        });
    }

    private void populateDatabase(){
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String title="";
                    String firstname = dataSnapshot.child("firstname").getValue().toString();
                    String surname = dataSnapshot.child("surname").getValue().toString();
                    String phone = dataSnapshot.child("phonenumber").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    if(dataSnapshot.hasChild("profileimage")) {
                        String profile = dataSnapshot.child("profileimage").getValue().toString();
                        Glide.with(requireActivity())
                                .load(profile)
                                .placeholder(R.drawable.profile)
                                .into(profileimage);
                    }

                    if(dataSnapshot.hasChild("title")){
                        title= dataSnapshot.child("title").getValue().toString();
                        titleEditText.setText(title);
                    }
                    firstnameEditText.setText(firstname);
                    surnameEditText.setText(surname);
                    phoneEditText.setText(phone);
                    emailEditText.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveToDatabase(View view){

        String firstname = firstnameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String title = titleEditText.getText().toString();
        if(TextUtils.isEmpty(firstname)){
            showShortToast("Please type the first name");
        }else if(TextUtils.isEmpty(surname)){
            showShortToast("Please type your surname");
        }else if(TextUtils.isEmpty(phone)){
            showShortToast("Please type your phone number");
        }else if(TextUtils.isEmpty(email)){
            showShortToast("Please type your email address");
        }else if(TextUtils.isEmpty(title)){
            showShortToast("Please indicate your title");
        }else{
            if(profile!=null){
                UserRef.child(currentUserID).child("profileimage").setValue(profile);
            }
            UserRef.child(currentUserID).child("firstname").setValue(firstname);
            UserRef.child(currentUserID).child("surname").setValue(surname);
            UserRef.child(currentUserID).child("phonenumber").setValue(phone);
            UserRef.child(currentUserID).child("email").setValue(email);
            UserRef.child(currentUserID).child("title").setValue(title);

            hideKeyboard(activity);
            Navigation.findNavController(view).navigate(R.id.action_nav_edit_profile_to_nav_profile);
        }

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
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }
    /**
     * Start intent to pick image from Gallery
     */
    private void openGallery(){
        Intent galleryIntent = new Intent ();
        galleryIntent.setAction (Intent.ACTION_GET_CONTENT);
        galleryIntent.setType ("image/*");
        startActivityForResult (galleryIntent, Gallery_Pick);
    }

    /**
     * Handle the request codes for managing images
     * Start cropping activity for pre-acquired image saved on the device.
     * The acquired image is from the GALLERY-REQUEST_CODE
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            Intent intent = CropImage.activity(imageUri)
                    .setGuidelines (CropImageView.Guidelines.ON)
                    .setAspectRatio (1, 1)
                    .getIntent(requireContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult (data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri ();
                profileimage.setImageURI(resultUri);
                StorageReference filePath = UserPostImageRef.child (currentUserID+ ".jpg");
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading profile image");
                progressDialog.show();
                filePath.putFile (resultUri)
                        .addOnCompleteListener (new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful ()) {
                                    progressDialog.dismiss();
                                    Toast.makeText (getContext(), "Upload successful", Toast.LENGTH_SHORT).show ();
                                    Task<Uri> result = task.getResult ().getMetadata ().getReference ().getDownloadUrl ();
                                    result.addOnSuccessListener (
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    final String path = uri.toString ();
                                                    profile = path;
                                                }
                                            });

                                }else{
                                    Toast.makeText (getContext(), "Upload failure", Toast.LENGTH_SHORT).show ();
                                    progressDialog.dismiss();
                                }
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded "+ progress+"%");
                            }
                        });

            } else {
                Toast.makeText (getContext(), "Error occurred: Image can not be cropped. Try Again...", Toast.LENGTH_SHORT).show ();
            }

        }
    }
}
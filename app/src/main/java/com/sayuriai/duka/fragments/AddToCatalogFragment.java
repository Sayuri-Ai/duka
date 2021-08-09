package com.sayuriai.duka.fragments;

import android.annotation.SuppressLint;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sayuriai.duka.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToCatalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToCatalogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText item_name, item_cost, item_desc ;
    private Button mButtonSave;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, itemsRef;
    private  String currentUserID,image_url;
    private CircleImageView itemImage;
    private Activity activity;
    private String category;
    private StorageReference itemImageRef;
    private final static int Gallery_Pick = 1;
    private String saveCurrentDate, saveCurrentTime, postRandomName;

    public AddToCatalogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddToCatalogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddToCatalogFragment newInstance(String param1, String param2) {
        AddToCatalogFragment fragment = new AddToCatalogFragment();
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
        return inflater.inflate(R.layout.fragment_add_to_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        itemImage = view.findViewById(R.id.item_image);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        item_name = view.findViewById ( R.id.item_name);
        item_cost = view.findViewById ( R.id.item_cost);
        item_desc = view.findViewById ( R.id.item_desc);
        itemImage = view.findViewById ( R.id.item_image);

        mButtonSave = view.findViewById(R.id.save_button);
        UsersRef = FirebaseDatabase.getInstance ().getReference ().child ("Users");
        itemsRef = FirebaseDatabase.getInstance ().getReference ().child ("Catalog");
        itemImageRef = FirebaseStorage.getInstance().getReference().child("Catalog Images");

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

      mButtonSave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveDataToDatabase(v);
        }
    });
    RadioGroup rb = (RadioGroup) view.findViewById(R.id.radio);
        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_products:
                    category = "Products";
                    break;
                case R.id.radio_services:
                    category ="Services";
                    break;
            }
        }

    });

}
    private void saveDataToDatabase(View view) {

        String name = Objects.requireNonNull(item_name.getText()).toString().trim();
        String cost = Objects.requireNonNull(item_cost.getText()).toString().trim();
        String desc = Objects.requireNonNull(item_desc.getText()).toString().trim();

        if (TextUtils.isEmpty(cost)) {
            Toast.makeText(getContext(), "Please type item cost", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Please type the item name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(desc)) {
            Toast.makeText(getContext(), "Please type the item description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(getContext(), "please choose category", Toast.LENGTH_SHORT).show();
        }
        else {
            hideKeyboard(activity);
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            String date = currentDate.format(calFordDate.getTime());

            final FirebaseUser user = mAuth.getCurrentUser();
            assert user != null;
            currentUserID = user.getUid();
            HashMap<String, Object> itemMap = new HashMap<>();
            HashMap<String, Object> userMap = new HashMap<>();
            String key = itemsRef.push().getKey();
            assert key != null;
            itemMap.put("name",name);
            itemMap.put("key",key);
            itemMap.put("date", date);
            itemMap.put("image", image_url);
            itemMap.put("category", category);
            itemMap.put("cost", cost);
            itemMap.put("description", desc);
            itemMap.put("uid", currentUserID);


            itemsRef.child(key).updateChildren(itemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        String key_user =  UsersRef.child(currentUserID).child("Catalog").push().getKey();
                        assert key_user != null;
                        UsersRef.child(currentUserID).child("Catalog").child( key_user).updateChildren(itemMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //navigate
                                    Navigation.findNavController(view).navigate(R.id.action_nav_add_to_catalog_to_nav_catalog);
                                }else{
                                    Toast.makeText(requireContext(), "Error occurred while updating user map", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(requireContext(), "Error occurred while updating catalog map", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
        Calendar calFordDate = Calendar.getInstance ();
        SimpleDateFormat currentDate = new SimpleDateFormat ("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format (calFordDate.getTime ());
        Calendar calFordTime = Calendar.getInstance ();
        SimpleDateFormat currentTime = new SimpleDateFormat ("HH:mm");
        saveCurrentTime = currentTime.format (calFordTime.getTime ());
        postRandomName = saveCurrentDate + saveCurrentTime;
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            Intent intent = CropImage.activity(imageUri)
                    .setGuidelines (CropImageView.Guidelines.ON)
                    .getIntent(requireContext());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult (data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri ();
                itemImage.setImageURI(resultUri);
                StorageReference filePath = itemImageRef.child (resultUri.getLastPathSegment ()+ postRandomName+ ".jpg");
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading item image");
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
                                                    image_url = uri.toString ();
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
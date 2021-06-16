package com.example.practisedoneed.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.practisedoneed.R;
import com.example.practisedoneed.homePage;
//import com.example.practisedoneed.databinding.FragmentNotificationsBinding;
import com.example.practisedoneed.test;
import com.example.practisedoneed.ui.notifications.NotificationsViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class donateFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int RESULT_OK = -1;
    private Toolbar toolbar;
    private TextView donateTitle;
    private EditText postTitle;
    private EditText description;
    private EditText quantity;
    private Button chooseBtn;
    Uri imageUrl;
    String myUrl = "";
    ImageView image_add;
    Spinner categoriesSpinner;
    Spinner stateSpinner;
    String[] stateArray = {"State","Johor","Kedah","Kelantan","Melaka","Negeri Sembilan","Pahang","Penang","Perak","Perlis"
            ,"Sabah","Sarawak","Selangor","Terengganu","Kuala Lumpur","Labuan","Putrajaya"};
    String[] categoryArray = {"Categories","Home Equipment","Furniture","Computer","Smartphone","Technology","Cloth","Sport"};
    String category;
    String state;
    StorageTask uploadTask;
    StorageReference storageReference;
    String extension;
    private Context context;

//    private static final int GalleryPick = 1;
//    private static final int CAMERA_REQUEST = 100;
//    private static final int STORAGE_REQUEST = 200;
//    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
//    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
//    String cameraPermission[];
//    String storagePermission[];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate,container,false);
        setHasOptionsMenu(true);
        postTitle = view.findViewById(R.id.Tittle);
        description = view.findViewById(R.id.Description);
        quantity = view.findViewById(R.id.Quantity);
        toolbar = (Toolbar) view.findViewById(R.id.app_toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        donateTitle = toolbar.findViewById(R.id.text_Donate);
        donateTitle.setVisibility(View.VISIBLE);


        image_add = view.findViewById(R.id.image_add);

        categoriesSpinner = view.findViewById(R.id.categories_spinner);
        categoriesSpinner.setOnItemSelectedListener(this);
        ArrayAdapter ad1 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryArray);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(ad1);

        stateSpinner = view.findViewById(R.id.state_spinner);
        stateSpinner.setOnItemSelectedListener(this);
        ArrayAdapter ad2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, stateArray);
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(ad2);

        chooseBtn = view.findViewById(R.id.choose_image_btn);
        chooseBtn.setOnClickListener(this);
//        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        context = container.getContext();

        return view;
    }

    @Override
    public void onClick(View j) {
        if(j.getId()==R.id.choose_image_btn){
            pickFromGallery();
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().setAspectRatio(5,4).start(requireContext(), this);
    }

    public static String getMimeType(Activity context, Uri uri) {
        String extension;
        //Check uri format to avoid null
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            //If scheme is a content
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            if (TextUtils.isEmpty(extension)) {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file
            // name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            if (TextUtils.isEmpty(extension)) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(uri));
            }
        }
//        if (TextUtils.isEmpty(extension)) {
//            extension = getMimeTypeByFileName(TUriParse.getFileWithUri(uri, context).getName());
//        }
        return extension;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrl = result.getUri();
                Picasso.get().load(imageUrl).into(image_add);
            }else {
                Toast.makeText(getActivity(),"Something gone wrong!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(),"Something gone wrong2!",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.show();
        if(imageUrl != null){

            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "."+ getMimeType(getActivity(), imageUrl));
//            getFileExtensions(imageUrl)
            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){

                        throw  task.getException();

                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();

                        myUrl = downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("id",postid);
                        hashMap.put("image",myUrl);
                        hashMap.put("title", postTitle.getText().toString());
                        hashMap.put("description",description.getText().toString());
                        hashMap.put("quantity",quantity.getText().toString());
                        hashMap.put("location", state);
                        hashMap.put("category", category);
                        hashMap.put("donator", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        progressDialog.dismiss();

                    }
                    else {
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            Toast.makeText(getActivity(), "no Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.donate_toolbar, menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();

            return true;
        }else if(item.getItemId() == R.id.donate_now){
            uploadImage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.categories_spinner){
            category = categoryArray[position];
        }else if(parent.getId()==R.id.state_spinner){
            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            state = stateArray[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
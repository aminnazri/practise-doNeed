package com.example.practisedoneed.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class donateFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int RESULT_OK = -1;
    private Toolbar toolbar;
    private TextView donateTitle;
    private Button chooseBtn;
    Uri imageUrl;
    ImageView close, image_added,image_add;
    String[] stateArray = {"State","Johor","Kedah","Kelantan","Melaka","Negeri Sembilan","Pahang","Penang","Perak","Perlis"
            ,"Sabah","Sarawak","Selangor","Terengganu","Kuala Lumpur","Labuan","Putrajaya"};
    String[] categoryArray = {"Categories","Home Equipment","Furniture","Computer","Smartphone","Technology","Cloth","Sport"};


    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String cameraPermission[];
    String storagePermission[];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate,container,false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) view.findViewById(R.id.app_toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        donateTitle = toolbar.findViewById(R.id.text_Donate);
        donateTitle.setVisibility(View.VISIBLE);


        image_add = view.findViewById(R.id.image_add);

//        image_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//
//                intent.setType("image/*");
//
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(intent, "Open Gallery"), 1);
//
//            }
//        });

        Spinner categoriesSpinner = view.findViewById(R.id.categories_spinner);
        categoriesSpinner.setOnItemSelectedListener(this);
        ArrayAdapter ad1 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, categoryArray);
        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesSpinner.setAdapter(ad1);


        Spinner stateSpinner = view.findViewById(R.id.state_spinner);
        stateSpinner.setOnItemSelectedListener(this);
        ArrayAdapter ad2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, stateArray);
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(ad2);

        chooseBtn = view.findViewById(R.id.choose_image_btn);
        chooseBtn.setOnClickListener(this);
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.choose_image_btn){
            pickFromGallery();
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUrl = result.getUri();
                Picasso.get().load(imageUrl).into(image_add);
            }else {
                Toast.makeText(getActivity(),"Searching gone wrong!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(),"gone wrong!",Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtensions(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

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
            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            Toast.makeText(getActivity(), categoryArray[position], Toast.LENGTH_SHORT).show();
        }else if(parent.getId()==R.id.state_spinner){
            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
            Toast.makeText(getActivity(), stateArray[position], Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
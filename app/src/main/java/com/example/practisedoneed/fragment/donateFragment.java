package com.example.practisedoneed.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.practisedoneed.R;
import com.example.practisedoneed.homePage;
//import com.example.practisedoneed.databinding.FragmentNotificationsBinding;
import com.example.practisedoneed.ui.notifications.NotificationsViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class donateFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private Toolbar toolbar;
    private TextView donateTitle;
    Uri imageUrl;
    ImageView close, image_added,image_add;


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

        image_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
//
//// start cropping activity for pre-acquired image saved on the device
//                CropImage.activity(imageUri)
//                        .start(this);
//
//// for fragment (DO NOT use `getActivity()`)
//                CropImage.activity()
//                        .start(getContext(),this);

            }
        });
        CropImage.activity()
                .start(getContext(),this);
        return view;

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.e("resultUri ->", String.valueOf(resultUri));
//                AddImage1.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("error ->", String.valueOf(error));
            }
        }


//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            imageUrl = result.getUri();
//
//            image_added.setImageURI(imageUrl);
//        }
//        else {
//            Toast.makeText(PostActivity.this,"Searching gone wrong!",Toast.LENGTH_SHORT).show();
//
//            startActivity(new Intent(PostActivity.this, MainActivity.class));
//            finish();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
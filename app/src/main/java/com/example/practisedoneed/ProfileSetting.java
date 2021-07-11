package com.example.practisedoneed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.practisedoneed.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.r0adkll.slidr.Slidr;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

//profile setting class
public class ProfileSetting extends AppCompatActivity implements View.OnClickListener {

    private Uri imageUrl;
    private ImageView profile_image, add_image, back;
    private EditText username, email, phone, address, bio;
    private MaterialButton saveBtn;
    private Toolbar toolbar;
    private FirebaseUser firebaseUser;
    private String userID;
    private DatabaseReference reference;
    private Boolean checkImage;
    String userImage;

    HashMap<String, Object> hashMap;

    private StorageReference storageRef;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        Slidr.attach(this);
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        profile_image = findViewById(R.id.image_profile);
        add_image = findViewById(R.id.img_plus2);
        add_image.setOnClickListener(this);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        bio = findViewById(R.id.bio);
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = firebaseUser.getUid();

        storageRef = FirebaseStorage.getInstance().getReference("users");

        hashMap = new HashMap<>();
        checkImage = false;
        userProfile();

    }


    //onClick function
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            onBackPressed();
        } else if (v.getId() == R.id.img_plus2) {
            CropImage.activity().start(this);
        } else if (v.getId() == R.id.save) {
            saveProfile();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //GET AN IMAGE CHOSE BY USER FOR THEIR PROFILE PICTURE
            //SET THE IMAGE IN IMAGEVIEW IN PROFILE SETTING
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUrl = result.getUri();
            Glide.with(this).load(imageUrl).into(profile_image);
            checkImage = true;
        } else {
            Toast.makeText(ProfileSetting.this, "Searching gone wrong! Try again", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(ProfileSetting.this, MainActivity.class));
//            finish();
        }
    }


    //GET IMAGE EXTENSION FUNCTION
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


    //GET USER PROFILE FROM FIREBASE DATABASE FUNCTION
    private void userProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                userImage = user.getImageUrl();
                if(userImage!=null){
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                }
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                email.setFocusable(false);
                phone.setText(user.getPhone());
                address.setText(user.getAddress());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    //SAVE THE UPDATED USER PROFILE
    private void saveProfile() {
        ProgressDialog pd = new ProgressDialog(ProfileSetting.this);
        pd.setMessage("Please wait...");
        pd.show();
        String str_username = username.getText().toString();
        String str_email = email.getText().toString();
        String str_address = address.getText().toString();
        String str_phoneNumber = phone.getText().toString();
        String str_bio = bio.getText().toString();

        if (str_username.isEmpty() || str_email.isEmpty() || str_address.isEmpty() || str_phoneNumber.isEmpty()) {
            pd.dismiss();
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            //Check if user choose a new profile picture
            if (checkImage && imageUrl != null) {
                StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "."
                        + getMimeType(this,imageUrl));
                uploadTask = fileRef.putFile(imageUrl);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull @NotNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw  task.getException();
                        }
                        return fileRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if(task.isSuccessful()) {

                            Uri downloadUrl = task.getResult();

                            userImage = downloadUrl.toString();
                            pd.dismiss();
                            uploadData(str_username,str_address,str_phoneNumber,str_email,str_bio);
                        }
                    }
                });
            }else{
                pd.dismiss();
                uploadData(str_username,str_address,str_phoneNumber,str_email,str_bio);
            }

        }
    }

    //UPLOAD USERDATA FUNCTION
    //UPLOAD THE UPDATED USER PROFILE TO DATABASE
    private void uploadData(String str_username,String str_address,String str_phoneNumber,String str_email,String str_bio){
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        hashMap.put("id", userID);
        hashMap.put("username", str_username);
        hashMap.put("address", str_address);
        hashMap.put("phone", str_phoneNumber);
        hashMap.put("email", str_email);
        hashMap.put("imageUrl", userImage);
        hashMap.put("bio", str_bio);

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileSetting.this, "Profile update successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProfileSetting.this, "Profile update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
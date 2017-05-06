package com.example.praveen.lab4;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "UploadActivity";
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth = FirebaseAuth.getInstance();
        Button mChoose = (Button) findViewById(R.id.choose_button);
        Button mUpload = (Button) findViewById(R.id.upload_button);

        findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
        findViewById(R.id.private_switch).setVisibility(View.INVISIBLE);
        findViewById(R.id.upload_button).setVisibility(View.INVISIBLE);

        mChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });
    }

    public void choosePhoto() {
        Intent photoPickerIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    findViewById(R.id.upload_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageView).setVisibility(View.VISIBLE);

                    //display private switch only to authenticated users
                    if (mAuth.getCurrentUser() != null) {
                        findViewById(R.id.private_switch).setVisibility(View.VISIBLE);
                    }

                    ImageView mImageView = (ImageView) findViewById(R.id.imageView);
                    Button mUpload = (Button) findViewById(R.id.upload_button);

                    //display image
                    mImageView.setImageURI(selectedImage);





                } catch (Exception e) {
                    Toast.makeText(this, "Enable External Storage Permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void uploadPhoto() {

        //check if selected image is not blank
        if (selectedImage != null && !selectedImage.equals(Uri.EMPTY)) {

            //get description
            EditText photoDescription = (EditText) findViewById(R.id.editText);
            String description = photoDescription.getText().toString();

            //set default Privacy Setting to Public
            Switch privateSwitch = (Switch) findViewById(R.id.private_switch);
            String privacyState="";

            String imagePath="";

            String userID = "";
            if(mAuth.getCurrentUser()!=null){
                userID = mAuth.getCurrentUser().getUid();
            }

            if(privateSwitch.isChecked()){
                privacyState="Private";
                imagePath = "private/"+userID+"/"+selectedImage.getLastPathSegment();
            }else{
                privacyState = "Public";
                imagePath = "public/"+selectedImage.getLastPathSegment();
            }

            if(!description.isEmpty()){
                // set up firebase storage object
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

                // storage reference of firebase
                StorageReference storageRef = firebaseStorage.getReference();

                // Create a child reference
                StorageReference pathRef = storageRef.child(imagePath);

                StorageMetadata imageMetadata = new StorageMetadata.Builder()
                        .setCustomMetadata("UserID", userID)
                        .setCustomMetadata("PrivacySetting", privacyState)
                        .setCustomMetadata("Description", description)
                        .build();

                UploadTask uploadTask = pathRef.putFile(selectedImage, imageMetadata);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        // Handle unsuccessful uploads
                        Log.e(TAG, "Upload Failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "Upload Successful");
                        ((Switch) findViewById(R.id.private_switch)).setChecked(false);
                        ((ImageView) findViewById(R.id.imageView)).setImageResource(0);
                        ((EditText) findViewById(R.id.editText)).setText("");
                        goToPhotoViewer();
                    }
                });

            }

        }else{
            Toast.makeText(this, "Choose an Image to Upload", Toast.LENGTH_LONG).show();
        }

    }

    public void goToPhotoViewer(){
        Intent intent = new Intent(this, PhotoViewerActivity.class);
        startActivity(intent);
    }


}

package com.example.praveen.lab4;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.provider.MediaStore;
import android.net.Uri;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UploadActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth = FirebaseAuth.getInstance();
        //TODO check if user is autheticated
        Button mChoose = (Button) findViewById(R.id.choose_button);

        findViewById(R.id.imageView).setVisibility(View.INVISIBLE);
        findViewById(R.id.private_switch).setVisibility(View.INVISIBLE);
        findViewById(R.id.upload_button).setVisibility(View.INVISIBLE);

        mChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

    }

    public void uploadPhoto() {
        Intent photoPickerIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    ImageView mImageView = (ImageView) findViewById(R.id.imageView);

                    mImageView.setVisibility(View.VISIBLE);
                    findViewById(R.id.private_switch).setVisibility(View.VISIBLE);
                    findViewById(R.id.upload_button).setVisibility(View.VISIBLE);

                    //display image
                    mImageView.setImageURI(selectedImage);

                } catch (Exception e) {
                    Toast.makeText(this, "Enable External Storage Permissions", Toast.LENGTH_LONG).show();
                }
            }
    }


}

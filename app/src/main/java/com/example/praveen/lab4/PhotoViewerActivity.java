package com.example.praveen.lab4;

import android.os.Bundle;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.GridView;


import java.util.List;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PhotoViewerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "BrowsePhotosActivity";

    final List<String> publicPhotoLocations = new ArrayList<String>();
    final List<String> privatePhotoLocations = new ArrayList<String>();

    List<String> publicPhotoURLs = new ArrayList<String>();
    List<String> privatePhotoURLs = new ArrayList<String>();

    String[] publicImageContainer;
    String[] privateImageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_photo_viewer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.privateTextView).setVisibility(View.INVISIBLE);
        findViewById(R.id.privateGridView).setVisibility(View.INVISIBLE);

        getPhotoLocations();
        getPhotoURLs(publicPhotoLocations, publicPhotoURLs, false);
        updatePublicPhotos();
        if(mAuth.getCurrentUser()!=null){
            getPhotoURLs(privatePhotoLocations, privatePhotoURLs, false);
            updatePrivatePhotos();
        }

    }

    public void updatePublicPhotos() {
        GridView publicGridView = (GridView) findViewById(R.id.publicGridView);

        publicImageContainer = new String[publicPhotoURLs.size()];

        publicPhotoURLs.toArray(publicImageContainer);

        publicGridView.setAdapter(new ImageAdapter(PhotoViewerActivity.this, publicImageContainer));

    }

    public void updatePrivatePhotos(){

        //display private view only if user is authenticated
        if(mAuth.getCurrentUser()!= null){

            GridView privateGridView = (GridView) findViewById(R.id.privateGridView);

            privateImageContainer = new String[privatePhotoURLs.size()];

            privatePhotoURLs.toArray(privateImageContainer);

            findViewById(R.id.privateTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.privateGridView).setVisibility(View.VISIBLE);

            privateGridView.setAdapter(new ImageAdapter(PhotoViewerActivity.this, privateImageContainer));


        }
    }
    public void getLocations(DataSnapshot dataSnapshot, boolean privateFlag){
        if(privateFlag){
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                String location = (String) postSnapshot.getValue();
                privatePhotoLocations.add(location);
            }
            getPhotoURLs(privatePhotoLocations,privatePhotoURLs,true );
            updatePrivatePhotos();
            Log.i(TAG, "Successfully updated" + privateImageContainer.toString());
        }
        else{
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                String location = (String) postSnapshot.getValue();
                publicPhotoLocations.add(location);
            }
            getPhotoURLs(publicPhotoLocations, publicPhotoURLs, false);
            updatePublicPhotos();
            Log.i(TAG, "Successfully updated " + publicImageContainer.toString());
        }
    }

    public void getPhotoLocations() {

        // get the root of the database
        DatabaseReference db_root = FirebaseDatabase.getInstance().getReference();

        if(mAuth.getCurrentUser() != null) {
            String child = "private/" + mAuth.getCurrentUser().getUid();
            DatabaseReference privateDBRef = db_root.child(child);
            privateDBRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getLocations(dataSnapshot, true);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Read failed " + databaseError.getCode());
                }
            });
        }
        // add public photos
        DatabaseReference publicDBRef = db_root.child("public");
        publicDBRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getLocations(dataSnapshot, false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database Read Failed: " + databaseError.getCode());
            }
        });
    }

    public void getPhotoURLs(final List<String> photoPaths, final List<String> photoUrls, final boolean privateFlag) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        StorageReference rootDBRef = firebaseStorage.getReference();

        for (String path: photoPaths) {
            rootDBRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(!photoUrls.contains(uri.toString())) {
                        photoUrls.add(uri.toString());
                        Log.i(TAG, "Successfully added " + uri.toString());
                        if(privateFlag){
                            updatePrivatePhotos();
                        }else{
                            updatePublicPhotos();
                        }
                    }
                }
            });
        }
    }
}

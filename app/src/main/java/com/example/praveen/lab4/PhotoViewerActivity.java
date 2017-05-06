package com.example.praveen.lab4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PhotoViewerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "BrowsePhotosActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_photo_viewer);

        findViewById(R.id.privateTextView).setVisibility(View.INVISIBLE);
        findViewById(R.id.privateGridView).setVisibility(View.INVISIBLE);

        GridView publicGridview = (GridView) findViewById(R.id.publicGridView);
        publicGridview.setAdapter(new ImageAdapter(this));

        GridView privateGridView = (GridView) findViewById(R.id.privateGridView);

        publicGridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(PhotoViewerActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        //display private view only if user is authenticated
        if(mAuth.getCurrentUser()!= null){
            findViewById(R.id.privateTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.privateGridView).setVisibility(View.VISIBLE);
            privateGridView.setAdapter(new ImageAdapter(this));

            privateGridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(PhotoViewerActivity.this, "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
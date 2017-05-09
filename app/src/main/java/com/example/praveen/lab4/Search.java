package com.example.praveen.lab4;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praveen on 5/8/17.
 */

public class Search {

    List<String> mphotoLocations ;
    ArrayList<String> msearchResult;
    String msearchPhrase;


    public Search(List<String> photoLocations, String searchPhrase){
        mphotoLocations = photoLocations;
        msearchResult = new ArrayList<String>();
        msearchPhrase = searchPhrase;
    }

    public void getMetadata(){

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();

        for(final String ref: mphotoLocations){

            // Get reference to the file
            final StorageReference imageRef = storageRef.child(ref);

            imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    if (storageMetadata.getCustomMetadata("Description").contains(msearchPhrase)){
                        msearchResult.add(ref);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }
    }

    public ArrayList<String> getResult(){
        return msearchResult;
    }


}

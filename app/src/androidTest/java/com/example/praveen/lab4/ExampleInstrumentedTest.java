package com.example.praveen.lab4;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.praveen.lab4", appContext.getPackageName());
    }


    @Test
    public void checkInvalidReadFromCloudStorage() throws Exception {

        String testEmail = "test@test.com";
        String testPassword = "test1234";
        String user_id;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            if(auth.signInWithEmailAndPassword(testEmail, testPassword).isSuccessful()) {
                user_id = auth.getCurrentUser().getUid();
                auth.signOut();
            }
        } else {
            user_id = auth.getCurrentUser().getUid();
            auth.signOut();
        }

        boolean readFailed = true;
        StorageReference fileReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = fileReference.child("images/private/hJ31HCTTmDTe5ocJJhPHnxji6jB2/IMG_20170502_133734_5.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;

        try{
            fileRef.getBytes(ONE_MEGABYTE);
        }
        catch (Exception e){
            readFailed = true;
        }
        assertEquals(readFailed, true);
    }


    @Test
    public void checkInvalidWriteToCloudStorage() throws Exception {

        String testEmail = "test@test.com";
        String testPassword = "password1234";
        String user_id= "";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            if(auth.signInWithEmailAndPassword(testEmail, testPassword).isSuccessful()) {
                user_id = auth.getCurrentUser().getUid();
                auth.signOut();
            }
        }
        else{
            user_id = auth.getCurrentUser().getUid();
            auth.signOut();
        }

        boolean writeFailed = true;

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child("private/" + user_id + "/" + "test.jpg");

        byte[] byteArray;
        byteArray = "Praveen".getBytes();

        try{
            storageRef.putBytes(byteArray);
        }
        catch(Exception e){
            writeFailed = true;
        }
        assertEquals(writeFailed, true);
    }
}
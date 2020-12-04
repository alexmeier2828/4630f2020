package com.AlexMeier.regroup.profile;




import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.AlexMeier.regroup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.UserDataReader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

public class ProfileUtil {
    private static final String STORAGE_BUCKET_URL = "https://console.firebase.google.com/project/regroup-31a83/storage/regroup-31a83.appspot.com/files";
    private static final String USER_DATA = "user_data";
    private static final String TAG = "PROFILE_UTIL";
    private static final String IMAGE_FOLDER = "profile_pictures";
    private ProfileUtil(){}

    /**
     * submits a query to firestore for user profile data
     *
     * @param uid - firebase who we are searching for
     * @param onSuccessCallback - callback function that will be called if query is successfull,
     *                          provides a null argument if query returns no results
     * @param onFailureCallback - callback function called when query is unsuccessful
     */
    public static void getProfile(String uid, Consumer<ProfileData> onSuccessCallback,  Consumer<Exception> onFailureCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef =  db.collection(USER_DATA);

        //createQuery
        usersRef.document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();

                            //return the remaining copy of user data
                            onSuccessCallback.accept(new ProfileData(snapshot));

                        } else {
                            Log.d(TAG, "Error retrieving user data from firestore", task.getException());
                            onFailureCallback.accept(task.getException());
                        }
                    }
                });
    }

    /**
     * submits a query to firestore for user profile data
     * @param uid firebase who we are searching for
     * @param onSuccessCallback callback function that will be called if query is successful
     */
    public static void getProfile(String uid, Consumer<ProfileData> onSuccessCallback){
        ProfileUtil.getProfile(uid, onSuccessCallback, (String)->{});
    }

    /**
     * Updates the profile data for the currently logged in user
     * @param profileData
     */
    public static void updateCurrentUserProfile(ProfileData profileData){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        db.collection(USER_DATA).document(user.getUid()).set(profileData.getMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Successfully updated user data");
                        }else {
                            Log.e(TAG, "Failed to update user data:" + Context.getDefaultInstance());
                        }
                    }
                });

    }

    /**
     * uploads an image to firebase storage, then updates the user profile data on firestore
     * with the new image reference
     * @param imageUri
     */
    public static void updateCurrentUserProfilePicture(Uri imageUri, ProfileData oldData){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //upload image file
        StorageReference imageFolderRef = storage.getReference().child(IMAGE_FOLDER);
        StorageReference profilePictureRef = imageFolderRef.child(mAuth.getUid());
        profilePictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        profilePictureRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Updated user profile picture on cloud storage");
                    String imagePath = profilePictureRef.getPath();
                    ProfileData updatedData = new ProfileData(oldData.getUserName(), oldData.getProfileBody(), imagePath);
                    updateCurrentUserProfile(updatedData);
                }else {
                    Log.e(TAG, "Failed to upload profile picture to cloud storage: " + task.getException().toString());
                }
            }
        });
    }

    public static void getUserDict(List<String> uidList, Consumer<HashMap<String,ProfileData>> onSuccessCallback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef =  db.collection(USER_DATA);

        //createQuery
        usersRef.whereIn(FieldPath.documentId(), uidList).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            HashMap<String, ProfileData> userList = new HashMap<>();

                            for (DocumentSnapshot document:snapshot
                            ) {
                                userList.put(document.getId(),new ProfileData(document));
                            }
                            //return the remaining copy of user data
                            onSuccessCallback.accept(userList);

                        } else {
                            Log.d(TAG, "Error retrieving user data from firestore", task.getException());
                        }
                    }
                });
    }
}

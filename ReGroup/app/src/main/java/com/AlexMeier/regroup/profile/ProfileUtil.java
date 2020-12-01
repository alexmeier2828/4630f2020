package com.AlexMeier.regroup.profile;




import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.function.Consumer;

public class ProfileUtil {
    private static final String USER_DATA = "user_data";
    private static final String TAG = "PROFILE_UTIL";
    private ProfileUtil(){}

    /**
     * submits a query to firestore for user profile data
     *
     * @param user  - firebase who we are searching for
     * @param onSuccessCallback - callback function that will be called if query is successfull,
     *                          provides a null argument if query returns no results
     * @param onFailureCallback - callback function called when query is unsuccessful
     */
    public static void getProfile(FirebaseUser user, Consumer<ProfileData> onSuccessCallback,  Consumer<Exception> onFailureCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef =  db.collection(USER_DATA);

        //createQuery
        usersRef.document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
//                            if(snapshot.size() == 0){
//                                onSuccessCallback.accept(null); //return null when no matches are returned
//                            }else if(snapshot.size() == 1){
//                                //query returns one result
//                                DocumentSnapshot document = snapshot.getDocuments().get(0);
//                                ProfileData userData = new ProfileData(document);
//                                onSuccessCallback.accept(userData);
//                            }
//                            else {
//                                //duplicate users, one should be kept, and others should be deleted
//                                Queue<QueryDocumentSnapshot> duplicates = new PriorityQueue<QueryDocumentSnapshot>();
//                                for (QueryDocumentSnapshot queryDocumentSnapshot: snapshot
//                                     ) {
//                                    duplicates.add(queryDocumentSnapshot);
//                                }
//
//                                DocumentSnapshot realVersion = duplicates.remove(); //save one copy
//
//                                //delete the rest TODO:This is dangerous to have the client clean the database like this
//                                Log.w(TAG, "Detected duplicate user documents");
//                                for (QueryDocumentSnapshot queryDocumentSnapshot: duplicates
//                                     ) {
//                                    DocumentReference ref = queryDocumentSnapshot.getReference();
//                                    ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()){
//                                                Log.w(TAG, "Deleted document" + queryDocumentSnapshot.getId());
//                                            } else {
//                                                Log.e(TAG, "Failed to delete duplicate user data" + task.getException().toString());
//                                            }
//                                        }
//                                    });
//                                }

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
     * @param user firebase who we are searching for
     * @param onSuccessCallback callback function that will be called if query is successful
     */
    public static void getProfile(FirebaseUser user, Consumer<ProfileData> onSuccessCallback){
        ProfileUtil.getProfile(user, onSuccessCallback, (String)->{});
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
                            Log.e(TAG, "Failed to update user data:" + task.getException().toString());
                        }
                    }
                });

    }
}

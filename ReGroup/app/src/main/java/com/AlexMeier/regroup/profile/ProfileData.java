package com.AlexMeier.regroup.profile;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.ObjectValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * stores profile information for a given user
 */
public class ProfileData {
    private String userName;
    private String profileBody;
    private String imageReference;

    public ProfileData(String userName, String profileBody, String imageReference){
        this.userName = userName;
        this.profileBody = profileBody;
        this.imageReference = imageReference;
    }

    public ProfileData(String userName, String profileBody){
        this(userName, profileBody, null);
    }

    public ProfileData(DocumentSnapshot document) {

        userName = document.getString("userName");
        profileBody = document.getString("profileBody");
        imageReference = document.getString("imageReference");
    }
    public String getUserName() {
        return userName;
    }

    public String getProfileBody() {
        return profileBody;
    }

    public void setProfileBody(String newBody){
        this.profileBody = newBody;
    }
    /**
     * returns a firebase storage reference for the profile picture
     * @return
     */
    public StorageReference getImageReference() {
        if(imageReference != null){
            return FirebaseStorage.getInstance().getReference().child(imageReference);
        }
        else {
            return null;
        }
    }

    /**
     *
     * @return returns a map for use with firestore
     */
    public Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        map.put("profileBody", profileBody);
        map.put("imageReference", imageReference);
        return map;
    }
}

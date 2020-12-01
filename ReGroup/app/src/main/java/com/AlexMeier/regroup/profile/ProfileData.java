package com.AlexMeier.regroup.profile;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.ObjectValue;
import com.google.firestore.v1.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * stores profile information for a given user
 */
public class ProfileData {
    private String userName;
    private String profileBody;

    public ProfileData(String userName, String profileBody){
        this.userName = userName;
        this.profileBody = profileBody;
    }

    public ProfileData(DocumentSnapshot document) {

        userName = document.getString("userName");
        profileBody = document.getString("profileBody");
    }
    public String getUserName() {
        return userName;
    }

    public String getProfileBody() {
        return profileBody;
    }

    /**
     *
     * @return returns a map for use with firestore
     */
    public Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        map.put("profileBody", profileBody);
        return map;
    }
}

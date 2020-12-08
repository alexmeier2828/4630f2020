package com.AlexMeier.regroup.messaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupChatAPI {
    private static final String TAG = "GROUP_CHAT_API";
    private GroupChatAPI() {}

    /**
     * Calls join group api function
     * @param mAuth TODO:remove mAuth from arguments and get an instance of it
     * @return returns the response data containing group metadata
     */
    public static Task<GroupChatResponse> joinGroup(FirebaseAuth mAuth){
        FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();

        //newGroup function
        Map<String, Object> data = new HashMap<>();
        data.put("userID", mAuth.getUid());

        return firebaseFunctions.getHttpsCallable("joinGroup")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, GroupChatResponse>() {
                    @Override
                    public GroupChatResponse then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap resultMap = (HashMap) task.getResult().getData();
                        ArrayList<String> userList = new ArrayList();
                        int size = 0;
                        String name = "error";

                        try {

                            userList = (ArrayList<String>)resultMap.get("members");
                            name = (String)resultMap.get("name");
                            size = (int)resultMap.get("size");
                            Log.d(TAG, userList.toString());


                        } catch (Exception e){
                            Log.e(TAG, e.toString());

                        }

                        GroupChatResponse groupChatResponse = new GroupChatResponse(userList, name, size);
                        return groupChatResponse;
                    }
                });
    }

    /**
     * Calls leave group api function on firebase
     * @param groupID - a group that the user should be removed from (safe.  Will not affect groups
     *                that the user is not already a part of)
     */
    public static Task leaveGroup(String groupID){
        final String userID = FirebaseAuth.getInstance().getUid();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userID", userID);
        requestBody.put("groupID", groupID );
        Log.d(TAG, "Leaving group" + groupID);

        return FirebaseFunctions.getInstance().getHttpsCallable("leaveGroup")
                .call(requestBody);
//                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
//                    @Override
//                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
//                        Log.d(TAG, "Successfully called leaveGroup API function on group: " + groupID);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "Failed to call leaveGroup api function. " + e.toString());
//            }
//        });
    }
}

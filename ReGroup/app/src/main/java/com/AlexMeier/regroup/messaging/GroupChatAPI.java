package com.AlexMeier.regroup.messaging;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GroupChatAPI {
    private static final String TAG = "GROUP_CHAT_API";
    private GroupChatAPI() {}

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
                        String result = (String) task.getResult().getData();
                        String[] userList = {};
                        int size = 0;
                        String name = "error";

                        try {
                            JSONObject messageJson = new JSONObject(result);
                            userList = (String[])messageJson.get("members");
                            name = (String)messageJson.get("name");
                            size = (int)messageJson.get("size");
                            Log.d(TAG, userList.toString());


                        } catch (Exception e){
                            Log.e(TAG, e.toString());

                        }

                        GroupChatResponse groupChatResponse = new GroupChatResponse(userList, name, size);
                        return groupChatResponse;
                    }
                });
    }
}

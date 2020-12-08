package com.AlexMeier.regroup.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.AlexMeier.regroup.profile.ProfileData;
import com.AlexMeier.regroup.profile.ProfileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import firestream.chat.namespace.Fire;
import firestream.chat.realtime.RealtimeService;
import io.reactivex.disposables.Disposable;

/**
 * Manages sending, and recieving messages from server
 */
public abstract class GroupChatManager {
    private final static String TAG = "GROUP_CHAT_MANAGER";
    private boolean DEBUG_MODE_ON = false;                  //setting to true enables server echo
    private List<String> userList;
    private HashMap<String, ProfileData> userDict;
    private String groupID;
    private MyFirebaseMessagingService messagingService;
    private FirebaseUser user;
    private BroadcastReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GroupChatManager(Context context, GroupChatResponse groupChatResponse) {
        userList = groupChatResponse.getMembers();  //make copy of userlist
        groupID = groupChatResponse.getName();

        ProfileUtil.getUserDict(userList, stringProfileDataDictionary -> {
            userDict = stringProfileDataDictionary;
        });
        //subscribe to group

        FirebaseMessaging.getInstance().subscribeToTopic(groupID)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Log.e(TAG, "Failed to subscribe to group chat metadata service ");
                }
                else {
                    Log.i(TAG, "Subscribed to group chat metadata sercice for groupID: " + groupID);
                }
            }
        });


//        messagingService.addSubscriberCallback(new Consumer<RemoteMessage>() {
//            @Override
//            public void accept(RemoteMessage remoteMessage) {
//                updateGroup(remoteMessage);
//            }
//        });


        //firestream
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Fire.stream().initialize(context, new RealtimeService());


        Disposable d = Fire.stream().getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
            if(messageEvent.isAdded()){
                //message recieved
                String sender = "Unknown UID";
                String uid = messageEvent.get().toTextMessage().getFrom();
                if(userDict != null && userDict.containsKey(uid)){
                    sender = userDict.get(uid).getUserName();
                }
                Message message = new Message(messageEvent.get().toTextMessage().getText(), sender, false);
                onMessageReceived(message);
            }
        });

        if(!DEBUG_MODE_ON){
            if(this.userList.contains(user.getUid())) {
                this.userList.removeIf(uid -> uid.equals(user.getUid()));

            }
        }
        //update profile mapping
        ProfileUtil.getUserDict(userList, stringProfileDataDictionary -> {
            userDict = stringProfileDataDictionary;
            welcomeMessage();
        });
    }

    /**
     * Sends message to registered users in group chat
     * @param messageBody
     */
    public void sendMessage(String messageBody){
        for (String userID:userList
             ) {
            if(!DEBUG_MODE_ON && !userID.equals(user.getUid())){
                Fire.stream().sendMessageWithText(userID, messageBody).subscribe();
            }
        }
    }

    /**
     * Cleans up current group chat.  Call before leaving group activity
     */
    public Task leaveGroup(){
        //unsubscribe from group changes
        FirebaseMessaging.getInstance().unsubscribeFromTopic(groupID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Unsubscribed from group metadata update messages");
                } else {
                    Log.e(TAG, "Failed to unsubscribe from group metadata messages: " + task.getException().toString());
                }
            }
        });


        userList = new ArrayList<>();
        userDict = new HashMap<>();

        //call leaveGroup api
        return GroupChatAPI.leaveGroup(groupID);
    }

    public HashMap<String, ProfileData> getGroupMemberProfiles(){
        return userDict;
    }

    /**
     * Called when message is received from group chat;
     * @param message message with metadata
     */
    public abstract void onMessageReceived(Message message);



   /*
   Private methods
    */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateGroup(String message){

        try {
            JSONObject messageJson = new JSONObject(message);
            JSONArray jsonUserList = messageJson.getJSONObject("group").getJSONArray("members");
            ArrayList<String> newUserList = new ArrayList<>();
            for(int i = 0; i < jsonUserList.length(); i++){
                newUserList.add(jsonUserList.getString(i));
            }
            this.userList = newUserList;
            if(!DEBUG_MODE_ON){
                if(this.userList.contains(user.getUid())) {
                    this.userList.removeIf(uid -> uid.equals(user.getUid()));

                }
            }


            //update profile mapping
            ProfileUtil.getUserDict(userList, stringProfileDataDictionary -> {
                userDict = stringProfileDataDictionary;
                welcomeMessage();
            });


            Log.d(TAG, userList.toString());



        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    private void welcomeMessage(){
        if(userList.size() > 0){

            String userString = "\n";
            for (String user: userList
            ) {
                if(userDict.containsKey(user)){
                    userString += userDict.get(user).getUserName() +"\n";
                }

            }
            onMessageReceived(new Message("The following users are now in the group: " + userString, "ReGroup", false));
        } else {
            onMessageReceived(new Message("The group is empty... Stick around and someone might join!", "ReGroup", false));
        }
    }
}

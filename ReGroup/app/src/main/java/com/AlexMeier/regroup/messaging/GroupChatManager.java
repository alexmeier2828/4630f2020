package com.AlexMeier.regroup.messaging;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private ArrayList<String> userList;
    private String groupID;
    private MyFirebaseMessagingService messagingService;
    private FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GroupChatManager(Context context, GroupChatResponse groupChatResponse) {
        userList = groupChatResponse.getMembers();  //make copy of userlist
        groupID = groupChatResponse.getName();
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


        messagingService = new MyFirebaseMessagingService();
        messagingService.addSubscriberCallback(new Consumer<RemoteMessage>() {
            @Override
            public void accept(RemoteMessage remoteMessage) {
                updateGroup(remoteMessage);
            }
        });


        //firestream
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Fire.stream().initialize(context, new RealtimeService());

        Disposable d = Fire.stream().getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
            if(messageEvent.isAdded()){
                //message recieved
                Message message = new Message(messageEvent.get().toTextMessage().getText(), messageEvent.get().toTextMessage().getFrom(), false);
                onMessageReceived(message);
            }
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
     * Called when message is received from group chat;
     * @param message message with metadata
     */
    public abstract void onMessageReceived(Message message);


   /*
   Private methods
    */

    private void updateGroup(RemoteMessage message){
        String messageString = message.getData().toString();

        try {
            JSONObject messageJson = new JSONObject(messageString);
            this.userList = (ArrayList<String>) messageJson.get("members");
            if(!DEBUG_MODE_ON){
                if(this.userList.contains(user.getUid())) {
                    this.userList.remove(user.getUid());
                }
            }
            Log.d(TAG, userList.toString());
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }




}

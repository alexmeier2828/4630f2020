package com.AlexMeier.regroup.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentViewHolder;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AlexMeier.regroup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.acl.Group;

import firestream.chat.FireStream;
import firestream.chat.namespace.Fire;
import firestream.chat.realtime.RealtimeService;
import io.reactivex.disposables.Disposable;

public class ChatRoom extends AppCompatActivity {
    private static final String TAG = "CHAT_ROOM";
    private int scrollerID;
    FirebaseUser user;
    FirebaseAuth mAuth;
    GroupChatManager groupChatManager;

    GroupChatResponse currentGroup;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        final Button new_group = findViewById(R.id.send);
        new_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                sendMessage(v);
            }
        });

        //initialize conversation
        mAuth = FirebaseAuth.getInstance();
        GroupChatAPI.joinGroup(mAuth).addOnCompleteListener(new OnCompleteListener<GroupChatResponse>() {
            @Override
            public void onComplete(@NonNull Task<GroupChatResponse> task) {
                if(!task.isSuccessful()){
                    Log.e(TAG, "Failed to get group chat response: " + task.getException());
                }else{
                    subscribeToChat(task.getResult());
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendMessage(View view){
        //read text from message editor
        EditText editText = findViewById(R.id.message);
        String messageText = editText.getText().toString();

        //post message to board
        addMessageToBoard(new Message(messageText, "You", true));

        //firestream send message
        groupChatManager.sendMessage(messageText);
    }


    //TODO set message board to lock until user has been subscribed to a group
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addMessageToBoard(Message message){
        String messageText = message.getMessageBody();
        MessageFragment messageFragment = MessageFragment.newInstance(message.getMessageAuthor(), messageText, message.isUserIsSender());
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, messageFragment);
        transaction.commit();
    }



    private void subscribeToChat(GroupChatResponse group){
        //subscribe to chat message topic
        groupChatManager = new GroupChatManager(this, group) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onMessageReceived(Message message) {
                addMessageToBoard(message);
            }
        };
    }
}
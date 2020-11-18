package com.AlexMeier.regroup.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    FirebaseUser user;
    FirebaseAuth mAuth;
    GroupChatManager groupChatManager;

    GroupChatResponse currentGroup;

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
    public void addMessageToBoard(Message message){
        TextView textView = new TextView(this);
        String messageText = message.getMessageBody();
        textView.setText(messageText);

        //determine message style
        if(message.isUserIsSender()){
            textView.setGravity(Gravity.RIGHT);
        } else {
            textView.setGravity(Gravity.LEFT);
        }

        textView.setTextSize(32);
        textView.setPadding(12, 12, 12, 12 );
        //MessageBox messageBox = new MessageBox(this, messageText);
        LinearLayout messageBoard = findViewById(R.id.message_board);
        messageBoard.addView(textView);
    }



    private void subscribeToChat(GroupChatResponse group){
        //subscribe to chat message topic
        groupChatManager = new GroupChatManager(this, group) {
            @Override
            public void onMessageReceived(Message message) {
                addMessageToBoard(message);
            }
        };
    }
}
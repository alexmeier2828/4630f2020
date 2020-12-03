package com.AlexMeier.regroup.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.AlexMeier.regroup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatRoom extends AppCompatActivity {
    private static final String TAG = "CHAT_ROOM";
    private int scrollerID;
    private boolean DEBUG_MODE_ON = false;
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

        //handle send button
        final EditText messageEditor = findViewById(R.id.message);
        messageEditor.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if(actionId == EditorInfo.IME_ACTION_SEND){
                            if(messageEditor.getText().toString() != ""){
                                sendMessage();
                            }
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendMessage(){
        //read text from message editor
        EditText editText = findViewById(R.id.message);
        String messageText = editText.getText().toString();
        editText.setText("");

        //do not send empty message
        if(messageText != ""){
            //post message to board
            addMessageToBoard(new Message(messageText, "You", true));

            //firestream send message
            groupChatManager.sendMessage(messageText);
        }
    }


    //TODO set message board to lock until user has been subscribed to a group

    /**
     * Adds a new message bubble to the message board with a smooth animation
     * @param message
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addMessageToBoard(Message message){
        String messageText = message.getMessageBody();
        MessageFragment messageFragment = MessageFragment.newInstance(message.getMessageAuthor(), messageText, message.isUserIsSender());
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, messageFragment);
        transaction.commit();

        //scroll to bottom of scroll view with smooth animation
        ScrollView scrollView = findViewById(R.id.chatroom_scroll);
        LinearLayout scrollLinearLayout = findViewById(R.id.fragment_holder);
        ObjectAnimator scrollAnimation = ObjectAnimator.ofInt(scrollView, "scrollY", scrollLinearLayout.getBottom());
        scrollAnimation.setDuration(1000);
        scrollAnimation.start();

    }


    /**
     * Subscribes to updates to group meta data
     * @param group
     */
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
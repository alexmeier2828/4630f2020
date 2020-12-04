package com.AlexMeier.regroup.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.AlexMeier.regroup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatRoom extends FragmentActivity {
    private static final String TAG = "CHAT_ROOM";
    private int scrollerID;
    private FragmentManager fragmentManager;
    FirebaseUser user;
    FirebaseAuth mAuth;
    GroupChatManager groupChatManager;

    GroupChatResponse currentGroup;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        fragmentManager = getSupportFragmentManager();
        //initialize conversation
        mAuth = FirebaseAuth.getInstance();
        GroupChatAPI.joinGroup(mAuth).addOnCompleteListener(new OnCompleteListener<GroupChatResponse>() {
            @Override
            public void onComplete(@NonNull Task<GroupChatResponse> task) {
                if(!task.isSuccessful()){
                    Log.e(TAG, "Failed to get group chat response: " + task.getException());
                }else{
                    subscribeToChat(task.getResult());
                    ProgressBar spinner = findViewById(R.id.spinner);
                    spinner.setVisibility(View.GONE);
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

        //set keyboard send button visible
        messageEditor.setImeOptions(EditorInfo.IME_ACTION_SEND);
        messageEditor.setRawInputType(InputType.TYPE_CLASS_TEXT);
     }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendMessage(){
        //read text from message editor
        EditText editText = findViewById(R.id.message);
        String messageText = editText.getText().toString();
        editText.setText("");

        //do not send empty message
        if(!messageText.isEmpty()){
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
        if(!getSupportFragmentManager().isDestroyed()){
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

    /**
     * Called when activity switch happens
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            leaveGroupDialog();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void leaveGroupDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final TextView leaveGroupWarning = new TextView(this);
        leaveGroupWarning.setText("Do you want to leave this group?");
        dialogBuilder.setView(leaveGroupWarning);
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                groupChatManager.leaveGroup().addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        finish();
                    }
                });
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();
    }
}
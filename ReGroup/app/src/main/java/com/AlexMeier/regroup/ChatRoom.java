package com.AlexMeier.regroup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatRoom extends AppCompatActivity {

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
    }

    public void sendMessage(View view){
        TextView textView = new TextView(this);
        EditText editText = findViewById(R.id.message);
        String messageText = editText.getText().toString();
        textView.setText(messageText);
        textView.setGravity(Gravity.RIGHT);
        textView.setTextSize(32);
        textView.setPadding(12, 12, 12, 12 );
        //MessageBox messageBox = new MessageBox(this, messageText);
        LinearLayout messageBoard = findViewById(R.id.message_board);
        messageBoard.addView(textView);
    }
}
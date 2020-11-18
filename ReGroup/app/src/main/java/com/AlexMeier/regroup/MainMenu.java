package com.AlexMeier.regroup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.AlexMeier.regroup.messaging.ChatRoom;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button new_group = findViewById(R.id.new_group);
        new_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                newGroup(v);
            }
        });

    }

    public void newGroup(View view){
        android.content.Intent intent = new android.content.Intent(this, ChatRoom.class);
        startActivity(intent);
    }
}
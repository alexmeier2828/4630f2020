package com.AlexMeier.regroup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.AlexMeier.regroup.messaging.ChatRoom;
import com.AlexMeier.regroup.profile.ProfileActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //map new_group buttion
        final Button new_group = findViewById(R.id.new_group);
        new_group.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                newGroup(v);
            }
        });

        //map profile button
        final Button view_profile = findViewById(R.id.Profile);
        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUserProfile(v);
            }
        });

    }

    private void viewUserProfile(View v) {
       Intent intent = new Intent(this, ProfileActivity.class);
       startActivity(intent);
    }

    public void newGroup(View view){
        Intent intent = new Intent(this, ChatRoom.class);
        startActivity(intent);
    }
}
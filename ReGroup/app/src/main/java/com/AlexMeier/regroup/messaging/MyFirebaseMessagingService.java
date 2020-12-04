package com.AlexMeier.regroup.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private List<Consumer<RemoteMessage>> SubscriberCallbacks;
    public MyFirebaseMessagingService() {
        SubscriberCallbacks = new ArrayList<Consumer<RemoteMessage>>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        for (Consumer<RemoteMessage> callback: this.SubscriberCallbacks
             ) {
            callback.accept(remoteMessage);
        }
    }


    //add subscriber
    public void addSubscriberCallback(Consumer<RemoteMessage> callback){
        this.SubscriberCallbacks.add(callback);

    }

    public void clearSubscriberCallbacks(){
        this.SubscriberCallbacks = new ArrayList<>();
    }
}

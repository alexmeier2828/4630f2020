package com.AlexMeier.regroup.messaging;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AlexMeier.regroup.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mSender;
    private String mBody;
    private Boolean mSenderIsYou;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sender Name of the sender.
     * @param body Body text of the message.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String sender, String body, Boolean senderIsYou) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, sender);
        args.putString(ARG_PARAM2, body);
        args.putBoolean(ARG_PARAM3, senderIsYou);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MessageFragment", "oncreate" + (savedInstanceState == null));
        if (getArguments() != null) {
            mSender = getArguments().getString(ARG_PARAM1);
            mBody = getArguments().getString(ARG_PARAM2);
            mSenderIsYou = getArguments().getBoolean(ARG_PARAM3);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("MessageFragment", "oncreateView");
        int layout;
        if(mSenderIsYou){
            layout = R.layout.fragment_message_from_sender;
        }
        else {
            layout = R.layout.fragment_message;
        }
                View view = inflater.inflate(layout, container, false);
        TextView bodyText = view.findViewById(R.id.message_body);
        bodyText.setText(mBody);
        TextView senderText = view.findViewById(R.id.message_sender);
        senderText.setText(mSender);
        return view;
    }
}
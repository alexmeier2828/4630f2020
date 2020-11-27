package com.AlexMeier.regroup.messaging;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GroupChatResponse {
    private int size;
    private String name;
    private ArrayList<String> members;

    public GroupChatResponse(ArrayList<String> memberList, String name, int size){
        members = memberList;
        this.name = name;
        this.size = size;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }
}


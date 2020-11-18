package com.AlexMeier.regroup.messaging;

public class GroupChatResponse {
    private int size;
    private String name;
    private String[] members;

    public GroupChatResponse(String[] memberList, String name, int size){
        members = memberList;
        this.name = name;
        this.size = size;
    }

    public String[] getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }
}


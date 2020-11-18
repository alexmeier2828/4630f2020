package com.AlexMeier.regroup.messaging;

/**
 * class to encapulate messages
 * contains all information needed to display a message
 */
public class Message {
    private String messageBody;
    private String messageAuthor;
    private boolean userIsSender;

    public Message(String messageBody, String messageAuthor, boolean userIsSender){
        this.messageBody = messageBody;
        this.messageAuthor = messageAuthor;
        this.userIsSender = userIsSender;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getMessageAuthor() {
        return messageAuthor;
    }

    public boolean isUserIsSender() {
        return userIsSender;
    }
}

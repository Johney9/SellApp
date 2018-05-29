package com.app.sell.events;

import com.app.sell.model.Chatroom;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatNotificationEvent {

    public final String title;
    public final String message;
    public final String iconUri;
    public final Chatroom chatroom;
    public final int newMessageNumber;
}

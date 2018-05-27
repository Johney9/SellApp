package com.app.sell.events;

import com.app.sell.model.Chatroom;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatroomLoadedEvent {
    public final Chatroom chatroom;
}

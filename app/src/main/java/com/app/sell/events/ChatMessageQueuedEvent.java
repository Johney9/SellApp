package com.app.sell.events;

import com.app.sell.model.ChatMessage;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatMessageQueuedEvent {
    public final ChatMessage chatMessage;
}

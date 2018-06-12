package com.app.sell.model;

import android.support.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Parcel
public class ChatMessage implements Comparable<ChatMessage> {

    String id;
    String senderId;
    String senderUsername;
    String message;
    Long timestamp;

    @ParcelConstructor
    public ChatMessage(String id, String senderId, String senderUsername, String message, Long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(@NonNull ChatMessage o) {
        return timestamp.compareTo(o.getTimestamp());
    }
}

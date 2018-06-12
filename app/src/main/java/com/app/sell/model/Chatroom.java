package com.app.sell.model;


import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Parcel
public class Chatroom {

    String id;
    String chatroomName;
    String askerId;
    String offerId;
    String offerImageUri;
    Map<String, ChatMessage> chatroomMessages;
    Map<String, Map<String, Integer>> users;

    @ParcelConstructor
    public Chatroom(String id, String chatroomName, String askerId, String offerId, String offerImageUri, Map<String, ChatMessage> chatroomMessages, Map<String, Map<String, Integer>> users) {
        this.id = id;
        this.chatroomName = chatroomName;
        this.askerId = askerId;
        this.offerId = offerId;
        this.offerImageUri = offerImageUri;
        this.chatroomMessages = chatroomMessages;
        this.users = users;
    }
}

package com.app.sell.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chatroom implements Parcelable {

    private String chatroomName;
    private String chatroomId;
    private String askerId;
    private String offererId;
    private String offerId;
    private List<ChatMessage> chatroomMessages;

    protected Chatroom(Parcel in) {
        chatroomName = in.readString();
        chatroomId = in.readString();
        askerId = in.readString();
        offererId = in.readString();
        offerId = in.readString();
    }

    public static final Creator<Chatroom> CREATOR = new Creator<Chatroom>() {
        @Override
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        @Override
        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatroomName);
        dest.writeString(chatroomId);
        dest.writeString(askerId);
        dest.writeString(offererId);
        dest.writeString(offerId);
    }
}

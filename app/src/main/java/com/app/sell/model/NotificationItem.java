package com.app.sell.model;

import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationItem implements Comparable<NotificationItem> {

    private String username;
    private String title;
    private String description;
    private String iconUri;
    private String chatroomId;
    private Long timestamp;

    @Override
    public int compareTo(@NonNull NotificationItem o) {
        return timestamp.compareTo(o.getTimestamp());
    }
}

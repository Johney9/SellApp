package com.app.sell.events;

import com.app.sell.model.User;

public class UserRetrievedEvent {

    public final User user;

    public UserRetrievedEvent(User user) {
        this.user = user;
    }

}

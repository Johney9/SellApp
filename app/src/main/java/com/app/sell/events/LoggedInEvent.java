package com.app.sell.events;

import com.app.sell.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoggedInEvent {
    public final User user;
}

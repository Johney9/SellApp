package com.app.sell.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String uid;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String location;
    private String image;
    private String email;

}

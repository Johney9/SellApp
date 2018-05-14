package com.app.sell.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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

    public User(FirebaseUser firebaseUser) {
        this.setImage(String.valueOf(firebaseUser.getPhotoUrl()));
        String[] splitUsername = firebaseUser.getDisplayName().split(" ");
        this.setUsername(firebaseUser.getDisplayName());
        this.setFirstName(splitUsername[0]);
        this.setLastName(splitUsername[splitUsername.length - 1]);
        this.setEmail(firebaseUser.getEmail());
        this.setUid(firebaseUser.getUid());
    }
}

package com.coc.deep.anytimepay;

/**
 * Created by Deep on 04/11/2017.
 */

public class UserDetails {
    String username;
    String password;

    public UserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

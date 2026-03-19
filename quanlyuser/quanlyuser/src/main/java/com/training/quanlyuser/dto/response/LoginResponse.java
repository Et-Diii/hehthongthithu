package com.training.quanlyuser.dto.response;

import java.util.Set;



//hà hồng dương -12/3/2026
public class LoginResponse {

    private String token;
    private String username;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }



    public void setUsername(String username) { this.username = username; }
}
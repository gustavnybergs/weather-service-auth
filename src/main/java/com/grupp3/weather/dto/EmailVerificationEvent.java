package com.grupp3.weather.dto;

import java.io.Serializable;

public class EmailVerificationEvent implements Serializable {
    private String email;
    private String username;
    private String verificationToken;

    public EmailVerificationEvent() {}

    public EmailVerificationEvent(String email, String username, String verificationToken) {
        this.email = email;
        this.username = username;
        this.verificationToken = verificationToken;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
}

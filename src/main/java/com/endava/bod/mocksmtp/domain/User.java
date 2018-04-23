package com.endava.bod.mocksmtp.domain;

public class User {

    private String user;
    private String pass;
    private String email;
    private boolean activated;


    public User(){
    }

    public User(String user, String email) {
        this.user = user;
        this.email = email;
        this.activated = false;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

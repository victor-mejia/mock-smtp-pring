package com.endava.bod.mocksmtp.domain;

public class Activation {
    private String code;
    private String user;

    public Activation(){}

    public Activation(String code, String user) {
        this.code = code;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

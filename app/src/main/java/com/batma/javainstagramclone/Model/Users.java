package com.batma.javainstagramclone.Model;

public class Users {
    private String id;
    private String email;
    private String password;
    private String resimUri;

    public Users(String email, String password, String resimUri) {
        this.email = email;
        this.password = password;
        this.resimUri = resimUri;
    }

    public Users() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResimUri() {
        return resimUri;
    }

    public void setResimUri(String resimUri) {
        this.resimUri = resimUri;
    }
}

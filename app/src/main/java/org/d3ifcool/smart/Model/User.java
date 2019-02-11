package org.d3ifcool.smart.Model;

public class User {
    private String id;
    private String username;
    private String fullname;
    private String email;
    private String imageurl;
    private String password;
    private String typeAccount;
    private String houseName;

    public User(String id, String username, String fullname, String imageurl, String typeAccount, String houseName, String password) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.typeAccount = typeAccount;
        this.houseName = houseName;
        this.password = password;
    }

    public User(){

    }

//    public User(String username, String password, String UUID, String accountType) {
//        this.username = username;
//        this.password = password;
//        this.imageurl = UUID;
//        this.typeAccount = accountType;
//    }

    public User(String username, String password, String email, String accountType) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.typeAccount = accountType;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTypeAccount() {
        return typeAccount;
    }

    public void setTypeAccount(String typeAccount) {
        this.typeAccount = typeAccount;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
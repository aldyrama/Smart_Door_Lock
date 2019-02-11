package org.d3ifcool.smart.Account;


public class Account {
    private  String mUsername;
    private  String mImage ;
    private  String mTypeAccount;
    private String mFullName;
    private String mEmail;
    private String mPassword;


    public Account(String mUsername, String mImage, String mTypeAccount, String mFullName, String mEmail, String mPassword) {
        this.mUsername = mUsername;
        this.mImage = mImage;
        this.mTypeAccount = mTypeAccount;
        this.mFullName = mFullName;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public Account(String string, String string1, String string2, String string3, String string4) {
        this.mUsername = string;
        this.mImage = string1;
        this.mTypeAccount = string1;
        this.mFullName = string2;
        this.mEmail = string3;
        this.mPassword = string4;
    }

    public Account(String username, String img, String typeAccount, String password) {
        this.mUsername = username;
        this.mImage = img;
        this.mTypeAccount = typeAccount;
        this.mPassword = password;
    }

    public Account(String username, String typeAccount, String password) {
        this.mUsername = username;
        this.mTypeAccount = typeAccount;
        this.mPassword = password;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmTypeAccount() {
        return mTypeAccount;
    }

    public void setmTypeAccount(String mTypeAccount) {
        this.mTypeAccount = mTypeAccount;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}

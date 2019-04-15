package org.d3ifcool.smart.Model;

public class Connect {

    public String users;

    private int photo;

    private String fullName;

    private String key;

    private int position;

    public Connect(String users) {

        if (users.trim().equals("")) {

            users = "No Name";
        }

        this.users = users;

    }

    public Connect(int position){

        this.position = position;

    }

    public Connect() {

        //empty constructor needed

    }

    public Connect(String str_invite, String str_username) {

        this.users = str_invite;

        this.fullName = str_username;

    }


    public String getUsers() {

        return users;

    }

    public void setUsers(String users) {

        this.users = users;

    }

    public int getPhoto() {

        return photo;

    }

    public void setPhoto(int photo) {

        this.photo = photo;

    }

    public String getFullName() {

        return fullName;

    }

    public void setFullName(String fullName) {

        this.fullName = fullName;

    }

    public String getKey() {

        return key;

    }

    public void setKey(String key) {

        this.key = key;

    }

}

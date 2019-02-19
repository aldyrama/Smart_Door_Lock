package org.d3ifcool.smart.Model;

public class Connect {
    public String users;
    private int photo;
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


    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

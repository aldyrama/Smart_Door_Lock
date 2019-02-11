package org.d3ifcool.smart.Model;

public class History {
    private String id;
    private String usernamse;
    private String fullname;
    private String date;
    private String imageurl;

    public History(String id, String usernamse, String fullname, String date, String imageurl) {
        this.id = id;
        this.usernamse = usernamse;
        this.fullname = fullname;
        this.date = date;
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsernamse() {
        return usernamse;
    }

    public void setUsernamse(String usernamse) {
        this.usernamse = usernamse;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}

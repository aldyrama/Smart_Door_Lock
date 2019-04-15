package org.d3ifcool.smart.Model;

public class History {

    private String fullname;

    private String date;

    private String imageurl;

    public History(String id, String usernamse, String fullname, String date, String imageurl) {

        this.fullname = fullname;

        this.date = date;

        this.imageurl = imageurl;

    }

    public History() {

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

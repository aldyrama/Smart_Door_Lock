package org.d3ifcool.smart.Model;

public class User {

    private String id;

    private String fullname;

    private String email;

    private String imageurl;

    private String password;

    private String typeAccount;

    private String houseName;

    private String start_access;

    private String expired;

    private String time;

    private String date;

    private int age;

    private String lock;

    private String lockImage;

    private String door;

    private String key;

    private boolean isChecked;

    public User(String id, String username, String fullname, String imageurl, String typeAccount, String houseName, String password) {

        this.id = id;

        this.fullname = fullname;

        this.imageurl = imageurl;

        this.typeAccount = typeAccount;

        this.houseName = houseName;

        this.password = password;

    }

    public User(){

    }

    public User(String username, String password, String email, String accountType) {

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


    public String getFullname() {

        return fullname;

    }

    public void setFullname(String fullname) {

        this.fullname = fullname;

    }

    public String getImageurl() {

        return imageurl;

    }

    public void setImageurl(String imageurl)
    {
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

    public String getStart_access() {

        return start_access;

    }

    public void setStart_access(String start_access) {

        this.start_access = start_access;

    }

    public String getExpired() {

        return expired;

    }

    public void setExpired(String expired) {

        this.expired = expired;

    }

    public String getTime() {

        return time;

    }

    public String getDate() {

        return date;

    }

    public void setTime(String time) {

        this.time = time;

    }

    public int getAge() {

        return age;

    }

    public void setAge(int age) {

        this.age = age;

    }

    public String getKey() {

        return key;

    }

    public void setKey(String key) {

        this.key = key;

    }

    public String getLock() {

        return lock;

    }

    public void setLock(String lock) {

        this.lock = lock;

    }

    public String getLockImage() {

        return lockImage;
    }

    public void setLockImage(String lockImage) {

        this.lockImage = lockImage;

    }

    public String getDoor() {

        return door;

    }

    public void setDoor(String door) {

        this.door = door;

    }

    public void setDate(String date) {

        this.date = date;

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
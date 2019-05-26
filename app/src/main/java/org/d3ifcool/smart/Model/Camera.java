package org.d3ifcool.smart.Model;

public class Camera {

    private String name;
    private String ipAddress;
    private String login;
    private String password;

    public Camera(String str_name, String str_ip) {
        this.name = str_name;
        this.ipAddress = str_ip;
    }

    public Camera() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

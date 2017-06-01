package com.adrian.farley.tools.ftp;

/**
 * Created by adrian on 16-11-22.
 */
public class Ftp {

    private String ipAddr;//ip地址

    private int port;//端口号

    private String userName;//用户名

    private String pwd;//密码

    private String path;//路径

    public Ftp(String ipAddr, int port, String userName, String pwd, String path) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.userName = userName;
        this.pwd = pwd;
        this.path = path;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}

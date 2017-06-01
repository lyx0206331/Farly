package com.adrian.farley.pojo;

/**
 * Created by adrian on 16-11-22.
 */

public class LanDev {
    private String ip;
    private int port;
    private String id;

    public LanDev(String ip, int port, String id) {
        this.ip = ip;
        this.port = port;
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LanDev{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", id='" + id + '\'' +
                '}';
    }
}

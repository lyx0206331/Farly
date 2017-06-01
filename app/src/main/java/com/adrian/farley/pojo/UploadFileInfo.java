package com.adrian.farley.pojo;

/**
 * Created by adrian on 16-12-13.
 */

public class UploadFileInfo {

    private String name;
    private String path;

    public UploadFileInfo() {
    }

    public UploadFileInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

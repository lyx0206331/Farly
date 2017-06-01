package com.adrian.farley.pojo;

/**
 * Created by adrian on 16-12-17.
 */

public class WarningInfo {
    private String index;
    private String value;

    public WarningInfo() {
    }

    public WarningInfo(String index, String value) {
        this.index = index;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

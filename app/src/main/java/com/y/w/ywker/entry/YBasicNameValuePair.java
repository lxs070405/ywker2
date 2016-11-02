package com.y.w.ywker.entry;

/**
 * Created by lxs on 16/4/13.
 */

public class YBasicNameValuePair implements NameValuePair {

    private String name = "";
    private String value = "";
    public YBasicNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}

package com.fs.fs.bean;

/**
 * Created by wyx on 2016/12/31.
 */

public class WIFIInfo {
    public String SSID = "";
    public String psk = "";

    @Override
    public String toString() {
        return String.format("%s:%s", SSID, psk);
    }
}

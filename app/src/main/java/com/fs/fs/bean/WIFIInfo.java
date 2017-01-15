package com.fs.fs.bean;

import java.io.Serializable;

/**
 * Created by wyx on 2016/12/31.
 */

public class WIFIInfo implements Serializable {
    public String SSID = "";
    public String psk = "";

    @Override
    public String toString() {
        return String.format("%s:%s", SSID, psk);
    }
}

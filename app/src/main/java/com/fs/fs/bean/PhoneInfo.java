package com.fs.fs.bean;

import java.io.Serializable;

/**
 * Created by wyx on 2016/12/31.
 */

public class PhoneInfo implements Serializable {
    public String name = "";
    public String phoneNumber = "";
    public String time = "";
    public String duration = "";
    public int type = 0;

    // Call type
    public static final int INCOMING = 1;
    public static final int OUTGOING = 2;
    public static final int MISSING = 3;


    @Override
    public String toString() {
        return String.format("%s:%s:%s:%s", phoneNumber, name, time, duration);
    }
}

package com.fs.fs.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wyx on 2016/12/31.
 */

public class PhoneInfo implements Serializable {
    @SerializedName("name") public String name ;
    @SerializedName("phone_number")public String phoneNumber ;
    @SerializedName("time")public String time ;
    @SerializedName("duration")public String duration ;
    @SerializedName("type")public int type ;

    // Call type
    public static final int INCOMING = 1;
    public static final int OUTGOING = 2;
    public static final int MISSING = 3;


    @Override
    public String toString() {
        return String.format("%s:%s:%s:%s", phoneNumber, name, time, duration);
    }
}

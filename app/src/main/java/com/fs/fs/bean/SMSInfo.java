package com.fs.fs.bean;

import java.io.Serializable;

/**
 * Created by wyx on 2016/12/30.
 */

public class SMSInfo implements Serializable {
    public String content = "";
    public String phoneNumber = "";
    public String time = "";
    public String name = "";

    @Override
    public String toString() {
        return String.format("%s:%s:%s:%s", content, phoneNumber, time, name);
    }
}

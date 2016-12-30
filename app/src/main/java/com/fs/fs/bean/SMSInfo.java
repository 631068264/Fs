package com.fs.fs.bean;

/**
 * Created by wyx on 2016/12/30.
 */

public class SMSInfo {
    public String content;
    public String phoneNumber;
    public String time;
    public String person;

    @Override
    public String toString() {
        return String.format("%s:%s:%s:%s", content, phoneNumber, time, person);
    }
}

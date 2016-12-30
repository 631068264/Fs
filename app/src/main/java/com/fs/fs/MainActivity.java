package com.fs.fs;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 应用监控
//        AppInfoService appInfoService = new AppInfoService(MainActivity.this);
//        appInfoService.getRunningAppInfo();
//        Logger.d("__________________end_____________");
//        appInfoService.getInstallAppInfo();

//        new LocationService(MainActivity.this).interval(new LocationService.LocateListener() {
//            @Override
//            public void onLocateSucceed(AMapLocation aMapLocation) {
//                Location location = new Location();
//                location.province = aMapLocation.getProvince();
//                location.city = aMapLocation.getCity();
//                location.district = aMapLocation.getDistrict();
//                location.street = aMapLocation.getStreet();
//                location.longitude = aMapLocation.getLongitude();
//                location.latitude = aMapLocation.getLatitude();
//                location.time = DateUtils.millis2String(aMapLocation.getTime());
//
//                LogUtils.d("%s:%s %s %s", location.latitude, location.longitude, location.city, location.street);
//            }
//
//            @Override
//            public void onLocateFail(int errorCode, String errorInfo) {
//
//            }
//        });

//        SMSReceiver.setListener(new SMSReceiver.SMSListener() {
//            @Override
//            public void onReceive(SmsMessage msg) {
//                SMSInfo smsInfo = new SMSInfo();
//                smsInfo.content = (msg.getMessageBody());
//                smsInfo.phoneNumber = msg.getOriginatingAddress();
//
//                smsInfo.time = DateUtils.millis2String(msg.getTimestampMillis());
//                LogUtils.d(smsInfo.toString());
//            }
//        });

//        new PhoneService(MainActivity.this).getSMS(new PhoneService.SMSListener() {
//            @Override
//            public void onGetAllSMS(List<SMSInfo> SMSInfo) {
//
//            }
//
//            @Override
//            public void onReceive(SMSInfo msgInfo) {
//                LogUtils.d(msgInfo.toString());
//            }
//
//
//        });
    }


}

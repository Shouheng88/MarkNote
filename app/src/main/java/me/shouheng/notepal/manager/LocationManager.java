package me.shouheng.notepal.manager;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationManager {

    private static LocationManager sInstance;

    private LocationClient mLocationClient;

    private BDLocationListener bdLocationListener;

    public static LocationManager getInstance(Context mContext){
        if (sInstance == null){
            synchronized (LocationManager.class) {
                if (sInstance == null) {
                    sInstance = new LocationManager(mContext.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private LocationManager(Context mContext){
        mLocationClient = new LocationClient(mContext);
    }

    public void locate(BDLocationListener mListener){
        bdLocationListener = mListener;
        mLocationClient.registerLocationListener(bdLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.requestLocation();
        mLocationClient.start();
    }

    public void stop() {
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
            if (bdLocationListener != null) {
                mLocationClient.unRegisterLocationListener(bdLocationListener);
                bdLocationListener = null;
            }
        }
    }
}

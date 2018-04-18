package com.mvvm.kipl.mvvmdemo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;


public class NetworkUtil extends BroadcastReceiver {

            static INetworkUtil _iNetworkUtilListener;
        static ArrayList<INetworkUtil> iNetworkUtilArrayList = new ArrayList<>();
            public  static NetworkUtil networkUtil;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // should check null because in air plan mode it will be null
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void setNetworkListener(INetworkUtil iNetworkUtil) {
        _iNetworkUtilListener = iNetworkUtil;
    }

    public static void addNetworkListener(INetworkUtil  iNetworkUtil){
        if (!iNetworkUtilArrayList.contains(iNetworkUtil)) {
            iNetworkUtilArrayList.add(iNetworkUtil);
        }

    }


    public void removeNetworkListener(INetworkUtil  iNetworkUtil){
        if (iNetworkUtilArrayList.contains(iNetworkUtil)) {
            iNetworkUtilArrayList.remove(iNetworkUtil);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {

     /*  if (isOnline(context)) {
            if (_iNetworkUtilListener != null)
                _iNetworkUtilListener.onNetworkChange(true);

        } else {
            if (_iNetworkUtilListener != null)
                _iNetworkUtilListener.onNetworkChange(false);
        }*/

        for (int i = 0; i < iNetworkUtilArrayList.size(); i++) {
            iNetworkUtilArrayList.get(i).onNetworkChange(isOnline(context));
        }
    }

    public interface INetworkUtil {
        void onNetworkChange(boolean available);
    }

    public void onNetworkChange(INetworkUtil _iNetworkUtilListener) {
        this._iNetworkUtilListener = _iNetworkUtilListener;
    }

    public static NetworkUtil getInstance(){
        if(networkUtil == null)
            networkUtil = new NetworkUtil();
        return networkUtil;
    }

}

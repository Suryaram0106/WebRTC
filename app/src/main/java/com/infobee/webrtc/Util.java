package com.infobee.webrtc;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Util {

    public static boolean hasInternetAccess(Context context) {
        if (context != null) {
            ConnectivityManager mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = mCM.getActiveNetworkInfo();
            boolean isConnection = netInfo != null && netInfo.isConnectedOrConnecting();
            if (!isConnection) {
                Toast.makeText(context, context.getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
            return isConnection;
        }
        return false;
    }

    public static String converFbDOB(String fbDOB) {
        String dateString = "";
        TimeZone tz = TimeZone.getTimeZone("IST");
        SimpleDateFormat formatInput = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat formatdate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dateString = formatdate.format(fbDOB);
            return dateString;
        } catch (Exception var9) {
            var9.printStackTrace();
            return dateString;
        }
    }
    public static boolean verifyPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static void hideKeypad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}

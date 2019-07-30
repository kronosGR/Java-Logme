package me.kandz.logme.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    /**
     * checks if the device has microphone
     * @param context
     * @return true if it has microphone.
     */
    public static boolean hasMicrophone(Context context){
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    /**
     * checks if the device has camera
     * @param context
     * @return true if it has microphone
     */
    public static boolean hasCamera(Context context){
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * returns the current time
     * @return
     */
    public static String getTime(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(d);
    }

    /**
     * returns the current date
     * @return
     */
    public static String getDate(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        return sdf.format(d);
    }

    /**
     * returns the current day
     * @return
     */
    public static String getDay(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        return sdf.format(d);
    }
}

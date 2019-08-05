package me.kandz.logme.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

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

    /**
     * checks if gps and network providers are on
     * @param context
     * @return true if is on
     */
    public static boolean checkIfLocationIsOn(Context context){
        LocationManager lm =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        //boolean network_enabled = false;

        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}

      //  try{
      //      gps_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      //  } catch (Exception ex){}

        return gps_enabled;
    }

    /**
     * restart the application
     * @param context
     */
    public static void restartApp(Context context){
        Intent i = context.getPackageManager().
                getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}

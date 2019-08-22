package me.kandz.logme.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import me.kandz.logme.Database.LogSqlLiteOpenHelper;

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

    /**
     * creates a zip file from a list of files
     * @param files the list of file urls
     * @param zipFilename the zip filename
     */
    public static boolean zipFiles(List<String> files, String zipFilename){
        boolean success = false;
        final int BUFFER = 2048;

        try {
            BufferedInputStream origin = null;

            FileOutputStream dest = new FileOutputStream(zipFilename);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte[] data = new byte[BUFFER];

            for (int i=0; i<files.size(); i++){
                String tmp = files.get(i);
                String tmpFilename = "";
                if (tmp.contains("file://"))
                    tmpFilename = tmp.substring(7);
                else
                    tmpFilename = tmp;

                Log.v("Creating Zip file: "+zipFilename, "Adding: " + tmpFilename);
                FileInputStream fi = new FileInputStream(tmpFilename);
                origin = new BufferedInputStream(fi, BUFFER );

                ZipEntry entry = new ZipEntry(tmpFilename.substring(tmpFilename.lastIndexOf("/") + 1));
                out.putNextEntry(entry);

                int count;
                while ((count = origin.read(data,0,BUFFER)) !=-1 ){
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    /**
     * check if the image file is rotated
     * @param file the image file
     * @return the angles so the image file is normal.
     */
    public static int needRotation(String file){
        try {
            ExifInterface exifInterface = new ExifInterface(file);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                return 270;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                return 180;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                return 90;
            else
                return 0;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * removes the unwanted characters from a file name
     * @param tmpFilename the original filename
     * @return the correct filename
     */
    public static String trimFilename(String tmpFilename){
        final String ReserverdChars = "|\\?*<\":>+[]/' ";
        String filename = "";
        filename = tmpFilename.replaceAll("[" + Pattern.quote(ReserverdChars) + "]" , "_");
        return filename;
    }

    /**
     * gets the size of a directory, including the files in the subdirectories
     * @param dir the location
     * @return the total size in long
     */
    public static long getDirSize(File dir){
        if (dir.exists()){
            long result =0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i<fileList.length; i ++){
                if (fileList[i].isDirectory()){
                    result += getDirSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }


    /**
     * a class to create a zip file. The constructor creates the zip and the method
     * addFileToZip() adds files to the zip
     * closeZip() closes the zip file
     */
    public static class MakeZip{
        static final int BUFFER = 2048;

        ZipOutputStream out;
        byte data[];

        public MakeZip(String name){
            FileOutputStream dest = null;
            try {
                dest = new FileOutputStream(name);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            data = new byte[BUFFER];
        }

        public boolean addFileToZip(String name){
            boolean success = false;

            String tmpFilename = "";
            if (name.contains("file://"))
                tmpFilename = name.substring(7);
            else
                tmpFilename = name;

            Log.v("ZIP - ", "adding: ");
            FileInputStream fi = null;
            try{
                fi = new FileInputStream(tmpFilename);
            } catch (FileNotFoundException e){
                e.printStackTrace();
                success = false;
            }
            BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(tmpFilename.substring(tmpFilename.lastIndexOf("/") + 1));
            try {
                out.putNextEntry(entry);
            } catch (IOException e){
                e.printStackTrace();
                success = false;
            }

            int count;
            try {
                while ((count = origin.read(data , 0 ,BUFFER)) !=-1){
                    out.write(data,0,count);
                }
            } catch (IOException e){
                e.printStackTrace();
                success = false;
            }

            try {
                origin.close();
                success = true;
            } catch (IOException e){
                e.printStackTrace();
                success = false;
            }
            return success;
        }

        public void closeZIP(){
            try {
                out.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

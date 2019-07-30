package me.kandz.logme.Database;

import android.provider.BaseColumns;

public class LogContract {

    //private constructor
    private LogContract(){}

    public static final class TypeEntry implements BaseColumns{

        public static final String TABLE_NAME = "types";
        public  static final String COL_NAME = "name";
        public static final String SQL_CREATE_TABLE=
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + COL_NAME + " TEXT NOT NULL)";
    }

    public static final class ExtrasEntry {
        public static final String TABLE_NAME = "extras";
        public static final String COL_LOG_ID = "logid";
        public static final String COL_TYPE_ID = "typeid";
        public static final String COL_URL = "url";
        public static final String COL_DATO = "dato";
        public static final String COL_TIME = "time";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
                + COL_LOG_ID + " INTEGER NOT NULL, "
                + COL_TYPE_ID + " INTEGER NOT NULL, "
                + COL_URL + " TEXT NOT NULL, "
                + COL_DATO + " TEXT, "
                + COL_TIME + " TEXT)";

    }

    public static final class LogsEntry implements BaseColumns{
        public static final String TABLE_NAME = "logs";
        public static final String COL_TITLE = "title";
        public static final String COL_DETAILS = "details";
        public static final String COL_DATO = "dato";
        public static final String COL_DAY = "day";
        public static final String COL_TIME = "time";
        public static final String COL_IMAGE = "image";
        public static final String COL_AUDIO = "audio";
        public static final String COL_VIDEO = "video";
        public static final String COL_LOCATION = "location";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + COL_TITLE + " TEXT, "
                + COL_DETAILS + " TEXT, "
                + COL_DATO + " TEXT, "
                + COL_DAY + " TEXT, "
                + COL_TIME + " TEXT, "
                + COL_IMAGE + " TEXT, "
                + COL_AUDIO + " TEXT, "
                + COL_VIDEO + " TEXT, "
                + COL_LOCATION + " TEXT)";
    }
}

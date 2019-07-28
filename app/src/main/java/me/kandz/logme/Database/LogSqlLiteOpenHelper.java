package me.kandz.logme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.kandz.logme.Database.LogContract.TypeEntry;

public class LogSqlLiteOpenHelper extends SQLiteOpenHelper {

    private static LogSqlLiteOpenHelper instance;

    private static final String DATABASE_NAME = "logme.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;


    private LogSqlLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized LogSqlLiteOpenHelper getInstance(Context context){
        if (instance == null)
            instance = new LogSqlLiteOpenHelper(context.getApplicationContext());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create the tables
        sqLiteDatabase.execSQL(LogContract.ExtrasEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(LogContract.LogsEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(TypeEntry.SQL_CREATE_TABLE);

        //insert the initial values to types table
        initiateTypes(sqLiteDatabase);

    }

    private void initiateTypes(SQLiteDatabase sqLiteDatabase) {
        //Call the insert function
        ContentValues values = new ContentValues();
        values.put(TypeEntry.COL_NAME, "Image");
        insertToTable(sqLiteDatabase, TypeEntry.TABLE_NAME, values);
        values.put(TypeEntry.COL_NAME, "Audio");
        insertToTable(sqLiteDatabase, TypeEntry.TABLE_NAME, values);
        values.put(TypeEntry.COL_NAME, "Video");
        insertToTable(sqLiteDatabase, TypeEntry.TABLE_NAME, values);
        values.put(TypeEntry.COL_NAME, "Location");
        insertToTable(sqLiteDatabase, TypeEntry.TABLE_NAME, values);

    }

    private long insertToTable(SQLiteDatabase sqLiteDatabase, String tableName, ContentValues values) {
        long rowID = sqLiteDatabase.insert(tableName, null, values);
        return rowID;
    }

    public long insertToTable(String tableName, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rowID = sqLiteDatabase.insert(tableName, null, values);
        return rowID;
    }

    public void deleteRecord(String tableName, String selectionCOL, String[] selectionValues){
        String selection = selectionCOL + " = ? ";
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(tableName, selection, selectionValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

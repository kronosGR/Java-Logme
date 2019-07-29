package me.kandz.logme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogContract.TypeEntry;
import me.kandz.logme.Utils.Extras;

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
        sqLiteDatabase.execSQL(ExtrasEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(LogContract.LogsEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(TypeEntry.SQL_CREATE_TABLE);

        //insert the initial values to types table
        initiateTypes(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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

    public List<Extras> readExtras(long rowID) {
        List<Extras> extras = new ArrayList<>();

        String[] columns = {
            ExtrasEntry.COL_LOG_ID,
            ExtrasEntry.COL_TYPE_ID,
            ExtrasEntry.COL_URL,
            ExtrasEntry.COL_DATO,
            ExtrasEntry.COL_TIME
        };

        String selection = ExtrasEntry.COL_LOG_ID + " = ? ";
        String[] selectionArgs = {Long.toString(rowID)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ExtrasEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        int logIdPOS = cursor.getColumnIndex(ExtrasEntry.COL_LOG_ID);
        int typeIdPOS = cursor.getColumnIndex(ExtrasEntry.COL_TYPE_ID);
        int urlPOS = cursor.getColumnIndex(ExtrasEntry.COL_URL);
        int datePOS = cursor.getColumnIndex(ExtrasEntry.COL_DATO);
        int timePOS = cursor.getColumnIndex(ExtrasEntry.COL_TIME);

        while (cursor.moveToNext()){
            Extras tmpExtra = new Extras(
                    cursor.getInt(logIdPOS),
                    cursor.getInt(typeIdPOS),
                    cursor.getString(urlPOS),
                    cursor.getString(datePOS),
                    cursor.getString(timePOS)
            );
            extras.add(tmpExtra);
        }
        return extras;
    }

    /**
     * update any table with the contentvalues where column = columnValue
     * @param tableName
     * @param values
     * @param column
     * @param columnValue
     * @return the affected lines
     */
    public int updateTable(String tableName, ContentValues values, String column, String[] columnValue){
        int rows;
        SQLiteDatabase db = getWritableDatabase();

        String columnString = column + " = ?";
        rows = db.update(tableName, values, columnString, columnValue);
        return rows;
    }
}

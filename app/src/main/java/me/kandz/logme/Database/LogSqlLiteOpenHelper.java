package me.kandz.logme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.Database.LogContract.TypeEntry;
import me.kandz.logme.Utils.Extras;
import me.kandz.logme.Utils.Logs;

public class LogSqlLiteOpenHelper extends SQLiteOpenHelper {

    private static LogSqlLiteOpenHelper instance;

    private static final String DATABASE_NAME = "logme.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;


    private LogSqlLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized LogSqlLiteOpenHelper getInstance(Context context) {
        if (instance == null)
            instance = new LogSqlLiteOpenHelper(context.getApplicationContext());
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create the tables
        sqLiteDatabase.execSQL(ExtrasEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(LogsEntry.SQL_CREATE_TABLE);
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

    /**
     * insert to a table, used only to initialize the tables.
     *
     * @param sqLiteDatabase
     * @param tableName
     * @param values
     * @return
     */
    private long insertToTable(SQLiteDatabase sqLiteDatabase, String tableName, ContentValues values) {
        long rowID = sqLiteDatabase.insert(tableName, null, values);
        return rowID;
    }

    /**
     * insert to a table a record
     *
     * @param tableName the table will be inserted the values to
     * @param values    content values
     * @return the inserted rowID
     */
    public long insertToTable(String tableName, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rowID = sqLiteDatabase.insert(tableName, null, values);
        return rowID;
    }

    /**
     * deletes a record where selectionCOL = selectionValues
     *
     * @param tableName       where the records will be deleted
     * @param selectionCOL    where clause column
     * @param selectionValues where clause value
     */
    public void deleteRecord(String tableName, String selectionCOL, String[] selectionValues) {
        String selection = selectionCOL + " = ? ";
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(tableName, selection, selectionValues);
    }

    /**
     * get from Extras Table a record with where clause logID and typeID
     * @param logID
     * @param typeID
     * @return cursor with results
     */
    public Cursor getExtrasWithIdAndType(String logID, String typeID){
        SQLiteDatabase db = getReadableDatabase();
        String selection = ExtrasEntry.COL_LOG_ID + " = ? AND " + ExtrasEntry.COL_TYPE_ID+ " = ?";
        return db.query(ExtrasEntry.TABLE_NAME, null, selection, new String[] { logID, typeID}
            ,null, null, null);

    }
    /**
     * get all the records for a table for a column = value
     *
     * @param tableName      the table it will get the records
     * @param selectionCol   the column for the where clause
     * @param selectionValue the value that must be equal to
     * @return cursor
     */
    public Cursor getTableWithSelection(String tableName, String selectionCol, String[] selectionValue) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = selectionCol + " = ? ";
        return db.query(tableName, null, selection, selectionValue, null, null, null);
    }

    /**
     * get all the extras for the LOGID
     *
     * @param rowID the logID
     * @return a list of Extras
     */
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

        while (cursor.moveToNext()) {
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
     * read a single log
     * @param rowID the log id
     * @return a logs object
     */
    public Logs getALog(long rowID) {
        String selection = LogsEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(rowID)};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(LogsEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        int idPOS = cursor.getColumnIndex(LogsEntry._ID);
        int titlePOS = cursor.getColumnIndex(LogsEntry.COL_TITLE);
        int detailsPOS = cursor.getColumnIndex(LogsEntry.COL_DETAILS);
        int dayPOS = cursor.getColumnIndex(LogsEntry.COL_DAY);
        int datePOS = cursor.getColumnIndex(LogsEntry.COL_DATO);
        int timePOS = cursor.getColumnIndex(LogsEntry.COL_TIME);
        int audioPOS = cursor.getColumnIndex(LogsEntry.COL_AUDIO);
        int imagePOS = cursor.getColumnIndex(LogsEntry.COL_IMAGE);
        int videoPOS = cursor.getColumnIndex(LogsEntry.COL_VIDEO);
        int locationPOS = cursor.getColumnIndex(LogsEntry.COL_LOCATION);

        cursor.moveToNext();
        boolean location = cursor.getString(locationPOS).equals("TRUE");
        boolean audio = cursor.getString(audioPOS).equals("TRUE");
        boolean image = cursor.getString(imagePOS).equals("TRUE");
        boolean video = cursor.getString(videoPOS).equals("TRUE");
        Logs tmpLog = new Logs(
                cursor.getInt(idPOS),
                cursor.getString(titlePOS),
                cursor.getString(detailsPOS),
                cursor.getString(dayPOS),
                cursor.getString(datePOS),
                cursor.getString(timePOS),
                image, audio, video, location);

        return tmpLog;
    }

    /**
     * update any table with the contentvalues where column = columnValue
     *
     * @param tableName
     * @param values
     * @param column
     * @param columnValue
     * @return the affected lines
     */
    public int updateTable(String tableName, ContentValues values, String column, String[] columnValue) {
        int rows;
        SQLiteDatabase db = getWritableDatabase();

        String columnString = column + " = ?";
        rows = db.update(tableName, values, columnString, columnValue);
        return rows;
    }

    /**
     * get alla the table order by column
     *
     * @param tableName
     * @param orderBy    column
     * @param orderStyle ASC or DESC
     * @return cursor with the results
     */
    public Cursor getTable(String tableName, String orderBy, String orderStyle) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(tableName, null, null, null, null, null
                , orderBy + " " + orderStyle);
    }
}

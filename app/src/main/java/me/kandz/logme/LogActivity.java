package me.kandz.logme;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.kandz.logme.Database.ExtrasAdapter;
import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.Database.LogSqlLiteOpenHelper;
import me.kandz.logme.Database.SwipeToDelete;
import me.kandz.logme.Utils.Extras;
import me.kandz.logme.Utils.Logs;
import me.kandz.logme.Utils.Utils;

public class LogActivity extends AppCompatActivity implements LocationListener {

    public static final int REQUEST_CODE_AUDIO_ACTIVITY = 1;
    public static final String LOGS_OBJECT = "logs_object";
    public static final int REQUEST_CODE_IMAGE_ACTIVITY = 2;
    public static final int REQUEST_CODE_VIDEO_ACTIVITY = 3;
    public static final int REQUEST_CODE_PERMISSION_FOR_LOCATION = 456;
    public static final String POSITION = "position";
    public static final String NEW_LOG = "newLog";
    private TextView dateTxt;
    private TextView timeTxt;
    private TextView dayTxt;
    private EditText titleEdit;
    private EditText detailsEdit;
    private ImageButton locationBtn;
    private ImageButton videoBtn;
    private ImageButton imageBtn;
    private ImageButton audioBtn;
    private RecyclerView recyclerView;
    private long rowID;
    private ExtrasAdapter extrasAdapter;
    private boolean updateLog;
    private Logs updateLogs;
    private ConstraintLayout constraintLayout;
    private LocationManager locationManager;
    private int position;
    private Menu mMenu;
    private MenuItem saveItem;
    private MenuItem mExportItem;
    private static String sLatitude = null;
    private static String sLongitude = null;
    private static MenuItem mShareItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //because they are static variable needs to be initialized.
        sLongitude = null;
        sLatitude = null;

        Intent intent = getIntent();
        if (intent.getParcelableExtra(LOGS_OBJECT) !=null){
            updateLogs = intent.getParcelableExtra(LOGS_OBJECT);
            position = intent.getIntExtra(POSITION, 0);
            intializeActivity();
            updateValues();
            if (intent.getBooleanExtra(NEW_LOG,false))
                updateLog = false;
            else
                updateLog = true;
        } else {
            intializeActivity();
           // createNewRecord();
        }
    }

    /**
     * update the Activity when it is opened for updateing
     */
    private void updateValues() {
        dateTxt.setText(updateLogs.getDato());
        dayTxt.setText(updateLogs.getDay());
        timeTxt.setText(updateLogs.getTime());
        titleEdit.setText(updateLogs.getTitle());
        detailsEdit.setText(updateLogs.getDetails());

        rowID = updateLogs.getID();
        updateRecycleView();
    }

    /**
     * create a new record with only date, day and time and all the booleans false
     */
   /* private void createNewRecord() {
        ContentValues values = new ContentValues();
        values.put(LogsEntry.COL_DATO, dateTxt.getText().toString());
        values.put(LogsEntry.COL_DAY, dayTxt.getText().toString());
        values.put(LogsEntry.COL_TIME, timeTxt.getText().toString());
        values.put(LogsEntry.COL_IMAGE, "FALSE");
        values.put(LogsEntry.COL_VIDEO, "FALSE");
        values.put(LogsEntry.COL_AUDIO, "FALSE");
        values.put(LogsEntry.COL_LOCATION, "FALSE");
        rowID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(LogsEntry.TABLE_NAME,  values);
    }
*/
    /**
     * get the view references and setonclick listeners to the buttons
     */
    private void intializeActivity() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dateTxt = (TextView) findViewById(R.id.dateTextView);
        dayTxt = (TextView)findViewById(R.id.dayTextView);
        timeTxt = (TextView) findViewById(R.id.timeTextView);
        titleEdit = (EditText) findViewById(R.id.titleEditText);
        detailsEdit = (EditText) findViewById(R.id.detailsEditText);
        audioBtn = (ImageButton) findViewById(R.id.soundImageButton);
        imageBtn = (ImageButton) findViewById(R.id.pictureImageButton);
        videoBtn = (ImageButton) findViewById(R.id.videoImageButton);
        locationBtn = (ImageButton) findViewById(R.id.locationImageButton);
        recyclerView = (RecyclerView) findViewById(R.id.logRecyclerView);
        constraintLayout = (ConstraintLayout) findViewById(R.id.con_layout);

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasMicrophone(getApplicationContext())) {
                    startActivityForResult(AudioActivity.makeIntent(getApplicationContext()), REQUEST_CODE_AUDIO_ACTIVITY);
                } else {
                    Toast.makeText(getApplicationContext(), "Your device does not have a microphone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasCamera(getApplicationContext())){
                    startActivityForResult(ImageActivity.makeIntent(getApplicationContext()), REQUEST_CODE_IMAGE_ACTIVITY);
                } else {
                    Toast.makeText(getApplicationContext(), "Your device does not have a camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasCamera(getApplicationContext())){
                    startActivityForResult(VideoActivity.makeIntent(getApplicationContext() ), REQUEST_CODE_VIDEO_ACTIVITY);
                } else {
                    Toast.makeText(getApplicationContext(), "Your device does not have a camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkIfLocationIsOn(getApplicationContext())){
                    checkPermissionsForLocation();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please turn on your device's lccation ,GPS/Network", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateDateTimeDay();
    }

    private void checkPermissionsForLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_FOR_LOCATION);
        } else {
            //take location
            getLocationOfTheDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_FOR_LOCATION){
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //take the location
                getLocationOfTheDevice();
            } else {
                Toast.makeText(this,"You must give permissions so the app can get your locattion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * gets the devices location
     */
    private void getLocationOfTheDevice() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        saveItem.setVisible(false);
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e){}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_AUDIO_ACTIVITY){
            if (resultCode == RESULT_OK){
                String filename = data.getData().toString();
                ContentValues values = new ContentValues();
                values.put(ExtrasEntry.COL_LOG_ID, rowID);
                values.put(ExtrasEntry.COL_TYPE_ID, 2); // 2 Audio
                values.put(ExtrasEntry.COL_URL, filename);
                values.put(ExtrasEntry.COL_DATO, Utils.getDate());
                values.put(ExtrasEntry.COL_TIME, Utils.getTime());
                long extraID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(ExtrasEntry.TABLE_NAME, values );

                updateRecycleView();

                values.clear();
                values.put(LogsEntry.COL_AUDIO, "TRUE");
                int rows = LogSqlLiteOpenHelper.getInstance(this).updateTable(LogsEntry.TABLE_NAME,
                        values, LogsEntry._ID, new String[] {Long.toString(rowID)});
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_ACTIVITY) {
            if (resultCode == RESULT_OK){
                String filename = data.getData().toString();
                ContentValues values = new ContentValues();
                values.put(ExtrasEntry.COL_LOG_ID , rowID);
                values.put(ExtrasEntry.COL_TYPE_ID, 1);  // 1 Image
                values.put(ExtrasEntry.COL_URL, filename);
                values.put(ExtrasEntry.COL_DATO, Utils.getDate());
                values.put(ExtrasEntry.COL_TIME, Utils.getTime());
                long extraID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(ExtrasEntry.TABLE_NAME, values);

                updateRecycleView();

                values.clear();
                values.put(LogsEntry.COL_IMAGE, "TRUE");
                int rows = LogSqlLiteOpenHelper.getInstance(this).updateTable(LogsEntry.TABLE_NAME,
                        values, LogsEntry._ID, new String[] { Long.toString(rowID)});
            }
        }
        else if (requestCode == REQUEST_CODE_VIDEO_ACTIVITY){
            if (resultCode == RESULT_OK){
                String filename = data.getData().toString();
                ContentValues values = new ContentValues();
                values.put(ExtrasEntry.COL_LOG_ID, rowID);
                values.put(ExtrasEntry.COL_TYPE_ID, 3);  //Video
                values.put(ExtrasEntry.COL_URL, filename);
                values.put(ExtrasEntry.COL_DATO, Utils.getDate());
                values.put(ExtrasEntry.COL_TIME, Utils.getTime());
                long extraID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(ExtrasEntry.TABLE_NAME, values);

                updateRecycleView();

                values.clear();
                values.put(LogsEntry.COL_VIDEO, "TRUE");
                int rows = LogSqlLiteOpenHelper.getInstance(this).updateTable(LogsEntry.TABLE_NAME,
                        values, LogsEntry._ID, new String[] {Long.toString(rowID)});
            }
        }
    }

    /**
     * update the recyclew view.
     */
    private void updateRecycleView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        List<Extras> extras = LogSqlLiteOpenHelper.getInstance(this).readExtras(rowID);
        extrasAdapter = new ExtrasAdapter(this, extras, constraintLayout);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(extrasAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(extrasAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_menu_save:
                Toast.makeText(this, "Log updated.", Toast.LENGTH_LONG).show();
                ContentValues values = new ContentValues();
                values.put(LogsEntry.COL_TITLE, titleEdit.getText().toString());
                values.put(LogsEntry.COL_DETAILS, detailsEdit.getText().toString());
                LogSqlLiteOpenHelper.getInstance(this).updateTable(LogsEntry.TABLE_NAME,
                        values,LogsEntry._ID, new String [] {Long.toString(rowID)});
                Intent intent = new Intent();
                intent.putExtra("rowID", rowID);
                intent.putExtra(POSITION,position);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.log_menu_export:
                startActivity(ExportLogActivity.makeIntent(this, updateLogs));
                return true;
            case R.id.log_menu_share_location:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/*");
                String textToShare = "My location \n"
                        + " Latitude :" + sLatitude
                        + "\n - Longitude: " + sLongitude;
                shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                startActivity(shareIntent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        saveItem = mMenu.findItem(R.id.log_menu_save);
        extrasAdapter.setSaveItem(saveItem);
        mExportItem = mMenu.findItem(R.id.log_menu_export);
        if (updateLog)
            mExportItem.setVisible(true);
        else
            mExportItem.setVisible(false);

        mShareItem = mMenu.findItem(R.id.log_menu_share_location);
        checkForLocationInExtras();

        return super.onPrepareOptionsMenu(menu);
    }

    private void checkForLocationInExtras() {
        if (sLatitude == null && sLongitude == null)
            mShareItem.setVisible(false);
        else
            mShareItem.setVisible(true);
    }

    /**
     * sets the sLatitude and sLongitude. This values will be used to share
     * @param latitude
     * @param longitude
     */
    public static void setLocation(String latitude, String longitude){
        sLatitude = latitude;
        sLongitude = longitude;
        if (mShareItem != null)
            mShareItem.setVisible(true);
    }

    /**
     * disables the share button. Used to be called from the extrasadapter
     */
    public static void disableShareButton(){
        mShareItem.setVisible(false);
    }

    /**
     * set the time, day and date on the activity
     */
    private void updateDateTimeDay() {
        dayTxt.setText(Utils.getDay());

        dateTxt.setText(Utils.getDate());

        timeTxt.setText(Utils.getTime());
    }

    /**
     * create an intent passing as extra a logs object
     * @param context
     * @param log that will be passed as extra
     * @return the created intent
     */
    public static Intent makeIntentForNew(Context context, Logs log){
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra(LOGS_OBJECT, log);
        intent.putExtra(NEW_LOG,true);
        return intent;
    }


    /**
     * create an intent passing as extra a logs object and position
     * @param context
     * @param log that will be passed as extra
     * @return the created intent
     */
    public static Intent makeIntentForUpdateWithPosition(Context context, Logs log, int position){
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra(LOGS_OBJECT, log);
        intent.putExtra(POSITION, position);
        return intent;
    }

    /**
     * save the location to the DB
     * @param longtitude
     * @param latitude
     */
    private void saveLocationToDB(String longtitude, String latitude){
        String location = latitude + "|" + longtitude;
        Toast.makeText(this, "Latitude: " + latitude + " | Longtitude: "+ longtitude, Toast.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        values.put(ExtrasEntry.COL_LOG_ID, rowID);
        values.put(ExtrasEntry.COL_TYPE_ID, 4);  //Location
        values.put(ExtrasEntry.COL_URL, location);
        values.put(ExtrasEntry.COL_DATO, Utils.getDate());
        values.put(ExtrasEntry.COL_TIME, Utils.getTime());
        long extraID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(ExtrasEntry.TABLE_NAME, values);

        updateRecycleView();

        values.clear();
        values.put(LogsEntry.COL_LOCATION, "TRUE");
        int rows = LogSqlLiteOpenHelper.getInstance(this).updateTable(LogsEntry.TABLE_NAME,
                values, LogsEntry._ID, new String[] {Long.toString(rowID)});

        sLatitude = latitude;
        sLongitude = longtitude;
        checkForLocationInExtras();
    }

    @Override
    public void onLocationChanged(Location location) {
        String longitude = Double.toString(location.getLongitude());
        String latitude = Double.toString(location.getLatitude());
        saveLocationToDB(longitude,latitude);
        locationManager.removeUpdates(this);
        locationManager = null;
        saveItem.setVisible(true);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable the GPS." , Toast.LENGTH_SHORT).show();
    }
}

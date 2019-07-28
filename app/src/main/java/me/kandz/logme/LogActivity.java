package me.kandz.logme;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.Database.LogSqlLiteOpenHelper;

public class LogActivity extends AppCompatActivity {

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
    private String day;
    private String date;
    private String time;
    private long rowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        intializeActivity();
        createNewRecord();
    }

    /**
     * create a new record with only date, day and time and all the booleans false
     */
    private void createNewRecord() {
        ContentValues values = new ContentValues();
        values.put(LogsEntry.COL_DATO, date);
        values.put(LogsEntry.COL_DAY, day);
        values.put(LogsEntry.COL_TIME,time);
        values.put(LogsEntry.COL_IMAGE, "FALSE");
        values.put(LogsEntry.COL_VIDEO, "FALSE");
        values.put(LogsEntry.COL_SOUND, "FALSE");
        values.put(LogsEntry.COL_LOCATION, "FALSE");
        rowID = LogSqlLiteOpenHelper.getInstance(this).insertToTable(LogsEntry.TABLE_NAME,  values);
    }

    /**
     * get the view references and setonclick listeners to the buttons
     */
    private void intializeActivity() {
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

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AudioActivity.makeIntent(getApplicationContext()));
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        updateDateTimeDay();
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
                //TODO  update the record show a message and close the activity
                Toast.makeText(this, "Log updated.", Toast.LENGTH_LONG).show();
                finish();
                return true;
            case R.id.log_menu_cancel:
                //TODO get the list of the extras and delete them one by one
                LogSqlLiteOpenHelper.getInstance(this).deleteRecord(LogsEntry.TABLE_NAME, LogsEntry._ID, new String[] {Long.toString(rowID)});
                Toast.makeText(this, "Log cancelled and deleted.", Toast.LENGTH_LONG).show();
                finish();
                return true;
        }
        return false;
    }

    /**
     * set the time, day and date on the activity
     */
    private void updateDateTimeDay() {
        Date d = new Date();


        SimpleDateFormat sdf = new SimpleDateFormat("E");
        day = sdf.format(d);
        dayTxt.setText(day);

        sdf = new SimpleDateFormat("d MMM yyyy");
        date = sdf.format(d);
        dateTxt.setText(date);

        sdf = new SimpleDateFormat("HH:mm:ss");
        time = sdf.format(d);
        timeTxt.setText(time);
    }

    /**
     * create the intent for this activity
     * @param context
     * @return the intent
     */
    public static Intent makeIntent(Context context){
        return new Intent(context, LogActivity.class);
    }
}

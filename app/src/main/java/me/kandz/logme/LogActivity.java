package me.kandz.logme;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

public class LogActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_AUDIO_ACTIVITY = 1;
    public static final String LOGS_OBJECT = "logs_object";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Intent intent = getIntent();
        if (intent.getParcelableExtra(LOGS_OBJECT) !=null){
            updateLog = true;
            updateLogs = intent.getParcelableExtra(LOGS_OBJECT);
            intializeActivity();
            updateValues();
        } else {
            intializeActivity();
            createNewRecord();
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
    private void createNewRecord() {
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
                }
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
                finish();
                return true;
            case R.id.log_menu_cancel:
                if (!updateLog) {
                    //LogSqlLiteOpenHelper.getInstance(this).deleteRecord(LogsEntry.TABLE_NAME, LogsEntry._ID, new String[]{Long.toString(rowID)});
                    //Toast.makeText(this, "Log cancelled and deleted.", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Changes haven't been changed.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Changes haven't been changed.", Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;
        }
        return false;
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
     * create the intent for this activity
     * @param context
     * @return the intent
     */
    public static Intent makeIntentForUpdate(Context context){
        return new Intent(context, LogActivity.class);
    }

    /**
     * create an intent passing as extra a logs object
     * @param context
     * @param log that will be passed as extra
     * @return the created intent
     */
    public static Intent makeIntentForUpdate(Context context, Logs log){
        Intent intent = new Intent(context, LogActivity.class);
        intent.putExtra(LOGS_OBJECT, log);
        return intent;
    }


}

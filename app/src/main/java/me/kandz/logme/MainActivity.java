package me.kandz.logme;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.Database.LogSqlLiteOpenHelper;
import me.kandz.logme.Database.LogsAdapter;
import me.kandz.logme.Database.SwipeToDeleteLogs;
import me.kandz.logme.Utils.Logs;
import me.kandz.logme.Utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private LogsAdapter logsAdapter;
    private TextView emptyTextView;
    private ConstraintLayout constraintLayout;
    public static int requestCodeLogActivity = 1;
    private List<Logs> logs;
    private Long rowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dato = Utils.getDate();
                String day = Utils.getDay();
                String time =  Utils.getTime();
                ContentValues values = new ContentValues();
                values.put(LogsEntry.COL_DATO, dato);
                values.put(LogsEntry.COL_DAY, day);
                values.put(LogsEntry.COL_TIME, time);
                values.put(LogsEntry.COL_IMAGE, "FALSE");
                values.put(LogsEntry.COL_VIDEO, "FALSE");
                values.put(LogsEntry.COL_AUDIO, "FALSE");
                values.put(LogsEntry.COL_LOCATION, "FALSE");
                long rowID = LogSqlLiteOpenHelper.getInstance(getApplicationContext()).insertToTable(LogsEntry.TABLE_NAME,  values);
                Logs log = new Logs(
                        (int)rowID, "", "", dato, day, time, false, false, false, false);
                logs.add(0, log);
                logsAdapter.notifyItemInserted(0);
                startActivityForResult(LogActivity.makeIntentForUpdate(getApplicationContext(), log), requestCodeLogActivity);
            }
        });

        LogSqlLiteOpenHelper.getInstance(this).getReadableDatabase();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        constraintLayout = (ConstraintLayout) findViewById(R.id.logsConstraintLayout);
        initializeRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeLogActivity){
            if (resultCode == RESULT_OK){
                rowID = data.getLongExtra("rowID", 0);
                int position = data.getIntExtra("position", 0);
                updateOneLog(position);
            }
        }
    }

    /**
     * updates the log that added or updated with the rowID
     */
    private void updateOneLog(int position) {
        Logs log = LogSqlLiteOpenHelper.getInstance(this).getALog(rowID);
        logs.remove(position);
        logs.add(position, log);
        logsAdapter.notifyItemInserted(position);
        logsAdapter.notifyDataSetChanged();
        hideMsgShowRec();
    }



    /**
     * initialiaze the recycler view
     */
    private void initializeRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.logsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        logs = new ArrayList<>();
        Cursor cursor = LogSqlLiteOpenHelper.getInstance(this).getTable(LogsEntry.TABLE_NAME, LogsEntry._ID, "DESC");

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

        while (cursor.moveToNext()){

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

            logs.add(tmpLog);
        }

        logsAdapter = new LogsAdapter(this, logs, constraintLayout);

        //initiate itemtouchHelper and attach it to the recyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteLogs(logsAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(logsAdapter);
        if (logs.size() == 0){
            hideRecShowMsg();
        } else {
            hideMsgShowRec();
        }
    }


    /**
     * hides the recycler view and shows the empty message
     */
    private void hideRecShowMsg() {
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    /**
     * hides the empty Message and shows the recyclerView
     */
    private void hideMsgShowRec() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logsAdapter.notifyDataSetChanged();
       // initializeRecyclerView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logs) {
            // Show the logs list
        } else if (id == R.id.nav_images) {
            //show the images list
        } else if (id == R.id.nav_audio) {
            //show the audio list
        } else if (id == R.id.nav_videos) {
            //show the videos list
        } else if (id == R.id.nav_locations) {
            //show the location list
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

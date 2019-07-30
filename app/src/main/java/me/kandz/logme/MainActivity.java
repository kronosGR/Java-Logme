package me.kandz.logme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.Database.LogSqlLiteOpenHelper;
import me.kandz.logme.Database.LogsAdapter;
import me.kandz.logme.Utils.Logs;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private LogsAdapter logsAdapter;

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
               startActivity(LogActivity.makeIntentForUpdate(getApplicationContext()));
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

        initializeRecyclerView();
    }

    /**
     * initialiaze the recycler view
     */
    private void initializeRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.logsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Logs> logs = new ArrayList<>();
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

        logsAdapter = new LogsAdapter(this, logs);
        recyclerView.setAdapter(logsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

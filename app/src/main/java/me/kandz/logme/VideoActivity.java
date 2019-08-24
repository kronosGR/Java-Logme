package me.kandz.logme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "video_url";
    public static final int REQUEST_CODE_VIDEO = 345;
    public static final int REQUEST_CODE_VIDEO_CAPTURE = 200;
    private VideoView videoView;
    private ImageView recordVideo;
    private ImageView saveVideo;
    private ImageView playVideoView;
    private Uri uri1;
    private boolean forPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        if (intent.getStringExtra(VIDEO_URL) != null){
            forPlaying = true;
            initializeViews();
            recordVideo.setVisibility(View.GONE);
            enablePlayImageView();
            enableSaveImageView();
            saveVideo.setImageResource(R.drawable.ic_cancel_black_24dp);
            uri1 = Uri.parse(intent.getStringExtra(VIDEO_URL));
            videoView.setVideoURI(uri1);
        } else {
            initializeViews();
            checkPermissions();
        }


    }

    /**
     * check for permissions
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

            disableRecordImageView();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO} , REQUEST_CODE_VIDEO);
        }
        else {
            enableRecordeImageView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_VIDEO){
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                enableRecordeImageView();
            } else {
                Toast.makeText(this, "You must give permissions so the app can record a video.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * creates the output video file
     * @return
     */
    private File getOutputVideoFile(){
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/LogMe/VideoFiles");
        if (!file.exists())
            file.mkdirs();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File (file.getPath() + File.separator + "VIDEO_"+ timestamp+".mp4");
    }

    /**
     * initialize the views
     */
    private void initializeViews() {
        videoView = (VideoView) findViewById(R.id.videoView);

        playVideoView = (ImageView) findViewById(R.id.playVideoImageView);
        disablePlayImageView();
        playVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
            }
        });

        recordVideo = (ImageView) findViewById(R.id.recordImageView);
        disableRecordImageView();
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                uri1 = Uri.fromFile(getOutputVideoFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri1);
                startActivityForResult(intent, REQUEST_CODE_VIDEO_CAPTURE);
            }
        });

        saveVideo = (ImageView) findViewById(R.id.saveImageView);
        disableSaveImageView();
        saveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!forPlaying) {
                    videoView.stopPlayback();
                    Intent intent = new Intent();
                    intent.setData(uri1);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    videoView.stopPlayback();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_VIDEO_CAPTURE){
            if (resultCode == RESULT_OK){
                videoView.setVideoURI(data.getData());
                enablePlayImageView();
                enableSaveImageView();

            }
        }

    }


    /**
     * enable play button imageview
     */
    private void enablePlayImageView() {
        playVideoView.setClickable(true);
        playVideoView.setVisibility(View.VISIBLE);
    }


    /**
     * disable play button imageview
     */
    private void disablePlayImageView() {
        playVideoView.setClickable(false);
        playVideoView.setVisibility(View.GONE);
    }

    /**
     * enable save imageview
     */
    private void enableSaveImageView() {
        saveVideo.setVisibility(View.VISIBLE);
        saveVideo.setClickable(true);
    }

    /**
     * disable save imageview
     */
    private void disableSaveImageView() {
        saveVideo.setVisibility(View.GONE);
        saveVideo.setClickable(false);
    }

    /**
     * disable record imageview
     */
    private void disableRecordImageView() {
        recordVideo.setClickable(false);
        recordVideo.setAlpha(0.2f);
    }

    /**
     * enable record imageview
     */
    private void enableRecordeImageView() {
        recordVideo.setClickable(true);
        recordVideo.setAlpha(1.0f);
    }

    /**
     * Creates an intent to start this activity
     * @param context
     * @return the created intent
     */
    public static Intent makeIntent(Context context){
        return new Intent(context, VideoActivity.class);
    }

    /**
     * make intent to stant this intent and play a video
     * @param context
     * @param url the video url
     * @return the created intent
     */
    public static Intent makeIntentToPlayVideo(Context context, String url){
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VIDEO_URL, url);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extra_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.extra_menu_export) {

            //strict mode ignores the uri exposure
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, uri1.toString().substring(uri1.toString().lastIndexOf("/") +1 ));
            intent.putExtra(Intent.EXTRA_STREAM, uri1);
            startActivity(Intent.createChooser(intent, "Share Video"));
        }
        return true;
    }
}

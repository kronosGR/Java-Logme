package me.kandz.logme;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    public static final int REQUEST_AUDIO_RECORD_CODE = 123;
    public static final String AUDIO_URL = "audio_url";
    private Chronometer chronometer;
    private ImageView recordBtn;
    private boolean recording;
    private ImageView playBtn;
    private ImageView saveBtn;
    private MediaRecorder mediaRecorder;
    private String filename;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Intent intent = getIntent();
        if (intent.getStringExtra(AUDIO_URL) != null){
            filename = intent.getStringExtra(AUDIO_URL);
            //play the audio file
            initViewsForPlaying();
        }
        else{
            //open the activity for recording
            initViews();
            checkPermissionsForAudio();
        }
    }


    /**
     * check if record_audio, read_external_storaga and write_external_storage are granted
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionsForAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            disableRecordButton();
            requestPermissions(new String[] {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_AUDIO_RECORD_CODE);
        } else {
            enableRecordButton();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_RECORD_CODE) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                enableRecordButton();
            } else {
                Toast.makeText(this, "You must give the permissions so the app can record audio", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    /**
     * initialize the views
     */
    private void initViews() {

        chronometer = (Chronometer) findViewById(R.id.chronometerView);
        playBtn = (ImageView) findViewById(R.id.playImageView);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play the recorded audio
                if (!isPlaying && filename != null)
                    playAudio();
                else
                    stopAudio();
            }

        });
        saveBtn = (ImageView) findViewById(R.id.saveImageView);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.setData(Uri.parse(filename));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        recordBtn = (ImageView) findViewById(R.id.recordImageView);
        recordBtn.setVisibility(View.VISIBLE);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recording){
                    //stop recording
                    stopRecording();
                } else {
                    //start recording
                    startRecording();
                }
            }
        });
    }

    /**
     * initialize the view for playing
     */
    private void initViewsForPlaying() {
        chronometer = (Chronometer) findViewById(R.id.chronometerView);
        playBtn = (ImageView) findViewById(R.id.playImageView);
        playBtn.setVisibility(View.VISIBLE);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play the recorded audio
                if (!isPlaying && filename != null)
                    playAudio();
                else
                    stopAudio();
            }

        });

        saveBtn = (ImageView) findViewById(R.id.saveImageView);
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setImageResource(R.drawable.ic_cancel_black_24dp);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recordBtn = (ImageView) findViewById(R.id.recordImageView);
        recordBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * stop playing the audio
     */
    private void stopAudio() {

        try{
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        mediaPlayer = null;
        isPlaying = false;
        playBtn.setImageResource(R.drawable.ic_play__black_24dp);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());

    }

    /**
     * play the recorded audio
     */
    private void playAudio() {
        isPlaying = true;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filename);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        playBtn.setImageResource(R.drawable.ic_stop_black_24dp);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playBtn.setImageResource(R.drawable.ic_play__black_24dp);
                chronometer.stop();
                isPlaying = false;
            }
        });
    }

    /**
     * stop recordiong audio
     */
    private void stopRecording() {
        Toast.makeText(getApplicationContext(), "Recording stopped and saved", Toast.LENGTH_SHORT).show();
        enableRecordButton();
        enablePlaySaveButtons();

        try{
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        mediaRecorder = null;

        chronometer.stop();
        recording = false;

        Toast.makeText(getApplicationContext(), "You can play the recording or save it and return", Toast.LENGTH_SHORT).show();
    }

    /**
     * start recording audio
     */
    private void startRecording() {

        recording = true;
        Toast.makeText(getApplicationContext(), "Start recording... Click the microphone to stop.", Toast.LENGTH_SHORT).show();
        disableRecordButton();
        disablePlaySaveButtons();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        //Create the directory and the filename
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/LogMe/AudioFiles");
        if (!file.exists())
            file.mkdirs();

        filename = root.getAbsolutePath() + "/LogMe/AudioFiles/" +
                String.valueOf(System.currentTimeMillis()) + ".mp3";

        mediaRecorder.setOutputFile(filename);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        catch (IOException e){
            e.printStackTrace();
            filename = null;
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /**
     * enable play and save buttons
     */
    private void enablePlaySaveButtons(){
        playBtn.setVisibility(View.VISIBLE);
        playBtn.setClickable(true);
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setClickable(true);
    }

    /**
     * disable play and save buttons
     */
    private void disablePlaySaveButtons(){
        playBtn.setVisibility(View.INVISIBLE);
        playBtn.setClickable(false);
        saveBtn.setVisibility(View.INVISIBLE);
        saveBtn.setClickable(false);
    }

    /**
     * enable record button
     */
    private void enableRecordButton() {
        ColorStateList csl = AppCompatResources.getColorStateList(this, android.R.color.holo_blue_light);
        ImageViewCompat.setImageTintList(recordBtn, csl);
        recordBtn.setAlpha(1.0f);
        recordBtn.setImageResource(R.drawable.ic_mic_black_24dp);
    }

    /**
     * disable record button
     */
    private void disableRecordButton(){
        ColorStateList csl = AppCompatResources.getColorStateList(this, android.R.color.holo_red_light);
        ImageViewCompat.setImageTintList(recordBtn, csl);
        recordBtn.setImageResource(R.drawable.ic_mic_off_black_24dp);
    }

    /**
     * make the intent for this activity
     * @param context
     * @return the intent
     */
    public static Intent makeIntent(Context context){
        return new Intent(context, AudioActivity.class);
    }

    /**
     * make the intent to play for this activity
     * @param context
     * @param url
     * @return the intent
     */
    public static Intent makeIntentForPlaying(Context context, String url){
        Intent intent = new Intent(context, AudioActivity.class);
        intent.putExtra(AUDIO_URL, url);
        return intent;
    }
}

package me.kandz.logme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
    }

    /**
     * make the intent for this activity
     * @param context
     * @return the intent
     */
    public static Intent makeIntent(Context context){
        return new Intent(context, AudioActivity.class);
    }
}

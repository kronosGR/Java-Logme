package me.kandz.logme;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.kandz.logme.Utils.Utils;

public class ImageActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMAGE = 234;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 100;
    public static final String IMAGE_URL = "image_url";
    private ImageView previewImage;
    private ImageView takePictureImage;
    private ImageView saveImage;
    private Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        previewImage = (ImageView) findViewById(R.id.previewImg);
        takePictureImage = (ImageView) findViewById(R.id.takePictureimageView);
        saveImage = (ImageView) findViewById(R.id.saveImageView);

        Intent intent = getIntent();
        if (intent.getStringExtra(IMAGE_URL) != null) {
            //showing a picture
            file = Uri.parse(intent.getStringExtra(IMAGE_URL));

            takePictureImage.setVisibility(View.INVISIBLE);
            previewImage.setImageURI(file);
            //previewImage.setRotation(Utils.needRotation(file.getPath()));
            saveImage.setVisibility(View.VISIBLE);
            saveImage.setImageResource(R.drawable.ic_cancel_black_24dp);
            saveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            //taking a picture
            takePictureImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    saveImage.setVisibility(View.GONE);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = Uri.fromFile(getOutputMediaFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
                }
            });

            saveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setData(file);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            checkPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                previewImage.setImageURI(file);
                saveImage.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * Request permissions Camera and Write_External_Storage
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            takePictureImage.setAlpha(0.2f);
            takePictureImage.setClickable(false);
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_IMAGE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureImage.setAlpha(1.0f);
                takePictureImage.setClickable(true);
            } else {
                Toast.makeText(this, "You must give the permissions so the app can take a picture", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    /**
     * creates and returns the file to be saved the image
     *
     * @return the file
     */
    private static File getOutputMediaFile() {
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/LogMe/ImageFiles");
        if (!file.exists())
            file.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(file.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    /**
     * makes the intent for this activity
     *
     * @param context
     * @return the created intent
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, ImageActivity.class);
    }

    /**
     * make the intent for this activity to show an image
     *
     * @param context
     * @param url     the url for the image to be shown
     * @return the created intent
     */
    public static Intent makeIntentForShowing(Context context, String url) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(IMAGE_URL, url);
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
            intent.putExtra(Intent.EXTRA_TEXT, file.toString().substring(file.toString().lastIndexOf("/") +1 ));
            intent.putExtra(Intent.EXTRA_STREAM, file);
            startActivity(Intent.createChooser(intent, "Share Image"));
        }
        return true;
    }

}

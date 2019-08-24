package me.kandz.logme;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PdfPrint;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogSqlLiteOpenHelper;
import me.kandz.logme.Utils.Logs;
import me.kandz.logme.Utils.Utils;

public class ExportLogActivity extends AppCompatActivity {

    public static final String LOG = "LOG";
    public static final int PERMISSIONS_REQUEST_CODE = 123;
    private WebView mWebView;
    private ImageView mExportImageView;
    private Logs mLog;
    private String mHtmlDoc;
    private ImageView mPrintImageView;
    private static List<String> extraFiles = new ArrayList<>();
    private static String mFilename;
    private static Context mContext;
    private static String mPdfFilename;
    private int mPrevAddedPOS = -1;
    private File mLogMePath;
    private String mTmpFilename;
    private String mPdfFile;
    private File root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_log);

        checkExternalStoragePermisssion();
    }



    private void initializeActivity() {
        extraFiles.clear();
        mContext = getApplicationContext();


        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //saves the pdf file at Downloads location
                root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                mLogMePath = new File(root.getAbsolutePath()+ "/LogMe");
                String tmpTitle = mLog.getTitle()+ "_" + Utils.getDate() + "_" + Utils.getTime() ;
                mTmpFilename = Utils.trimFilename(tmpTitle);
                mPdfFile = Utils.trimFilename(mTmpFilename) + ".pdf";

                PrintAttributes attributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600 ,600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();

                PdfPrint pdfPrint = new PdfPrint(attributes);
                PrintDocumentAdapter adapter = mWebView.createPrintDocumentAdapter("Document: " + mLog.getTitle());
                pdfPrint.print(adapter, mLogMePath, mPdfFile, new PdfPrint.CallbackPrint() {

                    @Override
                    public void success(String path) {

                    }

                    @Override
                    public void onFailure() {

                    }
                });


            }
        });

        mExportImageView = (ImageView) findViewById(R.id.exportImageView);
        mExportImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //zip files
                exportToZip();
            }
        });

        mPrintImageView = (ImageView)findViewById(R.id.printImageView);
        mPrintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prints the log
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                String jobname = "Document: " + mLog.getTitle();
                PrintDocumentAdapter printDocumentAdapter = mWebView.createPrintDocumentAdapter(jobname);
                PrintAttributes attributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600,600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
                PrintJob printJob = printManager.print(jobname, printDocumentAdapter, attributes);

            }
        });

        Intent intent = getIntent();
        if (intent.getParcelableExtra(LOG) != null) {
            mLog = intent.getParcelableExtra(LOG);
            createPdfOnWebView();
        } else {
            finish();
        }
    }

    /**
     * export the extras to zip file
     */
    private void exportToZip() {
        mPdfFilename = mLogMePath + "/" + mPdfFile;
        if (mPrevAddedPOS != -1)
            extraFiles.remove(mPrevAddedPOS);
        extraFiles.add(mPdfFilename);
        mPrevAddedPOS = extraFiles.size() -1;
        File file = new File(root.getAbsolutePath() + "/" + mTmpFilename + ".zip");
        mFilename = file.getAbsolutePath();

        new bgOperation().execute(null,null,null);
    }


    /**
     * check for the persmissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkExternalStoragePermisssion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //ask permissions
            requestPermissions(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSIONS_REQUEST_CODE);
        } else {
            initializeActivity();
        }
    }

    /**
     * check if permissions accepted
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            if (permissions.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                initializeActivity();
            } else {
                Toast.makeText(this, "You need to give permissions so the app can export the files", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }


    /**
     * creates the webview content from the log
     */
    private void createPdfOnWebView() {
        mHtmlDoc = "<html><body>" +
                "<h1>" + mLog.getTitle() + "</h1><hr/>" +
                "<b><i>" + mLog.getDay() + " " + mLog.getDato() + " - " + mLog.getTime() + "</i></b><br/><hr/>" +
                "<h4>" + mLog.getDetails() + "</h4><hr/><hr/>" +
                "<h2>Attachments / Files</h2>";


        //Get image files
        Cursor imgCursor = LogSqlLiteOpenHelper.getInstance(this).getExtrasWithIdAndType(
                Integer.toString(mLog.getID()), "1");

        int urlPOS = imgCursor.getColumnIndex(ExtrasEntry.COL_URL);
        int datePOS = imgCursor.getColumnIndex(ExtrasEntry.COL_DATO);
        int timePOS = imgCursor.getColumnIndex(ExtrasEntry.COL_TIME);

        mHtmlDoc += "<h3>Image Files</h3>";
        if (imgCursor.getCount() > 0) {
            int imgCount = 1;
            while (imgCursor.moveToNext()) {

                int tmpPOS = imgCursor.getString(urlPOS).lastIndexOf("/");
                String url = imgCursor.getString(urlPOS).substring(tmpPOS + 1 );
                mHtmlDoc += "<b>" + imgCount + ". " + url +
                        "</b><br/>&#8195; &#8195; <i>" + imgCursor.getString(datePOS) + " - " + imgCursor.getString(timePOS) + "</><br/>";
                imgCount++;

                extraFiles.add(imgCursor.getString(urlPOS));
            }
        } else {
            mHtmlDoc += "&#8195; &#8195;<b>No Image Files</b>";
        }

        //Get audio files
        Cursor audioCursor = LogSqlLiteOpenHelper.getInstance(this).getExtrasWithIdAndType(
                Integer.toString(mLog.getID()), "2");

        urlPOS = audioCursor.getColumnIndex(ExtrasEntry.COL_URL);
        datePOS = audioCursor.getColumnIndex(ExtrasEntry.COL_DATO);
        timePOS = audioCursor.getColumnIndex(ExtrasEntry.COL_TIME);


        mHtmlDoc += "<hr/><h3>Audio Files</h3>";
        if (audioCursor.getCount() > 0) {
            int audioCount = 1;
            while (audioCursor.moveToNext()) {
                int tmpPOS = audioCursor.getString(urlPOS).lastIndexOf("/");
                String url = audioCursor.getString(urlPOS).substring(tmpPOS + 1 );
                mHtmlDoc += "<b>" + audioCount + ". " + url +
                        "</b><br/>&#8195; &#8195; <i>" + audioCursor.getString(datePOS) + " - " + audioCursor.getString(timePOS) + "</><br/>";
                audioCount++;

                extraFiles.add(audioCursor.getString(urlPOS));
            }
        } else {
            mHtmlDoc += "&#8195; &#8195;<b>No Audio Files</b>";
        }

        //Get Video files
        Cursor videoCursor = LogSqlLiteOpenHelper.getInstance(this).getExtrasWithIdAndType(
                Integer.toString(mLog.getID()), "3");

        urlPOS = videoCursor.getColumnIndex(ExtrasEntry.COL_URL);
        datePOS = videoCursor.getColumnIndex(ExtrasEntry.COL_DATO);
        timePOS = videoCursor.getColumnIndex(ExtrasEntry.COL_TIME);


        mHtmlDoc += "<hr/><h3>Video Files</h3>";
        if (videoCursor.getCount() > 0) {
            int videoCount = 1;
            while (videoCursor.moveToNext()) {
                int tmpPOS = videoCursor.getString(urlPOS).lastIndexOf("/");
                String url = videoCursor.getString(urlPOS).substring(tmpPOS + 1 );
                mHtmlDoc += "<b>" + videoCount + ". " + url +
                        "</b><br/>&#8195; &#8195; <i>" + videoCursor.getString(datePOS) + " - " + videoCursor.getString(timePOS) + "</><br/>";
                videoCount++;

                extraFiles.add(videoCursor.getString(urlPOS));
            }
        } else {
            mHtmlDoc += "&#8195; &#8195;<b>No Video Files</b>";
        }

        //Get Locations
        Cursor locationCursor = LogSqlLiteOpenHelper.getInstance(this).getExtrasWithIdAndType(
                Integer.toString(mLog.getID()), "4");

        urlPOS = locationCursor.getColumnIndex(ExtrasEntry.COL_URL);
        datePOS = locationCursor.getColumnIndex(ExtrasEntry.COL_DATO);
        timePOS = locationCursor.getColumnIndex(ExtrasEntry.COL_TIME);


        mHtmlDoc += "<hr/><h3>Locations</h3>";
        if (locationCursor.getCount() > 0) {
            int locationCount = 1;
            while (locationCursor.moveToNext()) {
                String locTmp = locationCursor.getString(urlPOS);
                int delPOS = locTmp.indexOf("|");
                String latitude = locTmp.substring(0,delPOS);
                String longitude = locTmp.substring(delPOS+1, locTmp.length());

                mHtmlDoc += "<b>" + locationCount + ".  Latitude: " + latitude+
                        "<br/> &#8195 Longitude: " + longitude +
                        "</b><br/>&#8195; &#8195; <i>" + locationCursor.getString(datePOS) + " - " + locationCursor.getString(timePOS) + "</><br/>";
                locationCount++;

            }
        } else {
            mHtmlDoc += "&#8195; &#8195;<b>No Locations</b>";
        }



//                +"</body></html>";

        mWebView.loadDataWithBaseURL(null, mHtmlDoc, "text/html", "UTF-8", null);
    }

    public static Intent makeIntent(Context context, Logs logs) {
        Intent intent = new Intent(context, ExportLogActivity.class);
        intent.putExtra(LOG, logs);
        return intent;
    }

    /**
     * asynctask operation for the zip file creation
     */
    private static class bgOperation extends AsyncTask<Void, Void, Boolean>{

        private Utils.MakeZip mZip;

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = Utils.zipFiles(extraFiles, mFilename);
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            if (success) {
//                mZip.closeZIP();
                Toast.makeText(mContext, "The logs is at: " + mFilename, Toast.LENGTH_SHORT).show();

                //strict mode ignores the uri exposure
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                //pdf file saved in download directory.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String location = "file://"+ mFilename.substring(0,mFilename.lastIndexOf("/" ))+ "/";
                Uri dir = Uri.parse( location);
                intent.setDataAndType(dir, "*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
            else
                Toast.makeText(mContext, "There was a problem with the operation" , Toast.LENGTH_SHORT).show();
        }
    }
}

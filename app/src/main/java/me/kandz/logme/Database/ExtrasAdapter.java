package me.kandz.logme.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import me.kandz.logme.AudioActivity;
import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.ImageActivity;
import me.kandz.logme.LogActivity;
import me.kandz.logme.MainActivity;
import me.kandz.logme.R;
import me.kandz.logme.Utils.Extras;
import me.kandz.logme.Utils.Logs;
import me.kandz.logme.VideoActivity;

public class ExtrasAdapter extends RecyclerView.Adapter<ExtrasAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Extras> extras;
    private Extras recentlyDeleted;
    private int recentlyDeletedPosition;
    private ConstraintLayout mConstraintLayout;
    private List<Logs> logs;
    private boolean fromLog = false;
    private MenuItem saveItem;
    private boolean undoclicked;

    public ExtrasAdapter(Context context, List<Extras> extras, ConstraintLayout constraintLayout) {
        this.context = context;
        this.extras = extras;
        mConstraintLayout = constraintLayout;
        inflater = LayoutInflater.from(context);
        fromLog = true;
    }

    public ExtrasAdapter(Context context, List<Extras> extras, ConstraintLayout constraintLayout, List<Logs> mLogs) {
        this.context = context;
        this.extras = extras;
        mConstraintLayout = constraintLayout;
        inflater = LayoutInflater.from(context);
        logs = mLogs;
    }

    /**
     * getter for the context
     * @return
     */
    public Context getContext() {
        return context;
    }

    public void setSaveItem(MenuItem saveItem) {
        this.saveItem = saveItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = inflater.inflate(R.layout.extras_list_item, viewGroup, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Extras extra = extras.get(i);
        switch (extra.getTypeID()) {
            case 1:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_image_black_24dp);
                break;
            case 2:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_mic_black_24dp);
                break;
            case 3:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_video_black_24dp);
                break;
            case 4:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_location_black_24dp);
                String[] location = extra.getUrl().split(Pattern.quote("|"));
                String latitude = location[0];
                String longitude = location[1];
                LogActivity.setLocation(latitude,longitude);
                break;
        }
        viewHolder.dateTimeTextView.setText(extra.getDato() + " - " + extra.getTime());
        viewHolder.extrasCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (extra.getTypeID()){
                    case 1: //IMAGE
                        context.startActivity(ImageActivity.makeIntentForShowing(context, extra.getUrl()));
                        break;
                    case 2: //AUDIO
                        context.startActivity(AudioActivity.makeIntentForPlaying(context, extra.getUrl()));
                        break;
                    case 3: //VIDEO
                        context.startActivity(VideoActivity.makeIntentToPlayVideo(context, extra.getUrl()));
                        break;
                    case 4: //LOCATION
                        String[] location = extra.getUrl().split(Pattern.quote("|"));
                        String latitude = location[0];
                        String longitude = location[1];
                        Uri locationUri = Uri.parse("geo:" + latitude + "," + longitude);
                        Intent intent = new Intent(Intent.ACTION_VIEW, locationUri);
                        //intent.setPackage("com.google.android.apps.maps");
                        if (intent.resolveActivity(context.getPackageManager()) != null){
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "You need a map application to view the location",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        if (!fromLog) {
            viewHolder.extrasCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int logID = extra.getLogID();
                    Logs tmpLog = LogSqlLiteOpenHelper.getInstance(context).getALog(logID);
                    int position = findLocation(tmpLog);
                    ((Activity) context).startActivityForResult(LogActivity.makeIntentForUpdateWithPosition(context,
                            tmpLog, position), MainActivity.requestCodeLogActivity);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return extras.size();
    }

    /**
     * delete an item with undo snackbar option.
     * @param position the position of the item to be deleted
     */
    public void deleteItem(int position) {
        saveItem.setVisible(false);

        final int finalPosition = position;
        recentlyDeleted = extras.get(position);
        recentlyDeletedPosition = position;
        extras.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar.make(mConstraintLayout, "UNDO",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("YES", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extras.add(recentlyDeletedPosition,
                        recentlyDeleted);
                notifyItemInserted(recentlyDeletedPosition);
                saveItem.setVisible(true);
                undoclicked = true;
            }
        });

        //when the snackbar autohides
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                if (!undoclicked) {
                    String filename = recentlyDeleted.getUrl();
                    String typeId = Integer.toString(recentlyDeleted.getTypeID());
                    int ID = recentlyDeleted.getLogID();

                    // delete form disk
                    File fileToDelete = new File(filename);
                    if (fileToDelete.exists())
                        fileToDelete.delete();

                    // delete from the database
                    LogSqlLiteOpenHelper.getInstance(context).deleteRecord(
                            ExtrasEntry.TABLE_NAME
                            , ExtrasEntry.COL_URL
                            , new String[]{filename}
                    );

                    //check the typeId and set the columnName for the delete operation
                    String columnName = "";
                    switch (typeId) {
                        case "1":
                            columnName = LogsEntry.COL_IMAGE;
                            break;
                        case "2":
                            columnName = LogsEntry.COL_AUDIO;
                            break;
                        case "3":
                            columnName = LogsEntry.COL_VIDEO;
                            break;
                        case "4":
                            columnName = LogsEntry.COL_LOCATION;
                            LogActivity.disableShareButton();
                            break;

                    }

                    ContentValues values = new ContentValues();
                    //check if more the same type
                    Cursor cursor = LogSqlLiteOpenHelper.getInstance(context).getExtrasWithIdAndType(
                            Integer.toString(ID), typeId);

                    int howMany = cursor.getCount();
                    if (howMany == 0) {
                        //set to false
                        values.put(columnName, "FALSe");
                        int rows = LogSqlLiteOpenHelper.getInstance(context).updateTable(LogsEntry.TABLE_NAME,
                                values, LogsEntry._ID, new String[]{Integer.toString(ID)});
                    } else {

                        values.put(columnName, "TRUE");
                        int rows = LogSqlLiteOpenHelper.getInstance(context).updateTable(LogsEntry.TABLE_NAME,
                                values, LogsEntry._ID, new String[]{Integer.toString(ID)});

                    }
                    saveItem.setVisible(true);
                }
            }
        });
        snackbar.show();

    }

    private int findLocation(Logs log){
        int position = -1;
        for (int i=0; i<logs.size(); i++){
            if (logs.get(i).getID() == log.getID())
                return i;
        }
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView typeImageView;
        public TextView dateTimeTextView;
        public CardView extrasCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            typeImageView = (ImageView) itemView.findViewById(R.id.typeImageView);
            dateTimeTextView = (TextView) itemView.findViewById(R.id.timeDateTtextView);
            extrasCardView = (CardView) itemView.findViewById(R.id.extrasCardView);
        }
    }
}

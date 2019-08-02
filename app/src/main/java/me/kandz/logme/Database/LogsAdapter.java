package me.kandz.logme.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import me.kandz.logme.Database.LogContract.ExtrasEntry;
import me.kandz.logme.Database.LogContract.LogsEntry;
import me.kandz.logme.LogActivity;
import me.kandz.logme.MainActivity;
import me.kandz.logme.R;
import me.kandz.logme.Utils.Logs;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<Logs> logs;
    private ConstraintLayout mContrsaintLayout; //snackbar needs it
    private Logs recentlyDeleted;
    private int recentlyDeletedPosition;
    private boolean undoClicked;

    public LogsAdapter(Context context, List<Logs> logs, ConstraintLayout constraintLayout) {
        this.context = context;
        this.logs = logs;
        layoutInflater = LayoutInflater.from(context);
        mContrsaintLayout = constraintLayout;
    }

    /**
     * returns the context, used in the SwipeToDeleteLogs
     * @return
     */
    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = layoutInflater.inflate(R.layout.logs_list_item, viewGroup, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final Logs log = logs.get(i);
        viewHolder.dateTextView.setText(log.getDato());
        viewHolder.dayTextView.setText(log.getDay());
        viewHolder.timeTextView.setText(log.getTime());
        viewHolder.titleTextView.setText(log.getTitle());

        if(log.getAudio()){
            viewHolder.audioImageView.setAlpha(1.0f);
        }else{
            viewHolder.audioImageView.setAlpha(0.2f);
        }

        if(log.getImage()){
            viewHolder.imageImageView.setAlpha(1.0f);
        }else{
            viewHolder.imageImageView.setAlpha(0.2f);
        }

        if(log.getVideo()){
            viewHolder.videoImageView.setAlpha(1.0f);
        }else{
            viewHolder.videoImageView.setAlpha(0.2f);
        }

        if(log.getLocation()){
            viewHolder.locationImageView.setAlpha(1.0f);
        }else{
            viewHolder.locationImageView.setAlpha(0.2f);
        }

        viewHolder.logsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)context).startActivityForResult(
                        LogActivity.makeIntentForUpdateWithPosition(context,log, viewHolder.getAdapterPosition()),
                        MainActivity.requestCodeLogActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    /**
     * delete item used by SwipetoDeleteLogs
     * @param position
     */
    public void deleteItem(final int position) {
        final int finalPosition = position;
        recentlyDeleted = logs.get(finalPosition);
        recentlyDeletedPosition = position;

        logs.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar =Snackbar.make(mContrsaintLayout, "UNDO", Snackbar.LENGTH_LONG);
        snackbar.setAction("YES", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logs.add(position, recentlyDeleted);
                notifyItemInserted(recentlyDeletedPosition);
                undoClicked = true;
            }
        });

        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                @SuppressLint("StaticFieldLeak")
                AsyncTask task = new AsyncTask() {

                    @Override
                    protected void onPreExecute() {
                        if (!undoClicked)
                            Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        int count = 0;
                        if (!undoClicked) {
                            //get all the extras for this log
                            Cursor cursor = LogSqlLiteOpenHelper.getInstance(context).getTableWithSelection(
                                    ExtrasEntry.TABLE_NAME
                                    , ExtrasEntry.COL_LOG_ID
                                    , new String[]{Long.toString(recentlyDeleted.getID())});

                            //delete the files for the extras
                            int urlPOS = cursor.getColumnIndex(ExtrasEntry.COL_URL);
                            while (cursor.moveToNext()) {
                                String url = cursor.getString(urlPOS);
                                File fileToDelete = new File(url);
                                if (fileToDelete.exists())
                                    fileToDelete.delete();
                                Log.d("MAIN", url);
                                //delete the extras from the DB
                                LogSqlLiteOpenHelper.getInstance(context).deleteRecord(
                                        ExtrasEntry.TABLE_NAME
                                        , ExtrasEntry.COL_LOG_ID
                                        , new String[]{Long.toString(recentlyDeleted.getID())});

                                count++;
                            }
                            // delete the log from the DB
                            LogSqlLiteOpenHelper.getInstance(context).deleteRecord(
                                    LogsEntry.TABLE_NAME
                                    , LogsEntry._ID
                                    , new String[]{Long.toString(recentlyDeleted.getID())});
                        }
                        return count;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        if (!undoClicked) {
                            Toast.makeText(context, "Log and " + o.toString() + " files have been deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                task.execute();
            }
        });
        snackbar.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView dayTextView;
        public TextView timeTextView;
        public TextView titleTextView;
        public ImageView audioImageView;
        public ImageView imageImageView;
        public ImageView videoImageView;
        public ImageView locationImageView;
        public CardView logsCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.date_logs_list_item);
            dayTextView = (TextView) itemView.findViewById(R.id.day_logs_list_item);
            timeTextView = (TextView) itemView.findViewById(R.id.time_logs_list_item);
            titleTextView = (TextView) itemView.findViewById(R.id.title_logs_list_item);
            audioImageView = (ImageView) itemView.findViewById(R.id.audio_logs_list_item);
            imageImageView = (ImageView) itemView.findViewById(R.id.image_logs_list_item);
            videoImageView = (ImageView) itemView.findViewById(R.id.video_logs_list_item);
            locationImageView = (ImageView) itemView.findViewById(R.id.locaion_logs_list_item);
            logsCardView = (CardView) itemView.findViewById(R.id.cardview_logs_list);
        }

    }
}

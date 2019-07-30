package me.kandz.logme.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.kandz.logme.LogActivity;
import me.kandz.logme.R;
import me.kandz.logme.Utils.Logs;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<Logs> logs;

    public LogsAdapter(Context context, List<Logs> logs) {
        this.context = context;
        this.logs = logs;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = layoutInflater.inflate(R.layout.logs_list_item, viewGroup, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
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
                context.startActivity(LogActivity.makeIntentForUpdate(context,log));
            }
        });
    }

    @Override
    public int getItemCount() {
        return logs.size();
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

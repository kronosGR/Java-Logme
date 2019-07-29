package me.kandz.logme.Database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.kandz.logme.R;
import me.kandz.logme.Utils.Extras;

public class ExtrasAdapter extends RecyclerView.Adapter<ExtrasAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<Extras> extras;

    public ExtrasAdapter(Context context, List<Extras> extras) {
        this.context = context;
        this.extras = extras;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = inflater.inflate(R.layout.extras_list_item, viewGroup, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Extras extra = extras.get(i);
        switch (extra.getTypeID()){
            case 1:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_image_black_24dp);
                break;
            case 2:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_mic_black_24dp);
                break;
            case 3:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_image_black_24dp);
                break;
            case 4:
                viewHolder.typeImageView.setImageResource(R.drawable.ic_location_black_24dp);
                break;
        }
        viewHolder.dateTimeTextView.setText(extra.getDato() + " - " +extra.getTime());
    }

    @Override
    public int getItemCount() {
        return extras.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView typeImageView;
        public TextView dateTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            typeImageView = (ImageView) itemView.findViewById(R.id.typeImageView);
            dateTimeTextView = (TextView) itemView.findViewById(R.id.timeDateTtextView);
        }
    }
}

package au.com.zacher.popularmovies.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Brad on 23/06/2015.
 */
public class DisplayItemViewHolder extends RecyclerView.ViewHolder {
    public int position;

    public DisplayItem item;

    public ImageView image;
    public TextView title;
    public TextView subtitle;

    public DisplayItemViewHolder(View item) {
        super(item);
    }
}
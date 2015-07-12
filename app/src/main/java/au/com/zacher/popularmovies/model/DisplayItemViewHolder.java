package au.com.zacher.popularmovies.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.Utilities;

/**
 * Created by Brad on 23/06/2015.
 */
public class DisplayItemViewHolder extends RecyclerView.ViewHolder {
    public DisplayItem item;

    public ImageView image;
    public TextView title;
    public TextView subtitle;

    public DisplayItemViewHolder(View item) {
        super(item);
        this.image = (ImageView)item.findViewById(R.id.display_item_image);
        /*this.title = (TextView)item.findViewById(R.id.display_item_title);
        this.subtitle = (TextView)item.findViewById(R.id.display_item_subtitle);*/
    }

    public void bind(Context context, DisplayItem item) {
        /*if (item.title != null) {
            this.title.setText(item.title);
        } else {
            this.title.setVisibility(View.GONE);
        }
        if (item.subtitle != null) {
            this.subtitle.setText(item.subtitle);
        } else {
            this.subtitle.setVisibility(View.GONE);
        }*/
        this.item = item;

        Utilities.backgroundLoadImage(context, item, this);
    }
}
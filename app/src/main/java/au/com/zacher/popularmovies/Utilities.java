package au.com.zacher.popularmovies;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;

/**
 * Created by Brad on 9/07/2015.
 */
public class Utilities {
    /**
     * Loads an image in the background and switches it in when it is ready
     * @param context the context to use
     * @param item the item containing the image url to load
     * @param holder the holder to load the image into
     * @param position the current position of the holder (if this changes before the image is loaded, the image will not be swapped in)
     */
    public static void backgroundLoadImage(Context context, final DisplayItem item, final DisplayItemViewHolder holder, final int position) {
        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_music_note_black_48dp));
        final ImageView backgroundLoadedImage = new ImageView(context);
        Picasso.with(context)
                .load(item.imageUrl)
                .error(R.drawable.ic_error_outline_black_48dp)
                .into(backgroundLoadedImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }

                    @Override
                    public void onError() {
                        Logger.e(R.string.log_image_load_error, item.imageUrl);
                        if (holder.position == position) {
                            holder.image.setImageDrawable(backgroundLoadedImage.getDrawable());
                        }
                    }
                });
    }
}

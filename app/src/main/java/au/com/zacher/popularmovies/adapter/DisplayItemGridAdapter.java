package au.com.zacher.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;

/**
 * Created by Brad on 9/07/2015.
 */
public abstract class DisplayItemGridAdapter<T> extends ArrayAdapter<DisplayItem> {
    public DisplayItemGridAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * Gets the {@link DisplayItemViewHolder} for the given view
     */
    public DisplayItemViewHolder getViewHolder(View view) {
        return (DisplayItemViewHolder)view.getTag();
    }

    /**
     * Constructs and adds a {@link DisplayItem} for the given item
     */
    public void addItem(T item) {
        /*List<Image> images = getItemImages(item);
        String url = null;
        if (!images.isEmpty()) {
            Image closestImage = images.get(0);

            // no point doing math if we've only got 1 image
            if (images.size() > 1) {
                // find the image that most closely matches our required size for efficiency
                float imageSize = getContext().getResources().getDimension(R.dimen.search_fragment_image_size);
                float minDifH = Float.MAX_VALUE;
                float minDifW = Float.MAX_VALUE;
                for (Image image : images) {
                    float difH = image.height - imageSize;
                    float difW = image.width - imageSize;

                    // we only want images bigger than our required size
                    if (difH > 0 && difW > 0) {
                        if (difH < minDifH || difW < minDifW) {
                            closestImage = image;
                            minDifH = difH;
                            minDifW = difW;
                        }
                    }
                }
            }

            url = closestImage.url;
        }

        DisplayItem displayItem = new DisplayItem(this.getItemId(item), url, this.getItemTitle(item), this.getItemSubtitle(item));
        this.add(displayItem);*/
    }

    /**
     * Constructs and adds a {@link DisplayItem} for each item in the {@link Collection}
     */
    public void addAllItems(Collection<? extends T> collection) {
        for (T item : collection) {
            this.addItem(item);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DisplayItem item = this.getItem(position);

        // use ViewHolder pattern to speedup performance
        final DisplayItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_display_item, parent, false);
            holder = new DisplayItemViewHolder(convertView);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.subtitle = (TextView)convertView.findViewById(R.id.subtitle);
            holder.image = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (DisplayItemViewHolder)convertView.getTag();
        }

        // bind the values to the view
        holder.position = position;
        holder.title.setText(item.title);
        holder.subtitle.setText(item.subtitle);
        holder.item = item;

        Utilities.backgroundLoadImage(this.getContext(), item, holder, position);

        return convertView;
    }

    /**
     * Attempts to launch the {@link android.app.Activity} defined by {@link DisplayItemGridAdapter#getClickActivityClass()} when an item is clicked
     * @param context the context to use
     * @param clickedView the view that was clicked on
     */
    public void onItemClick(Context context, View clickedView) {
        Class clickActivityClass = this.getClickActivityClass();
        if (clickActivityClass == null) {
            // do nothing if the implementor has not specified a click class
            return;
        }

        DisplayItemViewHolder v = (DisplayItemViewHolder)clickedView.getTag();
        String id = this.getItem(v.position).id;
        String title = this.getItem(v.position).title;
        if (id != null) {
            // open the required view
            Intent i = new Intent(context, clickActivityClass)
                    .putExtra(this.getIdIntentExtraString(), id)
                    .putExtra(this.getTitleIntentExtraString(), title);
            this.getContext().startActivity(i);
        }
    }

    /**
     * Gets the title text to display for an item
     */
    protected abstract String getItemTitle(T item);
    /**
     * Gets the subtitle text to display for an item
     */
    protected abstract String getItemSubtitle(T item);
    /**
     * Gets the list of {@link Image} for an item
     */
    //protected abstract List<Image> getItemImages(T item);
    /**
     * Gets the id for an item
     */
    protected abstract String getItemId(T item);
    /**
     * Gets the {@link Class} of the {@link android.app.Activity} to launch on click
     * @return null if no activity should be launched, otherwise the activity class
     */
    protected abstract Class getClickActivityClass();
    /**
     * Gets the unique string which identifies where the item id should be stored in the launched {@link Intent}
     * @return the unique string if required, null if {@link DisplayItemGridAdapter#getClickActivityClass()) returns null
     */
    protected abstract String getIdIntentExtraString();
    /**
     * Gets the unique string which identifies where the item's title should be stored in the launched {@link Intent}
     * @return the unique string if required, null if {@link DisplayItemGridAdapter#getClickActivityClass()) returns null
     */
    protected abstract String getTitleIntentExtraString();
}

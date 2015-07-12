package au.com.zacher.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collection;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;

/**
 * Created by Brad on 9/07/2015.
 */
public abstract class DisplayItemListAdapter<T> extends RecyclerView.Adapter<DisplayItemViewHolder> implements View.OnClickListener {
    private final int itemViewResourceId;
    private final Context context;

    private ArrayList<DisplayItem> itemList = new ArrayList<>();

    public DisplayItemListAdapter(Context context, int itemViewResourceId) {
        super();
        this.context = context;
        this.itemViewResourceId = itemViewResourceId;
    }

    /**
     * Gets the context of this adapter
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Constructs and adds a {@link DisplayItem} for the given item
     */
    @SuppressWarnings("unused")
    public void addItem(T item) {
        this.addItem(item, true);
    }
    private void addItem(T item, boolean notify) {
        String url = this.getItemImage(item);
        DisplayItem displayItem = new DisplayItem(this.getItemId(item), url, this.getItemTitle(item), this.getItemSubtitle(item));
        this.add(displayItem, notify);
    }
    private void add(DisplayItem item, boolean notify) {
        this.itemList.add(item);
        if (notify) {
            this.notifyItemInserted(this.itemList.size() - 1);
        }
    }

    /**
     * Constructs and adds a {@link DisplayItem} for each item in the {@link Collection}
     */
    public void addAllItems(Collection<? extends T> collection) {
        int start = this.itemList.size();
        for (T item : collection) {
            this.addItem(item, false);
        }
        int end = this.itemList.size() - 1;
        //this.notifyItemRangeChanged(start, end);
        this.notifyDataSetChanged();
    }

    /**
     * Clears the adapter's storage
     */
    public void clear() {
        this.itemList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Gets the item at the specified position
     */
    public DisplayItem getItem(int position) {
        return this.itemList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public DisplayItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemViewResourceId, parent, false);
        view.setOnClickListener(this);
        // enforce the correct size for the screen
        view.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.getPosterWidth(), Utilities.getPosterHeight()));
        return new DisplayItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DisplayItemViewHolder holder, int position) {
        DisplayItem item = this.getItem(position);

        // bind the values to the view
        holder.bind(this.getContext(), item);
    }

    @Override
    public void onClick(View v) {
        RecyclerView parent = (RecyclerView)v.getParent();
        this.onItemClick(parent.getChildLayoutPosition(v));
    }

    /**
     * Attempts to launch the {@link android.app.Activity} defined by {@link DisplayItemListAdapter#getClickActivityClass()} when an item is clicked
     */
    private void onItemClick(int position) {
        Class clickActivityClass = this.getClickActivityClass();
        if (clickActivityClass == null) {
            // do nothing if the implementor has not specified a click class
            return;
        }

        String id = this.getItem(position).id;
        String title = this.getItem(position).title;
        if (id != null) {
            // open the required view
            Intent i = new Intent(this.context, clickActivityClass)
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
     * Gets the list of image urls for an item
     */
    protected abstract String getItemImage(T item);
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
     * @return the unique string if required, null if {@link DisplayItemListAdapter#getClickActivityClass()) returns null
     */
    protected abstract String getIdIntentExtraString();
    /**
     * Gets the unique string which identifies where the item's title should be stored in the launched {@link Intent}
     * @return the unique string if required, null if {@link DisplayItemListAdapter#getClickActivityClass()) returns null
     */
    protected abstract String getTitleIntentExtraString();
}

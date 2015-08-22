/*
 * Copyright 2015 Brad Zacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.zacher.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.activity.ActivityBase;
import au.com.zacher.popularmovies.activity.fragment.FragmentBase;
import au.com.zacher.popularmovies.model.DisplayItem;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;

/**
 * Created by Brad on 9/07/2015.
 */
public abstract class DisplayItemListAdapter<T> extends RecyclerView.Adapter<DisplayItemViewHolder> implements View.OnClickListener {
    private final int itemViewResourceId;
    private final Context context;

    private final ArrayList<DisplayItem> itemList = new ArrayList<>();

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
        for (T item : collection) {
            this.addItem(item, false);
        }
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
        Class<? extends ActivityBase> clickActivityClass = this.getClickActivityClass();
        Class<? extends FragmentBase> clickFragmentClass = this.getClickFragmentClass();
        if (clickActivityClass == null && clickFragmentClass == null) {
            // do nothing if the implementor has not specified a click class
            return;
        }

        DisplayItem item = this.getItem(position);
        String id = item.id;
        String title = item.title;
        if (id != null) {
            ActivityBase parentActivity = ((ActivityBase)this.getContext());
            if (parentActivity.findViewById(R.id.movie_details_fragment) != null) {
                //noinspection TryWithIdenticalCatches
                try {
                    Method method = clickFragmentClass.getDeclaredMethod("newInstance", String.class, String.class, boolean.class);
                    FragmentBase fragment = (FragmentBase)method.invoke(null, id, title, false);
                    parentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_details_fragment, fragment)
                            .commit();
                }
                // these exception shouldn't happen because we are working off concrete implementations
                catch (NoSuchMethodException ignored) { }
                catch (IllegalAccessException ignored) { }
                catch (InvocationTargetException ignored) { }
            } else {
                // open the required view
                Intent i = new Intent(this.context, clickActivityClass)
                        .putExtra(this.getIdIntentExtraString(), id)
                        .putExtra(this.getTitleIntentExtraString(), title);
                this.getContext().startActivity(i);
            }
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
    protected abstract Class<? extends ActivityBase> getClickActivityClass();
    /**
     * Gets the {@link Class} of the {@link android.app.Activity} to launch on click
     * @return null if no activity should be launched, otherwise the activity class
     */
    protected abstract Class<? extends FragmentBase> getClickFragmentClass();
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

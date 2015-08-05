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
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.MovieDetailsActivity;
import au.com.zacher.popularmovies.activity.fragment.MovieDetailsFragment;
import au.com.zacher.popularmovies.model.DisplayItemViewHolder;
import au.com.zacher.popularmovies.model.SimpleMovie;

/**
 * Created by Brad on 12/07/2015.
 */
public class SimpleMovieListAdapter extends DisplayItemListAdapter<SimpleMovie> {
    public SimpleMovieListAdapter(Context context, int itemViewResourceId) {
        super(context, itemViewResourceId);
    }

    @Override
    protected String getItemTitle(SimpleMovie item) {
        return item.title;
    }

    @Override
    protected String getItemSubtitle(SimpleMovie item) {
        return null;
    }

    @Override
    protected String getItemImage(SimpleMovie item) {
        return item.poster_path;
    }

    @Override
    protected String getItemId(SimpleMovie item) {
        return String.valueOf(item.id);
    }

    @Override
    protected Class getClickActivityClass() {
        return MovieDetailsActivity.class;
    }

    @Override
    protected String getIdIntentExtraString() {
        return MovieDetailsFragment.KEY_MOVIE_ID;
    }

    @Override
    protected String getTitleIntentExtraString() {
        return MovieDetailsFragment.KEY_MOVIE_TITLE;
    }

    @Override
    public DisplayItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DisplayItemViewHolder view = super.onCreateViewHolder(parent, viewType);
        // enforce the correct size for the screen
        view.itemView.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.getPosterWidth(), Utilities.getPosterHeight()));
        return view;
    }
}

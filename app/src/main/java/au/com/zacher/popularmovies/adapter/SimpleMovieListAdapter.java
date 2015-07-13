package au.com.zacher.popularmovies.adapter;

import android.content.Context;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.MovieDetailsActivity;
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
        return MovieDetailsActivity.KEY_MOVIE_ID;
    }

    @Override
    protected String getTitleIntentExtraString() {
        return MovieDetailsActivity.KEY_MOVIE_TITLE;
    }
}

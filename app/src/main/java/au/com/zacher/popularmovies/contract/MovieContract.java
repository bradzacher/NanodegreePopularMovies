package au.com.zacher.popularmovies.contract;

import android.content.SyncStatusObserver;
import android.database.Cursor;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;
import retrofit.Callback;

/**
 * Created by Brad on 10/07/2015.
 */
public final class MovieContract {
    public static final String POPULAR_MOVIES_TYPE = "popular_movies";

    public static void getPopular(final Callback<SimpleMovie[]> callback) {
        if (Utilities.isConnected()) {
            Utilities.triggerSync(SyncAdapter.POPULAR_MOVIES_SYNC, new SyncStatusObserver() {
                @Override
                public void onStatusChanged(int which) {
                    loadFromProvider(callback);
                }
            });
        } else {
            loadFromProvider(callback);
        }
    }

    private static void loadFromProvider(Callback<SimpleMovie[]> callback) {
        ApiResultCacheHelper provider = new ApiResultCacheHelper();
        Cursor cur = provider.get(POPULAR_MOVIES_TYPE);

        if (cur.getCount() == 0) {
            callback.failure(null);
            return;
        }

        SimpleMovie[] results = (SimpleMovie[])ApiResultCacheEntry.getObjectFromRow(cur, SimpleMovie[].class);
        callback.success(results, null);
    }
}

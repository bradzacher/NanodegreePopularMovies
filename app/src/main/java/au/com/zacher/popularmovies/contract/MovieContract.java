package au.com.zacher.popularmovies.contract;

import android.database.Cursor;
import android.os.Bundle;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.Movie;
import au.com.zacher.popularmovies.sync.SyncAdapter;

/**
 * Created by Brad on 12/07/2015.
 */
public final class MovieContract {
    public static final String MOVIES_TYPE = "movie_";
    public static final String KEY_MOVIE_ID = "movie-id";

    public static void getMovie(final String id, final ContractCallback<Movie> callback) {
        // attempt to load from the provider first
        loadFromProvider(id, new ContractCallback<Movie>() {
            @Override
            public void success(Movie movies) {
                callback.success(movies);
            }

            @Override
            public void failure(Exception error) {
                // attempt to force a sync
                if (Utilities.isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MOVIE_ID, id);
                    SyncAdapter.immediateSync(SyncAdapter.SYNC_TYPE_GET_MOVIE, bundle, new ContractCallback<Boolean>() {
                        @Override
                        public void success(Boolean result) {
                            loadFromProvider(id, callback);
                        }

                        @Override
                        public void failure(Exception e) {
                            callback.failure(e);
                        }
                    });
                } else {
                    callback.failure(error);
                }
            }
        });
    }

    private static void loadFromProvider(String id, ContractCallback<Movie> callback) {
        ApiResultCacheHelper provider = new ApiResultCacheHelper();

        Cursor cursor = provider.get(MOVIES_TYPE + "_" + id);

        if (cursor == null || cursor.getCount() == 0) {
            callback.failure(new Exception("No data in provider"));
            return;
        }
        cursor.moveToFirst();

        // five minutes ago
        if (!ApiResultCacheEntry.isOlderThan(cursor, 1000L * 60L * 5L) || !Utilities.isConnected()) {
            Movie results = (Movie) ApiResultCacheEntry.getObjectFromRow(cursor, Movie.class);
            cursor.close();
            callback.success(results);
        } else {
            callback.failure(new Exception("Data too old"));
        }
    }
}

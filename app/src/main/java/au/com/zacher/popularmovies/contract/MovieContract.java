package au.com.zacher.popularmovies.contract;

import android.content.SyncStatusObserver;
import android.database.Cursor;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Brad on 10/07/2015.
 */
public final class MovieContract {
    public static final String POPULAR_MOVIES_TYPE = "popular_movies";

    public static void getPopularMovies(final ContractCallback<SimpleMovie[]> callback) {
        // attempt to load from the provider first
        loadFromProvider(new ContractCallback<SimpleMovie[]>() {
            @Override
            public void success(SimpleMovie[] simpleMovies) {
                callback.success(simpleMovies);
            }

            @Override
            public void failure(Exception error) {
                // attempt to force a sync
                if (Utilities.isConnected()) {
                    SyncAdapter.immediateSync(SyncAdapter.POPULAR_MOVIES_SYNC, new ContractCallback<Boolean>() {
                        @Override
                        public void success(Boolean result) {
                            loadFromProvider(callback);
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

    private static void loadFromProvider(ContractCallback<SimpleMovie[]> callback) {
        ApiResultCacheHelper provider = new ApiResultCacheHelper();
        Cursor cursor = provider.get(POPULAR_MOVIES_TYPE);

        if (cursor == null || cursor.getCount() == 0) {
            callback.failure(new Exception("No data in provider"));
            return;
        }
        cursor.moveToFirst();

        SimpleMovie[] results = (SimpleMovie[])ApiResultCacheEntry.getObjectFromRow(cursor, SimpleMovie[].class);

        cursor.close();

        callback.success(results);
    }
}

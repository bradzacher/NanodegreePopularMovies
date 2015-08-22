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

package au.com.zacher.popularmovies.contract;

import android.database.Cursor;
import android.os.Bundle;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.MovieVideo;
import au.com.zacher.popularmovies.model.MovieWithReleases;
import au.com.zacher.popularmovies.model.Review;
import au.com.zacher.popularmovies.sync.SyncAdapter;

/**
 * Created by Brad on 12/07/2015.
 */
public final class MovieContract {
    public static final String MOVIES_DB_TYPE = "movie_";
    public static final String REVIEWS_DB_TYPE = "reviews_";
    public static final String VIDEOS_DB_TYPE = "videos_";
    public static final String KEY_MOVIE_ID = "movie-id";

    private static final long fiveMinutesInMS = 1000L * 60L * 5L;

    public static void getMovie(final String id, final ContractCallback<MovieWithReleases> callback) {
        attemptLoadFromProviderThenInternet(id, MovieContract.MOVIES_DB_TYPE, SyncAdapter.SYNC_TYPE_GET_MOVIE, MovieWithReleases.class, callback);
    }

    public static void getReviews(final String id, final ContractCallback<Review[]> callback) {
        attemptLoadFromProviderThenInternet(id, MovieContract.REVIEWS_DB_TYPE, SyncAdapter.SYNC_TYPE_GET_MOVIE_REVIEWS, Review[].class, callback);
    }

    public static void getVideos(final String id, final ContractCallback<MovieVideo[]> callback) {
        attemptLoadFromProviderThenInternet(id, MovieContract.VIDEOS_DB_TYPE, SyncAdapter.SYNC_TYPE_GET_MOVIE_VIDEOS, MovieVideo[].class, callback);
    }

    private static <T> void attemptLoadFromProviderThenInternet(final String id, final String dbType, final int syncType, final Class tClass, final ContractCallback<T> callback) {
        loadFromProvider(id, dbType, tClass, new ContractCallback<T>() {
            @Override
            public void success(T result) {
                callback.success(result);
            }

            @Override
            public void failure(Exception error) {
                // attempt to force a sync
                if (Utilities.isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(MovieContract.KEY_MOVIE_ID, id);
                    SyncAdapter.immediateSync(syncType, bundle, new ContractCallback<Boolean>() {
                        @Override
                        public void success(Boolean result) {
                            loadFromProvider(id, dbType, tClass, callback);
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

    private static <T> void loadFromProvider(String id, String dbType, Class tClass, ContractCallback<T> callback) {
        ApiResultCacheHelper provider = new ApiResultCacheHelper();

        Cursor cursor = provider.get(dbType + "_" + id);

        if (cursor == null || cursor.getCount() == 0) {
            callback.failure(new Exception("No data in provider"));
            provider.close();
            cursor.close();
            return;
        }
        cursor.moveToFirst();

        // five minutes ago
        if (!ApiResultCacheEntry.isOlderThan(cursor, MovieContract.fiveMinutesInMS) || !Utilities.isConnected()) {
            //noinspection unchecked
            T results = (T) ApiResultCacheEntry.getObjectFromRow(cursor, tClass);
            callback.success(results);
        } else {
            callback.failure(new Exception("Data too old"));
        }
        provider.close();
        cursor.close();
    }
}

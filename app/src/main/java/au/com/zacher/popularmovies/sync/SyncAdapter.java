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

package au.com.zacher.popularmovies.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.util.HashMap;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.api.TheMovieDbApi;
import au.com.zacher.popularmovies.api.TheMovieDbService;
import au.com.zacher.popularmovies.api.results.PagedResults;
import au.com.zacher.popularmovies.api.results.RelatedPagedResults;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.DiscoverMoviesContract;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.MovieWithReleases;
import au.com.zacher.popularmovies.model.Review;
import au.com.zacher.popularmovies.model.SimpleMovie;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Brad on 11/07/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_TYPE_CONFIGURATION = 0;
    public static final int SYNC_TYPE_DISCOVER_MOVIES = 1;
    public static final int SYNC_TYPE_GET_MOVIE = 2;
    public static final int SYNC_TYPE_GET_MOVIE_REVIEWS = 3;

    public static final String KEY_SYNC_TYPE = "sync-type";
    public static final String KEY_DISCOVER_FILTER = "discover-filter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        int type = extras.getInt(KEY_SYNC_TYPE);
        SyncAdapter.immediateSync(type, extras, null);
    }

    public static void immediateSync(final int type, Bundle extras, final ContractCallback<Boolean> callback) {
        String stringType = "unknown";
        switch (type) {
            case SyncAdapter.SYNC_TYPE_CONFIGURATION:
                stringType = "Configuration";
                break;

            case SyncAdapter.SYNC_TYPE_DISCOVER_MOVIES:
                stringType = "Popular Movies";
                break;

            case SyncAdapter.SYNC_TYPE_GET_MOVIE:
                stringType = "Get Movie";
                break;

            case SyncAdapter.SYNC_TYPE_GET_MOVIE_REVIEWS:
                stringType = "Get Movie Reviews";
                break;
        }
        Logger.v(R.string.log_sync_start, stringType);
        final String finalStringType = stringType;

        switch (type) {
            case SYNC_TYPE_CONFIGURATION:
            {
                TheMovieDbApi<TheMovieDbService.ConfigurationService> api = new TheMovieDbApi<>(TheMovieDbService.ConfigurationService.class);
                api.service.get(new Callback<Configuration>() {
                    @Override
                    public void success(Configuration configuration, Response response) {
                        SyncAdapter.LogSyncEnd(finalStringType);

                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        db.add(ConfigurationContract.TYPE, configuration);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        SyncAdapter.LogApiError(error);

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;

            case SYNC_TYPE_DISCOVER_MOVIES:
            {
                String mostPopular = Utilities.getString(R.string.pref_label_discovery_sort_order_most_popular);
                // set the sort order
                HashMap<String, Object> map = new HashMap<>();
                final boolean isMostPopular = Utilities.getPreference(R.string.pref_discovery_sort_order, R.string.pref_default_discovery_sort_order).equals(mostPopular);
                if (isMostPopular) {
                    map.put("sort_by", "popularity.desc");
                } else {
                    map.put("sort_by", "vote_average.desc");
                    map.put("vote_count.gte", "100"); // just to stop the movies that have only been voted on a few times
                }

                TheMovieDbApi<TheMovieDbService.DiscoverService> api = new TheMovieDbApi<>(TheMovieDbService.DiscoverService.class);
                api.service.getMovieList(map, new Callback<PagedResults<SimpleMovie>>() {
                    @Override
                    public void success(PagedResults<SimpleMovie> simpleMoviePagedResults, Response response) {
                        SyncAdapter.LogSyncEnd(finalStringType);

                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        String type = (isMostPopular) ? DiscoverMoviesContract.DISCOVER_MOVIES_MOST_POPULAR_TYPE : DiscoverMoviesContract.DISCOVER_MOVIES_HIGHEST_RATED_TYPE;

                        db.add(type, simpleMoviePagedResults.results);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        SyncAdapter.LogApiError(error);

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;

            case SYNC_TYPE_GET_MOVIE:
            {
                final String id = extras.getString(MovieContract.KEY_MOVIE_ID);

                TheMovieDbApi<TheMovieDbService.MoviesService> api = new TheMovieDbApi<>(TheMovieDbService.MoviesService.class);
                api.service.getMovieWithReleases(id, new Callback<MovieWithReleases>() {
                    @Override
                    public void success(MovieWithReleases movie, Response response) {
                        SyncAdapter.LogSyncEnd(finalStringType);

                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        String type = MovieContract.MOVIES_DB_TYPE + "_" + id;

                        db.add(type, movie);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        SyncAdapter.LogApiError(error);

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;

            case SYNC_TYPE_GET_MOVIE_REVIEWS:
            {
                final String id = extras.getString(MovieContract.KEY_MOVIE_ID);

                TheMovieDbApi<TheMovieDbService.MoviesService> api = new TheMovieDbApi<>(TheMovieDbService.MoviesService.class);
                api.service.getMovieReviews(id, new Callback<RelatedPagedResults<Review>>() {
                    @Override
                    public void success(RelatedPagedResults<Review> reviewRelatedPagedResults, Response response) {
                        SyncAdapter.LogSyncEnd(finalStringType);

                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        String type = MovieContract.REVIEWS_DB_TYPE + "_" + id;

                        db.add(type, reviewRelatedPagedResults.results);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        SyncAdapter.LogApiError(error);

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;
        }
    }

    private static void LogSyncEnd(String type) {
        Logger.d(R.string.log_sync_end, type);
    }
    private static void LogApiError(RetrofitError error) {
        Logger.e(R.string.log_api_error, error.getUrl(), error.getResponse().getStatus(), error.getMessage());
    }
}

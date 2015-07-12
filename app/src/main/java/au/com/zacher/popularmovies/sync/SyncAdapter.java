package au.com.zacher.popularmovies.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.api.TheMovieDbApi;
import au.com.zacher.popularmovies.api.TheMovieDbService;
import au.com.zacher.popularmovies.api.results.PagedResults;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.model.SimpleMovie;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Brad on 11/07/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String SYNC_TYPE_BUNDLE_KEY = "sync-type";
    public static final int CONFIGURATION_SYNC = 0;
    public static final int POPULAR_MOVIES_SYNC = 1;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        int type = extras.getInt(SYNC_TYPE_BUNDLE_KEY);
        SyncAdapter.immediateSync(type, null);
    }

    public static void immediateSync(int type, final ContractCallback<Boolean> callback) {
        switch (type) {
            case CONFIGURATION_SYNC:
            {
                TheMovieDbApi<TheMovieDbService.ConfigurationService> api = new TheMovieDbApi<>(TheMovieDbService.ConfigurationService.class);
                api.service.get(new Callback<Configuration>() {
                    @Override
                    public void success(Configuration configuration, Response response) {
                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        db.add(ConfigurationContract.TYPE, configuration);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.e(R.string.log_api_error, error.getUrl(), error.getResponse().getStatus(), error.getMessage());

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;

            case POPULAR_MOVIES_SYNC:
            {
                TheMovieDbApi<TheMovieDbService.MoviesService> api = new TheMovieDbApi<>(TheMovieDbService.MoviesService.class);
                api.service.getPopular(new Callback<PagedResults<SimpleMovie>>() {
                    @Override
                    public void success(PagedResults<SimpleMovie> simpleMoviePagedResults, Response response) {
                        ApiResultCacheHelper db = new ApiResultCacheHelper();
                        db.add(MovieContract.POPULAR_MOVIES_TYPE, simpleMoviePagedResults.results);

                        if (callback != null) {
                            callback.success(true);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Logger.e(R.string.log_api_error, error.getUrl(), error.getResponse().getStatus(), error.getMessage());

                        if (callback != null) {
                            callback.failure(error);
                        }
                    }
                });
            }
            break;
        }
    }
}

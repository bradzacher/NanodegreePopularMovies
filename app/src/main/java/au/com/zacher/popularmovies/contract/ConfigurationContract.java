package au.com.zacher.popularmovies.contract;

import android.content.SyncStatusObserver;
import android.database.Cursor;

import com.google.gson.Gson;


import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.sync.SyncAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Brad on 10/07/2015.
 */
public final class ConfigurationContract {
    private static Configuration instance;
    public static final String TYPE = "configuration";

    public static Configuration getInstance() {
        return instance;
    }

    /**
     * Loads the config from the internet, or from the DB if required
     */
    public static void getConfig(final ContractCallback<Configuration> callback) {
        loadConfigFromProvider(new ContractCallback<Configuration>() {
            @Override
            public void success(Configuration configuration) {
                callback.success(configuration);
            }

            @Override
            public void failure(Exception error) {
                if (Utilities.isConnected() && instance == null) {
                    SyncAdapter.immediateSync(SyncAdapter.CONFIGURATION_SYNC, new ContractCallback<Boolean>() {
                        @Override
                        public void success(Boolean result) {
                            loadConfigFromProvider(callback);
                        }

                        @Override
                        public void failure(Exception e) {
                            callback.failure(e);
                        }
                    });
                } else {
                    callback.failure(new Exception("No internet connection"));
                }
            }
        });
    }

    private static void loadConfigFromProvider(ContractCallback<Configuration> callback) {
        if (instance == null) {
            ApiResultCacheHelper db = new ApiResultCacheHelper();
            Cursor cursor = db.get(TYPE);

            if (cursor == null || cursor.getCount() == 0) {
                callback.failure(new Exception("No data in provider"));
                return;
            }
            cursor.moveToFirst();

            instance = (Configuration)ApiResultCacheEntry.getObjectFromRow(cursor, Configuration.class);

            cursor.close();
        }
        callback.success(instance);
    }
}

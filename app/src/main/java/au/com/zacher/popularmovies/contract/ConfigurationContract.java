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

/**
 * Created by Brad on 10/07/2015.
 */
public final class ConfigurationContract {
    private static Configuration instance;
    public static final String TYPE = "configuration";

    public static Configuration getConfiguration() {
        return instance;
    }

    /**
     * Loads the config from the internet, or from the DB if required
     */
    public static void initialConfigLoad(final Callback<Configuration> callback) {
        if (Utilities.isConnected() && instance == null) {
            Utilities.triggerSync(SyncAdapter.CONFIGURATION_SYNC, new SyncStatusObserver() {
                @Override
                public void onStatusChanged(int which) {
                    loadConfigFromProvider(callback);
                }
            });
        } else {
            loadConfigFromProvider(callback);
        }
    }

    private static void loadConfigFromProvider(Callback<Configuration> callback) {
        if (instance == null) {
            ApiResultCacheHelper db = new ApiResultCacheHelper();
            Cursor cursor = db.get(TYPE);

            if (cursor == null) {
                callback.failure(null);
                return;
            }
            cursor.moveToFirst();

            Gson gson = new Gson();
            instance = gson.fromJson(cursor.getString(cursor.getColumnIndex(ApiResultCacheEntry.COLUMN_JSON.name)), Configuration.class);
            cursor.close();
        }
        callback.success(instance, null);
    }
}

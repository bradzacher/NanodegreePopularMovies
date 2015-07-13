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
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.sync.SyncAdapter;

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
                    SyncAdapter.immediateSync(SyncAdapter.SYNC_TYPE_CONFIGURATION, Bundle.EMPTY, new ContractCallback<Boolean>() {
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

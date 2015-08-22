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

package au.com.zacher.popularmovies.api;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import au.com.zacher.popularmovies.Utilities;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;

/**
 * Created by Brad on 10/07/2015.
 */
public class TheMovieDbApi<T> {
    public static final String ENDPOINT = "http://api.themoviedb.org/3/";

    public final T service;

    public static Configuration configuration;
    static {
        TheMovieDbApi<TheMovieDbService.ConfigurationService> api = new TheMovieDbApi<>(TheMovieDbService.ConfigurationService.class);
        api.service.get(new Callback<Configuration>() {
            @Override
            public void success(Configuration configuration, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public TheMovieDbApi(Class<T> type) {
        Executor httpExecutor = Executors.newSingleThreadExecutor();
        MainThreadExecutor callbackExecutor = new MainThreadExecutor();

        this.service = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setExecutors(httpExecutor, callbackExecutor)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setRequestInterceptor(new Interceptor())
                .build()
                .create(type);


    }

    private class Interceptor implements RequestInterceptor {
        /**
         * Intercepts the http request to add the api_key
         */
        @Override
        public void intercept(RequestFacade request) {
            request.addQueryParam("api_key", Utilities.getApiKey());
        }
    }
}

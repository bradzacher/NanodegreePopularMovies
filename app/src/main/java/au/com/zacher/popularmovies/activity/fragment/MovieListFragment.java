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

package au.com.zacher.popularmovies.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Arrays;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.ViewState;
import au.com.zacher.popularmovies.adapter.SimpleMovieListAdapter;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.DiscoverMoviesContract;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;
import butterknife.Bind;

/**
 * Created by Brad on 2/08/2015.
 */
public class MovieListFragment extends FragmentBase implements Spinner.OnItemSelectedListener {
    @Bind(R.id.movie_grid) protected RecyclerView movieGrid;
    private SimpleMovieListAdapter movieGridAdapter;
    private boolean initialLoadDone;

    /**
     * This shouldn't be called anywhere publicly
     */
    public MovieListFragment() {
        super();
    }

    public static MovieListFragment newInstance() {
        return new MovieListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View retView = super.onCreateView(R.layout.fragment_movie_list, inflater, container, savedInstanceState);

        Logger.logActionCreate("MainActivity");

        // setup our periodic syncs
        Utilities.addPeriodicSync(SyncAdapter.SYNC_TYPE_CONFIGURATION, Bundle.EMPTY, 1, Utilities.SyncInterval.DAY);

        // fetch the pieces of the view
        this.movieGridAdapter = new SimpleMovieListAdapter(this.parent, R.layout.fragment_single_movie_poster);

        // setup the display grid
        this.movieGrid.setHasFixedSize(false);
        this.movieGrid.setAdapter(this.movieGridAdapter);
        this.movieGrid.setLayoutManager(new GridLayoutManager(this.parent, 2));

        // start the load
        this.configurationLoad();

        return retView;
    }
    private void configurationLoad() {
        this.setViewState(ViewState.IN_PROGRESS);

        ConfigurationContract.getConfig(new ContractCallback<Configuration>() {
            @Override
            public void success(Configuration configuration) {
                Utilities.initFromConfig(configuration);

                // trigger the popular movies load
                MovieListFragment.this.popularMoviesLoad();
            }

            @Override
            public void failure(Exception error) {
                // let the user know that we couldn't get any configuration (this should only ever happen if there is no internet connection on first load)
                MovieListFragment.this.setViewState(ViewState.ERROR);

                // and give them the option to retry
                MovieListFragment.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieListFragment.this.configurationLoad();
                    }
                });
            }
        });
    }
    private void popularMoviesLoad() {
        this.setViewState(ViewState.IN_PROGRESS);
        this.movieGridAdapter.clear();

        DiscoverMoviesContract.getDiscoverMovies(new ContractCallback<SimpleMovie[]>() {
            @Override
            public void success(SimpleMovie[] movies) {
                // show the list and add the loaded items
                MovieListFragment.this.setViewState(ViewState.SUCCESS);
                MovieListFragment.this.movieGridAdapter.addAllItems(Arrays.asList(movies));

                MovieListFragment.this.initialLoadDone = true;
            }

            @Override
            public void failure(Exception error) {
                // let the user know that we couldn't get any configuration (this should only ever happen if there is no internet connection on first load)
                MovieListFragment.this.setViewState(ViewState.ERROR);

                // and give them the option to retry
                MovieListFragment.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieListFragment.this.popularMoviesLoad();
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // this will attempt to
        if (!this.initialLoadDone) {
            return;
        }

        String item = (String)parent.getItemAtPosition(position);
        String mostPopular = this.getString(R.string.pref_label_discovery_sort_order_most_popular);
        String prefValue;
        if (item.equals(mostPopular)) {
            prefValue = this.getString(R.string.pref_label_discovery_sort_order_most_popular);
        } else {
            prefValue = this.getString(R.string.pref_label_discovery_sort_order_highest_rated);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.parent);
        prefs.edit().putString(this.getString(R.string.pref_discovery_sort_order), prefValue).apply();

        // reload the data
        this.popularMoviesLoad();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public View getMainViewItem() {
        return this.movieGrid;
    }
}

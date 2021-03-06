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
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
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
import au.com.zacher.popularmovies.data.helper.FavouriteHelper;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;
import butterknife.Bind;

/**
 * Created by Brad on 2/08/2015.
 */
public class MovieListFragment extends FragmentBase implements Spinner.OnItemSelectedListener {
    @Bind(R.id.movie_grid)      protected RecyclerView movieGrid;
    @Bind(R.id.no_favourites)   protected RelativeLayout noFavouritesText;

    private SimpleMovieListAdapter movieGridAdapter;
    private boolean initialLoadDone;

    /**
     * This shouldn't be called anywhere publicly
     */
    public MovieListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View retView = super.onCreateView(R.layout.fragment_movie_list, inflater, container, savedInstanceState);

        Logger.logActionCreate("MainActivity");

        // setup our periodic syncs
        Utilities.addPeriodicSync(SyncAdapter.SYNC_TYPE_CONFIGURATION, Bundle.EMPTY, 1, Utilities.SyncInterval.DAY);

        // fetch the pieces of the view
        this.movieGridAdapter = new SimpleMovieListAdapter(this.parent, R.layout.fragment_single_movie_poster);

        // figure out the number of cols we want
        if (Utilities.isLandscape()) {
            Utilities.setListColumnCount(3);
        } else {
            Utilities.setListColumnCount(2);
        }

        // setup the display grid
        this.movieGrid.setHasFixedSize(false);
        this.movieGrid.setAdapter(this.movieGridAdapter);
        this.movieGrid.setLayoutManager(new GridLayoutManager(this.parent, Utilities.getListColumnCount()));

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
        this.noFavouritesText.setVisibility(View.GONE);

        if (!Utilities.getPreference(R.string.pref_discovery_sort_order, R.string.pref_default_discovery_sort_order).equals(this.getString(R.string.pref_label_discovery_sort_order_favourites))) {
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
        } else {
            MovieListFragment.this.setViewState(ViewState.SUCCESS);
            MovieListFragment.this.initialLoadDone = true;

            // build the list from the db of favourites
            FavouriteHelper db = new FavouriteHelper();
            ArrayList<String[]> favourites = db.getAllFavourites();
            if (favourites == null) {
                this.noFavouritesText.setVisibility(View.VISIBLE);
                return;
            }

            ArrayList<SimpleMovie> movies = new ArrayList<>(favourites.size());
            for (String[] fav : favourites) {
                SimpleMovie m = new SimpleMovie();
                m.id = Integer.parseInt(fav[0]);
                m.poster_path = fav[1];
                movies.add(m);
            }
            MovieListFragment.this.movieGridAdapter.addAllItems(movies);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // because we set the selected item during load we don't want to run this listener during the load
        if (!this.initialLoadDone) {
            return;
        }

        String item = (String)parent.getItemAtPosition(position);
        final String mostPopular = this.getString(R.string.pref_label_discovery_sort_order_most_popular);
        final String highestRated = this.getString(R.string.pref_label_discovery_sort_order_highest_rated);
        final String favourite = this.getString(R.string.pref_label_discovery_sort_order_favourites);
        String prefValue;
        if (item.equals(mostPopular)) {
            prefValue = mostPopular;
        } else if (item.equals(highestRated)) {
            prefValue = highestRated;
        } else if (item.equals(favourite)) {
            prefValue = favourite;
        } else {
            // this shouldn't happen
            return;
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

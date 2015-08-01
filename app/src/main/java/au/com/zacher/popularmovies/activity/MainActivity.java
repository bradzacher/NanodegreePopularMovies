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

package au.com.zacher.popularmovies.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;
import android.widget.Spinner;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.adapter.SimpleMovieListAdapter;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.DiscoverMoviesContract;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;


public class MainActivity extends ActivityBase implements Toolbar.OnMenuItemClickListener, Spinner.OnItemSelectedListener {
    private RecyclerView movieGrid;
    private SimpleMovieListAdapter movieGridAdapter;
    private boolean initialLoadDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = false;
        super.onCreate(savedInstanceState, options, R.layout.activity_main);

        Logger.logActionCreate("MainActivity");

        // setup our periodic syncs
        Utilities.addPeriodicSync(SyncAdapter.SYNC_TYPE_CONFIGURATION, Bundle.EMPTY, 1, Utilities.SyncInterval.DAY);

        // fetch the pieces of the view
        this.movieGrid = (RecyclerView)this.findViewById(R.id.movie_grid);
        this.movieGridAdapter = new SimpleMovieListAdapter(this, R.layout.fragment_display_item);

        // setup the display grid
        this.movieGrid.setHasFixedSize(false);
        this.movieGrid.setAdapter(this.movieGridAdapter);
        this.movieGrid.setLayoutManager(new GridLayoutManager(this, 2));

        // start the load
        this.configurationLoad();
    }
    private void configurationLoad() {
        this.setViewState(ViewState.IN_PROGRESS);

        ConfigurationContract.getConfig(new ContractCallback<Configuration>() {
            @Override
            public void success(Configuration configuration) {
                Utilities.initFromConfig(configuration);

                // trigger the popular movies load
                MainActivity.this.popularMoviesLoad();
            }

            @Override
            public void failure(Exception error) {
                // let the user know that we couldn't get any configuration (this should only ever happen if there is no internet connection on first load)
                MainActivity.this.setViewState(ViewState.ERROR);

                // and give them the option to retry
                MainActivity.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.configurationLoad();
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
                MainActivity.this.setViewState(ViewState.SUCCESS);
                MainActivity.this.movieGridAdapter.addAllItems(Arrays.asList(movies));

                MainActivity.this.initialLoadDone = true;
            }

            @Override
            public void failure(Exception error) {
                // let the user know that we couldn't get any configuration (this should only ever happen if there is no internet connection on first load)
                MainActivity.this.setViewState(ViewState.ERROR);

                // and give them the option to retry
                MainActivity.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.popularMoviesLoad();
                    }
                });
            }
        });
    }

    @Override
    public View getMainViewItem() {
        return this.movieGrid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);

        this.getLayoutInflater().inflate(R.layout.fragment_sort_order_menu, this.toolbar);
        Spinner spinner = (Spinner)this.toolbar.findViewById(R.id.sort_order_menu);
        // set the selected item to be the user preference
        String item0 = (String)spinner.getItemAtPosition(0);
        String currentPref = Utilities.getPreference(R.string.pref_discovery_sort_order, R.string.pref_default_discovery_sort_order);
        if (item0.equals(currentPref)) {
            spinner.setSelection(0, false);
        } else {
            spinner.setSelection(1, false);
        }
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "MainActivity");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(this.getString(R.string.pref_discovery_sort_order), prefValue).apply();

        // reload the data
        this.popularMoviesLoad();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}

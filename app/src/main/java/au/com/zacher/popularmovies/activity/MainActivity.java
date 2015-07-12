package au.com.zacher.popularmovies.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import android.os.Handler;

import au.com.zacher.popularmovies.ActivityInitialiser;
import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.adapter.SimpleMovieListAdapter;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.model.SimpleMovie;
import au.com.zacher.popularmovies.sync.SyncAdapter;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private View noInternetSection;
    private Button loadRetryButton;
    private View progressBar;
    private RecyclerView movieGrid;
    private SimpleMovieListAdapter movieGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setApplicationContext(this.getApplicationContext());
        Logger.logActionCreate("MainActivity");
        super.onCreate(savedInstanceState);

        // setup the toolbar and contentView
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = false;
        ActivityInitialiser.initActivity(options, savedInstanceState, this, R.layout.activity_main);

        // setup our periodic syncs
        Utilities.addPeriodicSync(SyncAdapter.SYNC_TYPE_CONFIGURATION, Bundle.EMPTY, 1, Utilities.SyncInterval.DAY);

        // fetch the pieces of the view
        this.noInternetSection = this.findViewById(R.id.no_internet_section);
        this.loadRetryButton = (Button)this.noInternetSection.findViewById(R.id.retry_button);
        this.progressBar = this.findViewById(R.id.progress_bar);
        this.movieGrid = (RecyclerView)this.findViewById(R.id.movie_grid);
        this.movieGridAdapter = new SimpleMovieListAdapter(this.getApplicationContext(), R.layout.fragment_display_item);

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

        MovieContract.getDiscoverMovies(new ContractCallback<SimpleMovie[]>() {
            @Override
            public void success(SimpleMovie[] movies) {
                // show the list and add the loaded items
                MainActivity.this.setViewState(ViewState.SUCCESS);
                MainActivity.this.movieGridAdapter.addAllItems(Arrays.asList(movies));
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

    /**
     * Shows/hides view items based off of the given state
     */
    private void setViewState(final ViewState state) {
        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch(state) {
                    case ERROR:
                        MainActivity.this.progressBar.setVisibility(View.GONE);
                        MainActivity.this.movieGrid.setVisibility(View.GONE);
                        MainActivity.this.noInternetSection.setVisibility(View.VISIBLE);
                        break;

                    case IN_PROGRESS:
                        MainActivity.this.progressBar.setVisibility(View.VISIBLE);
                        MainActivity.this.movieGrid.setVisibility(View.GONE);
                        MainActivity.this.noInternetSection.setVisibility(View.GONE);
                        break;

                    case SUCCESS:
                        MainActivity.this.progressBar.setVisibility(View.GONE);
                        MainActivity.this.movieGrid.setVisibility(View.VISIBLE);
                        MainActivity.this.noInternetSection.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "MainActivity");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }

    private enum ViewState {
        IN_PROGRESS, ERROR, SUCCESS
    }
}

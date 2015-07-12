package au.com.zacher.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.model.Movie;

/**
 * Created by Brad on 12/07/2015.
 */
public class MovieDetailsActivity extends ActivityBase {
    public static final String KEY_MOVIE_ID = "movie-id";
    public static final String KEY_MOVIE_TITLE = "movie-title";

    private String movieId;
    private String movieTitle;
    private View mainItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("MainActivity");

        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = true;
        super.onCreate(savedInstanceState, options, R.layout.activity_movie_details);

        // get the basic items from the intent
        Intent i = this.getIntent();
        this.movieId = i.getStringExtra(MovieDetailsActivity.KEY_MOVIE_ID);
        this.movieTitle = i.getStringExtra(MovieDetailsActivity.KEY_MOVIE_TITLE);

        this.toolbar.setTitle(this.movieTitle);

        this.mainItem = this.findViewById(R.id.main_item);

        // get the rest of the data
        this.getMovieData();
    }
    private void getMovieData() {
        this.setViewState(ViewState.IN_PROGRESS);
        MovieContract.getMovie(this.movieId, new ContractCallback<Movie>() {
            @Override
            public void success(Movie result) {
                MovieDetailsActivity.this.setViewState(ViewState.SUCCESS);

                // load the data into the view
            }

            @Override
            public void failure(Exception e) {
                MovieDetailsActivity.this.setViewState(ViewState.ERROR);
                MovieDetailsActivity.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieDetailsActivity.this.getMovieData();
                    }
                });
            }
        });
    }

    @Override
    public View getMainViewItem() {
        return this.mainItem;
    }
}

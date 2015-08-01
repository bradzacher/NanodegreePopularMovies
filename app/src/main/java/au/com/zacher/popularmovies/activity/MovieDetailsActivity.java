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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.layout.CollapsingTitleLayout;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.model.MovieWithReleases;
import au.com.zacher.popularmovies.model.Release;
import butterknife.Bind;

/**
 * Created by Brad on 12/07/2015.
 */
public class MovieDetailsActivity extends ActivityBase {
    public static final String KEY_MOVIE_ID = "movie-id";
    public static final String KEY_MOVIE_TITLE = "movie-title";

    private String movieId;
    private String movieTitle;

    @Bind(R.id.movie_summary)           protected ScrollView movieSummary;
    @Bind(R.id.backdrop_toolbar)        protected CollapsingTitleLayout collapsingTitle;
    @Bind(R.id.backdrop_toolbar_image)  protected ImageView collapsingTitleImage;
    @Bind(R.id.add_favourite_button)    protected Button addFavouriteButton;
    @Bind(R.id.movie_summary_list)      protected LinearLayout summaryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = true;
        super.onCreate(savedInstanceState, options, R.layout.activity_movie_details, true);

        Logger.logActionCreate("MainActivity");

        // get the basic items from the intent
        Intent i = this.getIntent();
        this.movieId = i.getStringExtra(MovieDetailsActivity.KEY_MOVIE_ID);
        this.movieTitle = i.getStringExtra(MovieDetailsActivity.KEY_MOVIE_TITLE);

        this.addFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - favourite movie (assignment 2)
                Toast.makeText(MovieDetailsActivity.this, "Feature Not Implemented.", Toast.LENGTH_SHORT).show();
            }
        });

        // we want the title to be responsible for the background colour, not the toolbar
        ColorDrawable toolbarBackground = (ColorDrawable)this.toolbar.getBackground();
        int colorInt = toolbarBackground.getColor();
        final int[] toolbarColours = {
                Color.red(colorInt),
                Color.green(colorInt),
                Color.blue(colorInt)
        };
        this.collapsingTitle.setBackground(toolbarBackground);
        this.toolbar.setBackground(null);
        this.collapsingTitle.setTitle(this.movieTitle);
        // ensure the title only gets as big as the required backdrop size
        ViewGroup.LayoutParams layout = this.collapsingTitle.getLayoutParams();
        layout.height = Utilities.getBackdropHeight();
        this.collapsingTitle.setLayoutParams(layout);
        // ensure the scroll view starts at the required point in space
        this.movieSummary.setPadding(this.movieSummary.getPaddingLeft(), layout.height, this.movieSummary.getPaddingRight(), this.movieSummary.getPaddingBottom());
        this.movieSummary.setClipToPadding(false); // this will allow the scroll view to draw *above* its padding limit (so it fills the gap between the title bar and the start of the padding)

        this.movieSummary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                ScrollView movieSummary = MovieDetailsActivity.this.movieSummary;
                CollapsingTitleLayout collapsingTitle = MovieDetailsActivity.this.collapsingTitle;
                Toolbar toolbar = MovieDetailsActivity.this.toolbar;
                ImageView collapsingTitleImage = MovieDetailsActivity.this.collapsingTitleImage;

                // calculate the new size of the collapsing title
                int scrollY = movieSummary.getScrollY();
                int titleHeight = collapsingTitle.getHeight();
                int toolbarHeight = toolbar.getHeight();
                int heightRemaining = titleHeight - scrollY;
                float percent;
                if (heightRemaining > toolbarHeight) {
                    percent = scrollY / (float)(titleHeight - toolbarHeight);
                } else {
                    percent = 1.0f;
                }
                // if the user flicks it can cause a negative percent, which causes the colour filter to flash black
                percent = Math.max(0.0f, percent);

                // set the size of the title bar
                collapsingTitle.setScrollOffset(percent);
                // tint the image on collapse cos it looks neat
                collapsingTitleImage.setColorFilter(Color.argb((int) (170f * percent), toolbarColours[0], toolbarColours[1], toolbarColours[2]));
            }
        });


        // get the rest of the data
        this.getMovieData();
    }
    private void getMovieData() {
        this.setViewState(ViewState.IN_PROGRESS);
        MovieContract.getMovie(this.movieId, new ContractCallback<MovieWithReleases>() {
            @Override
            public void success(MovieWithReleases result) {
                MovieDetailsActivity.this.setViewState(ViewState.SUCCESS);

                MovieDetailsActivity.this.setMovie(result);
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

    /**
     * Sets the data from the given movie into the view
     */
    private void setMovie(MovieWithReleases result) {
        TextView voteCountText = (TextView)this.findViewById(R.id.movie_vote_count);
        TextView voteAverageText = (TextView)this.findViewById(R.id.movie_vote_average);
        TextView plotSynopsisText = (TextView)this.findViewById(R.id.movie_plot_synopsis);
        TextView taglineText = (TextView)this.findViewById(R.id.movie_tagline);
        ImageView miniPosterImage = (ImageView)this.findViewById(R.id.movie_mini_poster);

        // load any required images
        Picasso.with(this)
                .load(Utilities.getBaseBackdropUrl() + result.backdrop_path)
                .into(this.collapsingTitleImage);
        Picasso.with(this)
                .load(Utilities.getBasePosterUrl() + result.poster_path)
                .into(miniPosterImage);

        // set the values into the fixed fields
        voteCountText.setText(String.valueOf(result.vote_count));
        voteAverageText.setText(String.valueOf((int)(result.vote_average * 10)) + this.getString(R.string.movie_details_activity_vote_average_suffix));
        plotSynopsisText.setText(result.overview);
        taglineText.setText(result.tagline);

        // try to get the release date for the user's country
        String myCountryCode = Utilities.getCountryCode();
        String releaseFlag = "";
        String releaseDate = result.release_date;
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat localeDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        for (Release r : result.releases.countries) {
            if (!r.primary && !r.iso_3166_1.equals(myCountryCode)) {
                continue;
            }

            releaseFlag = Utilities.getFlagEmoji(r.iso_3166_1);
            // convert the date to the user's locale format
            try {
                Date d = apiDateFormat.parse(r.release_date);
                releaseDate = localeDateFormat.format(d);
            } catch (ParseException ignored) {
                // fall back on the original release date from the response
                releaseDate = r.release_date;
            }

            if (!r.primary) {
                // keep going until we find our country
                break;
            }
        }

        // concat the genre names
        String genreStr = "";
        for (int i = 0; i < result.genres.length; i++) {
            if (i != 0) {
                genreStr += System.getProperty("line.separator");
            }
            genreStr += result.genres[i].name;
        }

        // add the list of items to the summary table
        NumberFormat numberFormat = NumberFormat.getInstance();
        String[][] summaryItems = {
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_release_date),
                        releaseFlag + " " + releaseDate
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_runtime),
                        result.runtime + this.getString(R.string.movie_details_activity_summary_runtime_units)
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_status),
                        result.status
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_genres),
                        genreStr
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_budget),
                        "$" + numberFormat.format(result.budget)
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_revenue),
                        "$" + numberFormat.format(result.revenue)
                },
                new String[]{
                        this.getString(R.string.movie_details_activity_summary_language),
                        new Locale(result.original_language).getDisplayName()
                }
        };
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String[] item : summaryItems) {
            View v = inflater.inflate(R.layout.fragment_movie_details_summary_row, this.summaryList, false);
            ((TextView)v.findViewById(R.id.row_title)).setText(item[0]);
            ((TextView)v.findViewById(R.id.row_text)).setText(item[1]);
            this.summaryList.addView(v);
        }
    }

    @Override
    public View getMainViewItem() {
        return this.movieSummary;
    }
}

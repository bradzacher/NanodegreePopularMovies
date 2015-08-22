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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import au.com.zacher.popularmovies.ActivityInitialiser;
import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.ViewState;
import au.com.zacher.popularmovies.activity.layout.CollapsingTitleLayout;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.data.helper.FavouriteHelper;
import au.com.zacher.popularmovies.model.MovieWithReleases;
import au.com.zacher.popularmovies.model.Release;
import butterknife.Bind;

/**
 * Created by Brad on 2/08/2015.
 */
public class MovieDetailsFragment extends FragmentBase {
    public static final String KEY_MOVIE_ID = "movie-id";
    public static final String KEY_MOVIE_TITLE = "movie-title";
    public static final String KEY_AS_ACTIVITY = "as-activity";

    private String movieId;

    @Bind(R.id.movie_summary)           protected ScrollView movieSummary;
    @Bind(R.id.backdrop_toolbar)        protected CollapsingTitleLayout collapsingTitle;
    @Bind(R.id.backdrop_toolbar_image)  protected ImageView collapsingTitleImage;
    @Bind(R.id.add_favourite_button)    protected ImageButton addFavouriteButton;
    @Bind(R.id.movie_summary_list)      protected LinearLayout summaryList;
    @Bind(R.id.toolbar)                 protected Toolbar toolbar;
    @Bind(R.id.loading_ui_wrapper)      protected RelativeLayout loadingUiWrapper;

    /**
     * The specific movie details
     */
    private MovieWithReleases movieObject;

    public Toolbar getToolbar() {
        return this.toolbar;
    }
    private ReviewListFragment reviewsList;
    private TrailerListFragment trailerList;

    /**
     * This shouldn't be called anywhere publicly
     */
    public MovieDetailsFragment() {
        super();
    }

    public static MovieDetailsFragment newInstance(String movieId, String movieTitle) {
        return MovieDetailsFragment.newInstance(movieId, movieTitle, true);
    }
    public static MovieDetailsFragment newInstance(String movieId, String movieTitle, boolean asActivity) {
        Bundle args = new Bundle();
        args.putString(MovieDetailsFragment.KEY_MOVIE_ID, movieId);
        args.putString(MovieDetailsFragment.KEY_MOVIE_TITLE, movieTitle);
        args.putBoolean(MovieDetailsFragment.KEY_AS_ACTIVITY, asActivity);

        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View retView = super.onCreateView(R.layout.fragment_movie_details, inflater, container, savedInstanceState);
        Logger.logActionCreate("Details Fragment");

        // get the basic items from the intent
        Bundle args = this.getArguments();
        this.movieId = args.getString(MovieDetailsFragment.KEY_MOVIE_ID);
        String movieTitle = args.getString(MovieDetailsFragment.KEY_MOVIE_TITLE);
        boolean asActivity = args.getBoolean(MovieDetailsFragment.KEY_AS_ACTIVITY);

        int toolbarHeight = Utilities.calculateBackdropHeight(Utilities.getScreenWidth());
        if (!asActivity) {
            toolbarHeight /= 2;
        }

        // offset the loading fragment so it appears below the expanded title bar
        RelativeLayout.LayoutParams loadingUiWrapperLayoutParams = (RelativeLayout.LayoutParams) this.loadingUiWrapper.getLayoutParams();
        loadingUiWrapperLayoutParams.topMargin = toolbarHeight;

        // check the parent to see if we want a back button
        if (asActivity) {
            this.parent.getToolbar().setVisibility(View.GONE);
        }
        if (this.parent.getToolbar().getNavigationIcon() != null) {
            ToolbarOptions opts = new ToolbarOptions();
            opts.enableUpButton = true;
            ActivityInitialiser.setToolbarOptions(this.parent, this.toolbar, opts);
        }

        // create the review list fragment
        this.reviewsList = ReviewListFragment.newInstance(this.movieId);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.movie_reviews_list, this.reviewsList)
                .commit();

        // create the trailer list fragment
        this.trailerList = TrailerListFragment.newInstance(this.movieId);
        this.getFragmentManager().beginTransaction()
                .replace(R.id.movie_videos_list, this.trailerList)
                .commit();

        if (new FavouriteHelper().isFavourite(this.movieId)) {
            // make sure the button looks right
            this.addFavouriteButton.setImageResource(R.drawable.ic_favorite_black_36dp);
        }
        // attach button listener
        this.addFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDetailsFragment.this.favouriteButtonOnClick();
            }
        });

        // we want the title to be responsible for the background colour, not the toolbar
        ColorDrawable toolbarBackground = (ColorDrawable) this.toolbar.getBackground();
        int colorInt = toolbarBackground.getColor();
        final int[] toolbarColours = {
                Color.red(colorInt),
                Color.green(colorInt),
                Color.blue(colorInt)
        };
        this.collapsingTitle.setBackground(toolbarBackground);
        this.toolbar.setBackground(null);
        this.collapsingTitle.setTitle(movieTitle);

        // ensure the title only gets as big as the required backdrop size
        ViewGroup.LayoutParams layout = this.collapsingTitle.getLayoutParams();
        layout.height = toolbarHeight;
        this.collapsingTitle.setLayoutParams(layout);
        // ensure the scroll view starts at the required point in space
        this.movieSummary.setPadding(this.movieSummary.getPaddingLeft(), toolbarHeight, this.movieSummary.getPaddingRight(), this.movieSummary.getPaddingBottom());
        this.movieSummary.setClipToPadding(false); // this will allow the scroll view to draw *above* its padding limit (so it fills the gap between the title bar and the start of the padding)

        this.movieSummary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                ScrollView movieSummary = MovieDetailsFragment.this.movieSummary;
                CollapsingTitleLayout collapsingTitle = MovieDetailsFragment.this.collapsingTitle;
                Toolbar toolbar = MovieDetailsFragment.this.toolbar;
                ImageView collapsingTitleImage = MovieDetailsFragment.this.collapsingTitleImage;

                // calculate the new size of the collapsing title
                int scrollY = movieSummary.getScrollY();
                int titleHeight = collapsingTitle.getHeight();
                int toolbarHeight = toolbar.getHeight();
                int heightRemaining = titleHeight - scrollY;
                float percent;
                if (heightRemaining > toolbarHeight) {
                    percent = scrollY / (float) (titleHeight - toolbarHeight);
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
        this.movieSummary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // check to see if the reviews section is scrolled in yet
                MovieDetailsFragment.this.reviewsList.loadIfVisible(MovieDetailsFragment.this.movieSummary);
                // check to see if the reviews section is scrolled in yet
                MovieDetailsFragment.this.trailerList.loadIfVisible(MovieDetailsFragment.this.movieSummary);
            }
        });


        // get the rest of the data
        this.getMovieData();

        return retView;
    }
    private void getMovieData() {
        this.setViewState(ViewState.IN_PROGRESS);
        MovieContract.getMovie(this.movieId, new ContractCallback<MovieWithReleases>() {
            @Override
            public void success(MovieWithReleases result) {
                // because the load is async, we have to make sure that we're still attached before setting view data
                if (MovieDetailsFragment.this.isAdded()) {
                    MovieDetailsFragment.this.setViewState(ViewState.SUCCESS);

                    MovieDetailsFragment.this.setMovie(result);
                }
            }

            @Override
            public void failure(Exception e) {
                MovieDetailsFragment.this.setViewState(ViewState.ERROR);
                MovieDetailsFragment.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MovieDetailsFragment.this.getMovieData();
                    }
                });
            }
        });
    }

    /**
     * Sets the data from the given movie into the view
     */
    private void setMovie(MovieWithReleases result) {
        this.movieObject = result;

        TextView voteCountText = (TextView)this.rootView.findViewById(R.id.movie_vote_count);
        TextView voteAverageText = (TextView)this.rootView.findViewById(R.id.movie_vote_average);
        TextView plotSynopsisText = (TextView)this.rootView.findViewById(R.id.movie_plot_synopsis);
        TextView taglineText = (TextView)this.rootView.findViewById(R.id.movie_tagline);
        ImageView miniPosterImage = (ImageView)this.rootView.findViewById(R.id.movie_mini_poster);

        // load any required images
        Context context = this.getActivity();
        Picasso.with(context)
                .load(Utilities.getBaseBackdropUrl() + result.backdrop_path)
                .into(this.collapsingTitleImage);
        Picasso.with(context)
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
        LayoutInflater inflater = LayoutInflater.from(context);
        for (String[] item : summaryItems) {
            View v = inflater.inflate(R.layout.fragment_movie_details_summary_row, this.summaryList, false);
            ((TextView)v.findViewById(R.id.row_title)).setText(item[0]);
            ((TextView)v.findViewById(R.id.row_text)).setText(item[1]);
            this.summaryList.addView(v);
        }
    }

    /**
     * Called when the favourite button is clicked
     */
    private void favouriteButtonOnClick() {
        FavouriteHelper db = new FavouriteHelper();
        if (db.isFavourite(this.movieId)) {
            this.addFavouriteButton.setImageResource(R.drawable.ic_favorite_border_black_36dp);
            db.remove(this.movieId);
        } else {
            this.addFavouriteButton.setImageResource(R.drawable.ic_favorite_black_36dp);
            db.add(this.movieId, this.movieObject.poster_path);
        }
    }

    @Override
    public View getMainViewItem() {
        return this.movieSummary;
    }
}

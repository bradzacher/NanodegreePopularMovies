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
import android.os.Bundle;
import android.view.View;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.activity.fragment.MovieDetailsFragment;

/**
 * Created by Brad on 12/07/2015.
 */
public class MovieDetailsActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = true;
        super.onCreate(savedInstanceState, options, R.layout.activity_movie_details, true);
        // we want to use the fragment's toolbar
        this.toolbar.setVisibility(View.GONE);

        Intent i = this.getIntent();
        String movieId = i.getStringExtra(MovieDetailsFragment.KEY_MOVIE_ID);
        String movieTitle = i.getStringExtra(MovieDetailsFragment.KEY_MOVIE_TITLE);

        // create the fragment
        MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(movieId, movieTitle);
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_fragment, fragment)
                .commit();
    }
}

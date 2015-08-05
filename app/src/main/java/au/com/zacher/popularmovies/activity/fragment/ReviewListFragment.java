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

import android.graphics.Rect;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.activity.ViewState;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.model.Review;
import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewListFragment extends FragmentBase {
    private static final String KEY_MOVIE_ID = "movie-id";

    private String movieId;

    private boolean hasLoadedReviews;

    @Bind(R.id.root_view) View rootView;
    @Bind(R.id.movie_reviews_list) LinearLayout reviewList;
    @Bind(R.id.no_reviews_text) TextView noResultsText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The id of the movie to get reviews for
     * @return A new instance of fragment ReviewListFragment.
     */
    public static ReviewListFragment newInstance(String movieId) {
        ReviewListFragment fragment = new ReviewListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.movieId = this.getArguments().getString(KEY_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(R.layout.fragment_review_list, inflater, container, savedInstanceState);
    }

    /**
     * Checks if the view is at least a little bit visible and will load the reviews
     */
    public void loadIfVisible(ScrollView parent) {
        if (!this.hasLoadedReviews) { // don't bother with the check if we've already loaded the reviews
            Rect scrollBounds = new Rect();
            parent.getHitRect(scrollBounds);
            if (this.rootView.getLocalVisibleRect(scrollBounds)) {
                this.getReviews();
            }
        }
    }
    private void getReviews() {
        // mark that we've at least tried
        ReviewListFragment.this.hasLoadedReviews = true;

        this.setViewState(ViewState.IN_PROGRESS);
        MovieContract.getReviews(this.movieId, new ContractCallback<Review[]>() {
            @Override
            public void success(Review[] result) {
                ReviewListFragment.this.setViewState(ViewState.SUCCESS);

                if (result.length != 0) {
                    LayoutInflater inflater = LayoutInflater.from(ReviewListFragment.this.parent);
                    for (Review review : result) {
                        View v = inflater.inflate(R.layout.fragment_single_review, ReviewListFragment.this.reviewList, false);
                        ((TextView) v.findViewById(R.id.display_item_title)).setText(review.author);
                        ((TextView) v.findViewById(R.id.display_item_subtitle)).setText(review.content);
                        ReviewListFragment.this.reviewList.addView(v);
                    }
                } else {
                    ReviewListFragment.this.noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(Exception e) {
                ReviewListFragment.this.setViewState(ViewState.ERROR);
                ReviewListFragment.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReviewListFragment.this.getReviews();
                    }
                });
            }
        });
    }

    @Override
    public View getMainViewItem() {
        return this.reviewList;
    }
}

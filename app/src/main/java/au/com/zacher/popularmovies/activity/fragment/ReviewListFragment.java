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

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
public class ReviewListFragment extends DetailsSubListFragment {
    @Bind(R.id.movie_reviews_list) LinearLayout reviewList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The id of the movie to get reviews for
     * @return A new instance of fragment ReviewListFragment.
     */
    public static ReviewListFragment newInstance(String movieId) {
        ReviewListFragment fragment = new ReviewListFragment();
        DetailsSubListFragment.newInstance(movieId, fragment);
        return fragment;
    }

    public ReviewListFragment() {
        super();
    }

    @Override
    protected void getData(final ContractCallback<Boolean> callback) {
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
                    callback.success(true);
                } else {
                    callback.success(false);
                }
            }

            @Override
            public void failure(Exception e) {
                callback.failure(e);
            }
        });
    }

    @Override
    public View getMainViewItem() {
        return this.reviewList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_review_list;
    }
}

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.activity.ViewState;
import au.com.zacher.popularmovies.contract.ContractCallback;
import butterknife.Bind;

/**
 * Created by Brad on 16/08/2015.
 */
public abstract class DetailsSubListFragment extends FragmentBase {
    protected static final String KEY_MOVIE_ID = "movie-id";
    protected boolean hasLoadedData;

    protected String movieId;

    @Bind(R.id.root_view)           protected View rootView;
    @Bind(R.id.no_reviews_text)     protected TextView noResultsText;

    protected static <T extends DetailsSubListFragment> void newInstance(String movieId, T fragment) {
        Bundle args = new Bundle();
        args.putString(KEY_MOVIE_ID, movieId);
        fragment.setArguments(args);
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
        return super.onCreateView(this.getLayoutId(), inflater, container, savedInstanceState);
    }

    /**
     * Checks if the view is at least a little bit visible and will load the reviews
     */
    public void loadIfVisible(ScrollView parent) {
        if (!this.hasLoadedData) { // don't bother with the check if we've already loaded the reviews
            Rect scrollBounds = new Rect();
            parent.getHitRect(scrollBounds);
            if (this.rootView.getLocalVisibleRect(scrollBounds)) {
                this.getDataHelper();
            }
        }
    }

    /**
     * Triggers the load of the data
     * @param callback - call this when the data load has completed (failed or success - call success with false if no items loaded)
     */
    protected abstract void getData(ContractCallback<Boolean> callback);
    private void getDataHelper() {
        // mark that we've at least tried
        this.hasLoadedData = true;

        this.setViewState(ViewState.IN_PROGRESS);
        this.getData(new ContractCallback<Boolean>() {
            @Override
            public void success(Boolean hasItems) {
                DetailsSubListFragment.this.setViewState(ViewState.SUCCESS);

                if (!hasItems) {
                    DetailsSubListFragment.this.noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(Exception error) {
                DetailsSubListFragment.this.setViewState(ViewState.ERROR);
                DetailsSubListFragment.this.loadRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailsSubListFragment.this.getDataHelper();
                    }
                });
            }
        });
    }

    /**
     * Gets the id of the layout to load when this fragment is created
     */
    protected abstract int getLayoutId();
}

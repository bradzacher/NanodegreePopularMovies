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
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.activity.ActivityBase;
import au.com.zacher.popularmovies.activity.ViewState;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Brad on 2/08/2015.
 */
public abstract class FragmentBase extends Fragment {
    protected ActivityBase parent;
    protected View rootView;

    @Bind(R.id.progress_bar)        protected ProgressBar progressBar;
    @Bind(R.id.no_internet_section) protected View noInternetSection;
    @Bind(R.id.retry_button)        protected Button loadRetryButton;

    public View onCreateView(int layoutId, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.parent = (ActivityBase)super.getActivity();
        this.rootView = inflater.inflate(layoutId, container, false);

        ButterKnife.bind(this, this.rootView);

        return this.rootView;
    }

    public void setViewState(final ViewState state) {
        Handler mainHandler = new Handler(this.parent.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case ERROR:
                        FragmentBase.this.progressBar.setVisibility(View.GONE);
                        FragmentBase.this.getMainViewItem().setVisibility(View.GONE);
                        FragmentBase.this.noInternetSection.setVisibility(View.VISIBLE);
                        break;

                    case IN_PROGRESS:
                        FragmentBase.this.progressBar.setVisibility(View.VISIBLE);
                        FragmentBase.this.getMainViewItem().setVisibility(View.GONE);
                        FragmentBase.this.noInternetSection.setVisibility(View.GONE);
                        break;

                    case SUCCESS:
                        FragmentBase.this.progressBar.setVisibility(View.GONE);
                        FragmentBase.this.getMainViewItem().setVisibility(View.VISIBLE);
                        FragmentBase.this.noInternetSection.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    public abstract View getMainViewItem();
}

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
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.activity.ViewState;
import au.com.zacher.popularmovies.contract.ContractCallback;
import au.com.zacher.popularmovies.contract.MovieContract;
import au.com.zacher.popularmovies.model.MovieVideo;
import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailerListFragment extends DetailsSubListFragment {
    @Bind(R.id.movie_trailer_list) LinearLayout trailerList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId The id of the movie to get reviews for
     * @return A new instance of fragment ReviewListFragment.
     */
    public static TrailerListFragment newInstance(String movieId) {
        TrailerListFragment fragment = new TrailerListFragment();
        DetailsSubListFragment.newInstance(movieId, fragment);
        return fragment;
    }

    public TrailerListFragment() {
        super();
    }

    @Override
    protected void getData(final ContractCallback<Boolean> callback) {
        MovieContract.getVideos(this.movieId, new ContractCallback<MovieVideo[]>() {
            @Override
            public void success(MovieVideo[] result) {
                TrailerListFragment.this.setViewState(ViewState.SUCCESS);

                if (result.length != 0) {
                    LayoutInflater inflater = LayoutInflater.from(TrailerListFragment.this.parent);
                    for (final MovieVideo video : result) {
                        final String videoUrl = "http://www.youtube.com/watch?v=" + video.key;

                        View v = inflater.inflate(R.layout.fragment_single_trailer, TrailerListFragment.this.trailerList, false);
                        ((TextView) v.findViewById(R.id.display_item_title)).setText(video.name);
                        v.findViewById(R.id.trailer_share_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_TEXT, videoUrl);
                                // create a share chooser so that
                                TrailerListFragment.this.startActivity(Intent.createChooser(i, TrailerListFragment.this.getString(R.string.generic_share)));
                            }
                        });

                        TrailerListFragment.this.trailerList.addView(v);

                        // when the user taps - open the video
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TrailerListFragment.this.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(videoUrl)));
                            }
                        });
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
        return this.trailerList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trailer_list;
    }
}

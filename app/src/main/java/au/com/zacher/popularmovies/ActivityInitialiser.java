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

package au.com.zacher.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Brad on 22/06/2015.
 */
public final class ActivityInitialiser {
    /**
     * Sets up the view and toolbar
     * @param options the options to use to build the toolbar
     * @param savedInstanceState the saved state to build the activity from
     * @param activity the activity to initialise
     * @param layoutId the id of the layout to inflate into the activity
     * @return the instance of the {@link Toolbar} that was constructed
     */
    public static Toolbar initActivity(ToolbarOptions options, Bundle savedInstanceState, final Activity activity, int layoutId) {
        Toolbar toolbar;
        AppCompatDelegate compatDelegate;

        if (activity instanceof AppCompatActivity) {
            activity.setContentView(layoutId);
            compatDelegate = ((AppCompatActivity)activity).getDelegate();
        } else {
            // setup the toolbar
            AppCompatCallback callback = new AppCompatCallback() {
                @Override
                public void onSupportActionModeStarted(ActionMode actionMode) {}

                @Override
                public void onSupportActionModeFinished(ActionMode actionMode) {}

                @Nullable
                @Override
                public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                    return null;
                }
            };
            compatDelegate = AppCompatDelegate.create(activity, callback);
            compatDelegate.onCreate(savedInstanceState);
            compatDelegate.setContentView(layoutId);
        }
        toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        compatDelegate.setSupportActionBar(toolbar);

        /*
        ActionBar actionbar = compatDelegate.getSupportActionBar();

        if (activity instanceof Toolbar.OnMenuItemClickListener) {
            toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener)activity);
        }
        if (activity instanceof Toolbar.OnClickListener) {
            toolbar.setOnClickListener((Toolbar.OnClickListener)activity);
        }*/

        ActivityInitialiser.setToolbarOptions(activity, toolbar, options);

        /*if (options != null) {
            if (options.enableUpButton) {
                actionbar.setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onBackPressed();
                    }
                });
            }
        }*/
        return toolbar;
    }

    public static void setToolbarOptions(final Activity activity, Toolbar toolbar, ToolbarOptions options) {
        if (options != null) {
            if (options.enableUpButton) {
                toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onBackPressed();
                    }
                });
            }
        }
    }
}

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

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import au.com.zacher.popularmovies.ActivityInitialiser;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.api.Configuration;
import au.com.zacher.popularmovies.contract.ConfigurationContract;
import au.com.zacher.popularmovies.contract.ContractCallback;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Brad on 13/07/2015.
 */
public abstract class ActivityBase extends AppCompatActivity {
    @Bind(R.id.toolbar)             protected Toolbar toolbar;
    public Toolbar getToolbar() {
        return this.toolbar;
    }

    protected void onCreate(Bundle savedInstanceState, ToolbarOptions options, int layoutId) {
        this.onCreate(savedInstanceState, options, layoutId, false);
    }
    protected void onCreate(Bundle savedInstanceState, ToolbarOptions options, int layoutId, boolean checkForConfiguration) {
        super.onCreate(savedInstanceState);

        Utilities.setApplicationContext(this.getApplicationContext());

        if (checkForConfiguration && ConfigurationContract.getInstance() == null) {
            ConfigurationContract.getConfig(new ContractCallback<Configuration>() {
                @Override
                public void success(Configuration result) {
                    Utilities.initFromConfig(result);
                }

                @Override
                public void failure(Exception e) {
                    // this shouldn't happen - this path only gets called from child views - which can only be reached if the MainActivity has at one point had a valid config
                }
            });
        }

        ActivityInitialiser.initActivity(options, savedInstanceState, this, layoutId);

        ButterKnife.bind(this);
    }
}

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

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Spinner;

import au.com.zacher.popularmovies.Logger;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;
import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.activity.fragment.MovieListFragment;

public class MainActivity extends ActivityBase implements Toolbar.OnMenuItemClickListener {
    private MovieListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToolbarOptions options = new ToolbarOptions();
        options.enableUpButton = false;
        super.onCreate(savedInstanceState, options, R.layout.activity_main);

        this.fragment = (MovieListFragment)this.getSupportFragmentManager().findFragmentById(R.id.movie_list_fragment);
        FrameLayout detailsFragmentParent = (FrameLayout)this.findViewById(R.id.movie_details_fragment_parent);
        FrameLayout listFragmentParent = (FrameLayout)this.findViewById(R.id.movie_list_fragment_parent);
        if (this.findViewById(R.id.movie_details_fragment) != null) {
            // resize the views for a 50-50 split
            Point p = new Point();
            ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
            int screenWidth = p.x;

            ViewGroup.LayoutParams params = listFragmentParent.getLayoutParams();
            params.width = screenWidth / 2;
            listFragmentParent.setLayoutParams(params);

            params = detailsFragmentParent.getLayoutParams();
            params.width = screenWidth / 2;
            detailsFragmentParent.setLayoutParams(params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);

        this.getLayoutInflater().inflate(R.layout.fragment_sort_order_menu, this.toolbar);
        Spinner spinner = (Spinner)this.toolbar.findViewById(R.id.sort_order_menu);
        // set the selected item to be the user preference
        String currentPref = Utilities.getPreference(R.string.pref_discovery_sort_order, R.string.pref_default_discovery_sort_order);
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            String itemText = (String)spinner.getItemAtPosition(i);
            if (itemText.equals(currentPref)) {
                spinner.setSelection(i, false);
            }
        }
        spinner.setOnItemSelectedListener(this.fragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logMethodCall("onOptionsItemSelected(MenuItem)", "MainActivity");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return this.onOptionsItemSelected(item);
    }
}

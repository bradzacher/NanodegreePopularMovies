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

package au.com.zacher.popularmovies.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.Utilities;

/**
 * Created by Brad on 23/06/2015.
 */
public class DisplayItemViewHolder extends RecyclerView.ViewHolder {
    public DisplayItem item;

    public ImageView image;
    public TextView title;
    public TextView subtitle;

    public DisplayItemViewHolder(View item) {
        super(item);
        this.image = (ImageView)item.findViewById(R.id.display_item_image);
        /*this.title = (TextView)item.findViewById(R.id.display_item_title);
        this.subtitle = (TextView)item.findViewById(R.id.display_item_subtitle);*/
    }

    public void bind(Context context, DisplayItem item) {
        /*if (item.title != null) {
            this.title.setText(item.title);
        } else {
            this.title.setVisibility(View.GONE);
        }
        if (item.subtitle != null) {
            this.subtitle.setText(item.subtitle);
        } else {
            this.subtitle.setVisibility(View.GONE);
        }*/
        this.item = item;

        Utilities.backgroundLoadImage(context, item, this);
    }
}
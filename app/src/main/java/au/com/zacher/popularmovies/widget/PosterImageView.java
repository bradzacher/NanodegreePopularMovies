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

package au.com.zacher.popularmovies.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import au.com.zacher.popularmovies.Utilities;

/**
 * Created by Brad on 22/08/2015.
 */
public class PosterImageView extends ImageView {
    public PosterImageView(Context context)
    {
        super(context);
    }

    public PosterImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PosterImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.getMeasuredWidth();
        int height = Utilities.calculatePosterHeight(width);
        this.setMeasuredDimension(width, height); //Snap to width
    }
}

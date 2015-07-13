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

import au.com.zacher.popularmovies.Utilities;

/**
 * Created by Brad on 23/06/2015.
 */
public class DisplayItem {
    public String id;
    public String imageUrl;
    public String title;
    public String subtitle;

    public DisplayItem(String id, String imageUrl, String title) {
        this(id, imageUrl, title, null);
    }
    public DisplayItem(String id, String imageUrl, String title, String subtitle) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DisplayItem)) {
            return false;
        }
        DisplayItem another = (DisplayItem)obj;

        return this.id.equals(another.id) &&
                Utilities.areEqual(this.imageUrl, another.imageUrl) &&
                Utilities.areEqual(this.title, another.title) &&
                Utilities.areEqual(this.subtitle, another.subtitle);
    }
}
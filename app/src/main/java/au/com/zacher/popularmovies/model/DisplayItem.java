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
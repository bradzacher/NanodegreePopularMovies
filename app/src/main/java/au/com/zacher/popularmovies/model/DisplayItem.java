package au.com.zacher.popularmovies.model;

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
                this.imageUrl.equals(another.imageUrl) &&
                this.title.equals(another.title) &&
                this.subtitle.equals(another.subtitle);
    }
}
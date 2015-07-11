package au.com.zacher.popularmovies.api;

/**
 * Created by Brad on 10/07/2015.
 */
public class Configuration {
    public class ImageConfiguration {
        public String base_url;
        public String secure_base_url;
        public String[] backdrop_sizes;
        public String[] logo_sizes;
        public String[] poster_sizes;
        public String[] profile_sizes;
        public String[] still_sizes;
    }

    public ImageConfiguration images;
    public String[] change_keys;
}

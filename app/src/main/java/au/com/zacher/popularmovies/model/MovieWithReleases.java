package au.com.zacher.popularmovies.model;

import au.com.zacher.popularmovies.api.results.PagedResults;

/**
 * Created by Brad on 13/07/2015.
 */
public class MovieWithReleases extends Movie {
    public class ReleasesData {
        public Release[] countries;
    }

    public ReleasesData releases;
}

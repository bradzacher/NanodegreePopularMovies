package au.com.zacher.popularmovies.api.results;

/**
 * Created by Brad on 11/07/2015.
 */
public class PagedResults<T> extends Results<T> {
    public int page;
    public int total_pages;
    public int total_results;
}

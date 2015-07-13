package au.com.zacher.popularmovies.model;

/**
 * Created by Brad on 11/07/2015.
 */
public class Movie extends MovieBase {
    public Collection belongs_to_collection;
    public int budget;
    public Genre[] genres;
    public String homepage;
    public String imdb_id;
    public SimpleCompany[] production_companies;
    public Country[] production_countries;
    public int revenue;
    public int runtime;
    public Language[] spoken_languages;
    public String status;
    public String tagline;
}

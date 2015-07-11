package au.com.zacher.popularmovies.model;

import java.util.Date;

import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;

/**
 * Created by Brad on 11/07/2015.
 */
public class ApiResultCacheItem {
    public String type;
    public String json;
    public Date date;

    public ApiResultCacheItem(String type, String json, Date date) {
        this.type = type;
        this.json = json;
        this.date = date;
    }
}

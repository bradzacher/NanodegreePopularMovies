package au.com.zacher.popularmovies.data;

import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.entry.DbEntry;

/**
 * Created by Brad on 27/06/2015.
 */
public class DbContract {
    public static final DbEntry[] Entries = {
        new ApiResultCacheEntry()
    };

}
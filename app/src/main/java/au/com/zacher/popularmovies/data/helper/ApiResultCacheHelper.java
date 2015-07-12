package au.com.zacher.popularmovies.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.Date;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.DbColumn;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;

/**
 * Created by Brad on 11/07/2015.
 */
public class ApiResultCacheHelper extends DbHelper {
    public ApiResultCacheHelper() {
        super(Utilities.getApplicationContext());
    }
    public ApiResultCacheHelper(Context context) {
        super(context);
    }

    public void add(String type, Object obj) {
        SQLiteDatabase db = this.getWritableDatabase();

        String json;
        if (obj instanceof String) {
            json = (String)obj;
        } else {
            json = Utilities.getObjectJson(obj);
        }

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ApiResultCacheEntry.COLUMN_TYPE.name, type);
            values.put(ApiResultCacheEntry.COLUMN_JSON.name, json);
            values.put(ApiResultCacheEntry.COLUMN_DATE.name, (new Date()).getTime());

            // delete the old value
            db.replace(ApiResultCacheEntry.TABLE_NAME, null, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public Cursor get(String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] queryColumns = new String[ApiResultCacheEntry.columns.values().size()];
        Collection<DbColumn> values = ApiResultCacheEntry.columns.values();
        DbColumn[] cols = new DbColumn[values.size()];
        values.toArray(cols);
        for (int i = 0; i < queryColumns.length; i++) {
            queryColumns[i] = cols[i].name;
        }

        Cursor result = null;

        db.beginTransaction();
        try {
            result = db.query(ApiResultCacheEntry.TABLE_NAME,
                                    queryColumns,
                                    ApiResultCacheEntry.COLUMN_TYPE.name + " = ?",
                                    new String[] { type },
                                    null,
                                    null,
                                    null);
        } finally {
            db.endTransaction();
        }

        return result;
    }
}

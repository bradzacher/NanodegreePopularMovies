package au.com.zacher.popularmovies.data.entry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.ColumnType;
import au.com.zacher.popularmovies.data.DbColumn;

/**
 * Created by Brad on 10/07/2015.
 */
public class ApiResultCacheEntry extends DbEntry {
    public static final String TABLE_NAME = "api_result_cache";

    public static final Map<String, DbColumn> columns;
    static {
        DbColumn col;
        Map<String, DbColumn> map = new HashMap<>();

        col = new DbColumn("type", ColumnType.TEXT, true);
        COLUMN_TYPE = col;
        map.put("COLUMN_TYPE", col);

        col = new DbColumn("json", ColumnType.TEXT);
        COLUMN_JSON = col;
        map.put("COLUMN_JSON", col);

        col = new DbColumn("date", ColumnType.DATETIME);
        COLUMN_DATE = col;
        map.put("COLUMN_DATE", col);

        columns = Collections.unmodifiableMap(map);
    }

    /**
     * The type of result
     */
    public static final DbColumn COLUMN_TYPE;

    /**
     * The json representation of the result
     */
    public static final DbColumn COLUMN_JSON;

    /**
     * The date the result was obtained
     */
    public static final DbColumn COLUMN_DATE;

    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(this.basicCreateTable());
    }

    @Override
    public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(this.basicDropTable());
        this.createTable(db);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, DbColumn> getColumns() {
        return columns;
    }

    /**
     * Gets the object data from a cursor row (doesn't perform any checks on the cursor)
     */
    public static Object getObjectFromRow(Cursor cur, Class type) {
        Gson gson = new Gson();
        return gson.fromJson(cur.getString(cur.getColumnIndex(ApiResultCacheEntry.COLUMN_JSON.name)), type);
    }
}

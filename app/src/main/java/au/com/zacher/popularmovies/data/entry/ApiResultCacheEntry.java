/*
 * Copyright 2015 Brad Zacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.zacher.popularmovies.data.entry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        col = new DbColumn("date", ColumnType.INTEGER);
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

    /**
     * Checks if the cursor row is older than the given age (doesn't perform any checks on the cursor)
     */
    public static boolean isOlderThan(Cursor cur, long age) {
        long insertedTimeStamp = cur.getLong(cur.getColumnIndex(ApiResultCacheEntry.COLUMN_DATE.name));
        long timeAgo = (new Date().getTime()) - age;
        return (timeAgo - insertedTimeStamp) > 0;
    }
}

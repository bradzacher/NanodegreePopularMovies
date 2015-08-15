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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import au.com.zacher.popularmovies.data.ColumnType;
import au.com.zacher.popularmovies.data.DbColumn;

/**
 * Created by Brad on 15/08/2015.
 */
public class FavouriteEntry extends DbEntry {
    public static final String TABLE_NAME = "favourites";


    public static final Map<String, DbColumn> columns;
    static {
        DbColumn col;
        Map<String, DbColumn> map = new HashMap<>();

        col = new DbColumn("item_id", ColumnType.TEXT, true);
        COLUMN_ITEM_ID = col;
        map.put("COLUMN_ITEM_ID", col);

        col = new DbColumn("item_image", ColumnType.TEXT, false);
        COLUMN_POSTER_PATH = col;
        map.put("COLUMN_POSTER_PATH", col);

        col = new DbColumn("date", ColumnType.INTEGER);
        COLUMN_DATE = col;
        map.put("COLUMN_DATE", col);

        columns = Collections.unmodifiableMap(map);
    }

    /**
     * The id of the item (from movie db's API)
     */
    public static final DbColumn COLUMN_ITEM_ID;

    /**
     * The path to the item's poster (for convenience loading later)
     */
    public static final DbColumn COLUMN_POSTER_PATH;

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
        try {
            db.execSQL(this.basicDropTable());
        } catch (SQLiteException ignored) { }
        this.createTable(db);
        // TODO - make sure that this gets updated if I ever change th table structure so that
    }

    @Override
    public String getTableName() {
        return FavouriteEntry.TABLE_NAME;
    }

    @Override
    public Map<String, DbColumn> getColumns() {
        return FavouriteEntry.columns;
    }
}

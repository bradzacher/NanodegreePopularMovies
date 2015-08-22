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

package au.com.zacher.popularmovies.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import au.com.zacher.popularmovies.Utilities;
import au.com.zacher.popularmovies.data.entry.FavouriteEntry;

/**
 * Created by Brad on 15/08/2015.
 */
public class FavouriteHelper extends DbHelper {
    public FavouriteHelper() {
        this(Utilities.getApplicationContext());
    }
    public FavouriteHelper(Context context) {
        super(context);
    }

    /**
     * Adds an item to favourites
     * @param id - the id of the item
     */
    public void add(String id, String posterPath) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(FavouriteEntry.COLUMN_ITEM_ID.name, id);
            values.put(FavouriteEntry.COLUMN_POSTER_PATH.name, posterPath);
            values.put(FavouriteEntry.COLUMN_DATE.name, (new Date()).getTime());

            // delete the old value
            db.replace(FavouriteEntry.TABLE_NAME, null, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * Removes an item from favourites
     * @param id - the id of the item
     */
    public void remove(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            // delete the old value
            db.delete(FavouriteEntry.TABLE_NAME, FavouriteEntry.COLUMN_ITEM_ID.name + " = ?", new String[]{id});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * Checks if an item has been favourited
     * @param id - the id of the item
     * @return true if has been favourited, false otherwise
     */
    public boolean isFavourite(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the old value
        Cursor cur = db.query(FavouriteEntry.TABLE_NAME, new String[] { FavouriteEntry.COLUMN_ITEM_ID.name }, FavouriteEntry.COLUMN_ITEM_ID.name + " = ?", new String[] { id }, null, null, null, "1");
        boolean res = cur.moveToFirst();
        db.close();
        cur.close();
        return res;
    }

    /**
     * Gets all the favourite items from the database
     * @return a list of arrays containing [0] => the item id, [1] => the poster path
     */
    public ArrayList<String[]> getAllFavourites() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cur = db.query(FavouriteEntry.TABLE_NAME, new String[]{FavouriteEntry.COLUMN_ITEM_ID.name, FavouriteEntry.COLUMN_POSTER_PATH.name},
                null, null, null, null, FavouriteEntry.COLUMN_DATE.name, null);

        ArrayList<String[]> res = null;
        if (cur.moveToFirst()) {
            res = new ArrayList<>();
            do {
                res.add(new String[]{
                        cur.getString(0),
                        cur.getString(1)
                });
            } while (cur.moveToNext());
        }

        db.close();
        cur.close();
        return res;
    }
}

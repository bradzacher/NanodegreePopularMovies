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

package au.com.zacher.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import au.com.zacher.popularmovies.RandomString;
import au.com.zacher.popularmovies.data.entry.ApiResultCacheEntry;
import au.com.zacher.popularmovies.data.entry.DbEntry;
import au.com.zacher.popularmovies.data.helper.ApiResultCacheHelper;
import au.com.zacher.popularmovies.data.helper.DbHelper;
import au.com.zacher.popularmovies.model.DisplayItem;

/**
 * Created by Brad on 12/07/2015.
 */
public class TestDb extends AndroidTestCase {
    private void deleteDb() {
        this.getContext().deleteDatabase(DbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() {
        this.deleteDb();
    }

    @Override
    protected void tearDown() {
        this.deleteDb();
    }

    public void testCreateDb() throws Throwable {
        SQLiteDatabase db = new DbHelper(this.getContext()).getWritableDatabase();
        assertTrue(db.isOpen());

        HashSet<String> tableNameSet = new HashSet<>();
        for (DbEntry e : DbContract.Entries) {
            tableNameSet.add(e.getTableName());
        }
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: the database was not created correctly", cursor.moveToFirst());

        do {
            tableNameSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Error: not all tables were created (missing: " + TextUtils.join(",", tableNameSet) + ")", tableNameSet.isEmpty());
        cursor.close();

        // check each table for all required columns
        for (DbEntry e : DbContract.Entries) {
            cursor = db.rawQuery("PRAGMA table_info(" + e.getTableName() + ")", null);
            assertTrue("Error: table " + e.getTableName() + " is missing", cursor.moveToFirst());

            HashSet<String> columnNameSet = new HashSet<>();
            for (DbColumn col : e.getColumns().values()) {
                columnNameSet.add(col.name);
            }

            int columnNameIndex = cursor.getColumnIndex("name");
            do {
                columnNameSet.remove(cursor.getString(columnNameIndex));
            } while (cursor.moveToNext());

            assertTrue("Error: table " + e.getTableName() + " was missing columns: " + TextUtils.join(",", columnNameSet), columnNameSet.isEmpty());
            cursor.close();
        }

        db.close();
    }

    public void testCacheEntry() {
        long seed = (new Date()).getTime();
        Random rand = new Random(seed);
        RandomString randomString = new RandomString(rand, 20);

        HashMap<String, DisplayItem> randomItems = new HashMap<>();

        int count = 5;
        for (int i = 0; i < count; i++) {
            randomItems.put(randomString.nextString(), new DisplayItem(
                    randomString.nextString(),
                    randomString.nextString(),
                    randomString.nextString(),
                    randomString.nextString()
            ));
        }

        ApiResultCacheHelper db = new ApiResultCacheHelper(this.getContext());

        //noinspection ToArrayCallWithZeroLengthArrayArgument
        LinkedList<String> keys = new LinkedList<>(Arrays.asList(randomItems.keySet().toArray(new String[0])));

        for (String key : keys) {
            DisplayItem item = randomItems.get(key);

            // add the item
            db.add(key, item);

            // get the item
            Cursor cursor = db.get(key);
            DisplayItem fromDb = this.getItem(cursor);

            // compare
            assertTrue(fromDb.equals(item));
        }
    }

    private DisplayItem getItem(Cursor cursor) {
        assertNotNull(cursor);
        assertNotSame(cursor.getCount(), 0);

        cursor.moveToFirst();

        DisplayItem results = (DisplayItem) ApiResultCacheEntry.getObjectFromRow(cursor, DisplayItem.class);

        cursor.close();

        return results;
    }
}

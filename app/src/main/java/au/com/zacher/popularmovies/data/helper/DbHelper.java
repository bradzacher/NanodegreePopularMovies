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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import au.com.zacher.popularmovies.data.DbContract;
import au.com.zacher.popularmovies.data.entry.DbEntry;

/**
 * Created by Brad on 27/06/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "popularmovies.db";
    public static final int DATABASE_VERSION = 4;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (DbEntry e : DbContract.Entries) {
            e.createTable(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (DbEntry e : DbContract.Entries) {
            e.upgradeTable(db, oldVersion, newVersion);
        }
    }
}
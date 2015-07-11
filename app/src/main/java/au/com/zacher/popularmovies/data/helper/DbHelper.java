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
    public static final int DATABASE_VERSION = 1;


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
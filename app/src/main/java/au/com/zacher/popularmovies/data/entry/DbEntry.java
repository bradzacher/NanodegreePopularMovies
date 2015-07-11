package au.com.zacher.popularmovies.data.entry;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import au.com.zacher.popularmovies.data.DbColumn;

public abstract class DbEntry {
    /**
     * Creates the associated table in the {@link SQLiteDatabase}
     * @param db the database to use to create
     */
    public abstract void createTable(SQLiteDatabase db);

    /**
     * Upgrades the associated table in the {@link SQLiteDatabase} to the given version
     * @param db the database to upgrade
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    public abstract void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Gets the name of the table associated with this {@link DbEntry}
     */
    public abstract String getTableName();

    /**
     * Gets a list of the {@link DbColumn} in the table
     */
    public abstract Map<String, DbColumn> getColumns();

    /**
     * Generates a basic create table string (no checks)
     */
    public String basicCreateTable() {
        String q = "CREATE TABLE " + this.getTableName() + " (";
        int i = 0;
        for (DbColumn col : this.getColumns().values()) {
            if (i != 0) {
                q += ", ";
            }
            q += col.columnString();
            i++;
        }
        q += ")";
        return q;

    }

    /**
     * Generates a basic drop table string (no checks)
     */
    public String basicDropTable() {
        return "DROP TABLE " + this.getTableName();
    }
}
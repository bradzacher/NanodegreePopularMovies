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

/**
 * Created by Brad on 27/06/2015.
 */
public class DbColumn {
    public final String name;
    public final ColumnType type;
    public final boolean isPrimaryKey;
    public final boolean isAutoincrement;
    public final boolean isNullAllowed;
    public final String defaultValue;

    /**
     * Creates the db column.
     * @param name the name of the column
     * @param type the type of the column
     */
    @SuppressWarnings("unused")
    public DbColumn(String name, ColumnType type) {
        this(name, type, false);
    }

    /**
     * Creates the db column.
     * @param name the name of the column
     * @param type the type of the column
     * @param isPrimaryKey true if this column is a primary key, false otherwise
     */
    @SuppressWarnings("unused")
    public DbColumn(String name, ColumnType type, boolean isPrimaryKey) {
        this(name, type, isPrimaryKey, true, null);
    }

    /**
     * Creates an auto incrementing integer db column.
     * @param name the name of the column
     * @param isPrimaryKey true if this column is a primary key, false otherwise
     */
    @SuppressWarnings("unused")
    public DbColumn(String name, boolean isPrimaryKey) {
        this.name = name;
        this.type = ColumnType.INTEGER;
        this.isPrimaryKey = isPrimaryKey;
        this.isNullAllowed = false;
        this.defaultValue = null;
        this.isAutoincrement = true;
    }

    /**
     * Creates the db column.
     * @param name the name of the column
     * @param type the type of the column
     * @param defaultValue the default value of the column - note that the caller is responsible for correct stringification of the default value
     */
    @SuppressWarnings("unused")
    public DbColumn(String name, ColumnType type, String defaultValue) {
        this(name, type, false, true, defaultValue);
    }

    /**
     * Creates a db column
     * @param name the name of the column
     * @param type the type of the column
     * @param isPrimaryKey true if this column is PK, false otherwise
     * @param isNullAllowed true if null is allowed, false otherwise
     * @param defaultValue the default value of the column - note that the caller is responsible for correct stringification of the default value
     */
    public DbColumn(String name, ColumnType type, boolean isPrimaryKey, boolean isNullAllowed, String defaultValue) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoincrement = false;
        this.isNullAllowed = isNullAllowed;
        this.defaultValue = defaultValue;
    }

    /**
     * Generates this column's string for use in a create table statement
     */
    public String columnString() {
        return this.name + " " +
                this.type.name() + " " +
                (this.isPrimaryKey ? "PRIMARY KEY " : "") +
                (this.isAutoincrement ? "AUTOINCREMENT " : "") +
                (this.defaultValue != null ? "DEFAULT " + this.defaultValue + " " : "") +
                (this.isNullAllowed ? "" : "NOT NULL");
    }
}

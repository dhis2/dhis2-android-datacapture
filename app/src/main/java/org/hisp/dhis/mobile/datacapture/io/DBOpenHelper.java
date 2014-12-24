package org.hisp.dhis.mobile.datacapture.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dhis2.db";
    private static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON;";
    private static final int DATABASE_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBSchema.CREATE_DASHBOARD_TABLE);
        db.execSQL(DBSchema.CREATE_DASHBOARD_ITEMS_TABLE);
        // execute sql commands on initialization

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBSchema.DROP_DASHBOARD_TABLE);
        db.execSQL(DBSchema.DROP_DASHBOARD_ITEMS_TABLE);
        // handle DB schema version changes
    }

    /**
     * Enabling support of ForeignKeys in SQLite database
     * each time it is being used. Works on android from 2.2
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL(ENABLE_FOREIGN_KEYS);
        }
    }
}

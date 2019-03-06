package com.example.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "inventory.db";
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static final String DATABASE_ALTER_ITEM_1 = "ALTER TABLE "
            + InventoryEntry.TABLE_NAME + " ADD COLUMN " + InventoryEntry.COLUMN_INVENTORY_IMAGE+ " BLOB;";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_INVENTORY_ITEM + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER DEFAULT 0, "
                + InventoryEntry.COLUMN_INVENTORY_IMAGE + " BLOB);";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);

        // create new table
        onCreate(db);
                if (oldVersion < 2) {
            db.execSQL(DATABASE_ALTER_ITEM_1);
        }
    }
}

package com.example.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {


    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY_LIST:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case INVENTORY_LIST:
                return InventoryEntry.INVENTORY_LIST_TYPE;
            case INVENTORY_ID:
                return  InventoryEntry.INVENTORY_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_LIST:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_LIST:
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY_LIST:
                return updateItem(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        String item = values.getAsString(InventoryEntry.COLUMN_INVENTORY_ITEM);

        if ( item == null ) { throw new IllegalArgumentException("There must be an item"); }

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if ( quantity < 0 ) { throw new IllegalArgumentException("There cannot be a negative amount"); }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryEntry.COLUMN_INVENTORY_ITEM)) {
            String item = values.getAsString(InventoryEntry.COLUMN_INVENTORY_ITEM);
            if ( item == null ) { throw new IllegalArgumentException("The must be an item"); }
        }
        if (values.containsKey((InventoryEntry.COLUMN_INVENTORY_QUANTITY))) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            if ( quantity < 0 ) { throw new IllegalArgumentException("There cannot be a negative amount"); }
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int id = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to update row for " + uri);
        }
        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null); }
        return id;
    }


    private static final int INVENTORY_LIST = 100;
    private static final int INVENTORY_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY_LIST);
        sUriMatcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }
}
package com.example.inventory.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String AUTHORITY =  "com.example.inventory.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_INVENTORY = "inventory";
    public static final String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir/com.example.android.inventory/inventory";
    public static final String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item/com.example.android.inventory/inventory";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private InventoryContract() { }

    /* Inner class that defines the table contents */
    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INVENTORY_ITEM = "item";
        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";
        public static final String COLUMN_INVENTORY_PHOTO = "photo";
        public static final String INVENTORY_LIST_TYPE =
                CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_INVENTORY;
        public static final String INVENTORY_ITEM_TYPE =
                CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_INVENTORY;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
    }
}

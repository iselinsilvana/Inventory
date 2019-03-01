package com.example.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor cursor) {super(context, cursor, 0);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ImageView imageListView = (ImageView) view.findViewById(R.id.image_list);
        TextView tvItemName = (TextView) view.findViewById(R.id.list_tv_item);
        TextView tvItemQuantity = (TextView) view.findViewById(R.id.list_tv_quantity);
        Button usedOneButton = (Button) view.findViewById(R.id.btn_used_one);

        String itemName = cursor.getString(cursor.getColumnIndex("item"));
        int itemQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        final long currentId = cursor.getLong(idColumnIndex);

        usedOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // skriv inn det som skjer her
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentId);
                Integer quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                if (quantity > 0) {
                    quantity -= 1;
                    ContentValues newValue = new ContentValues();
                    newValue.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
                    int rowsAffected = context.getContentResolver().update(currentUri, newValue, null, null);
                }
            }
        });

        tvItemName.setText(itemName);
        if (itemQuantity <= 0) {
            tvItemQuantity.setText("0");
            usedOneButton.setText("tomt");
        } else {
            tvItemQuantity.setText(itemQuantity + " stk");
        }
    }
}

package com.example.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

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

        final String itemName = cursor.getString(cursor.getColumnIndex("item"));
        final int itemQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
        byte[] byteImage = cursor.getBlob(cursor.getColumnIndex("image"));

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        final long currentId = cursor.getLong(idColumnIndex);

        usedOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // skriv inn det som skjer her
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentId);
                Log.v(LOG_TAG, "TEST: current uri is" + currentUri);
                int quantity = itemQuantity;
                //Integer quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                Log.v(LOG_TAG, "TEST: Cursor is currently pointing at " + itemName + " with quantity " + quantity);
                if (quantity > 0) {
                    quantity -= 1;
                    ContentValues newValue = new ContentValues();
                    newValue.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
                    int rowsAffected = context.getContentResolver().update(currentUri, newValue, null, null);
                    Log.v(LOG_TAG, "TEST: the new quantity is " + quantity);
                }
            }
        });

        tvItemName.setText(itemName);
        if (itemQuantity <= 0) {
            tvItemQuantity.setText("0");
            usedOneButton.setText("tomt");
            usedOneButton.setEnabled(false);
        } else {
            tvItemQuantity.setText(itemQuantity + " stk");
            usedOneButton.setText("Brukt ein");
            usedOneButton.setEnabled(true);
        }

        if ( byteImage != null ) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
            imageListView.setImageBitmap(bitmapImage);
        }
    }
}

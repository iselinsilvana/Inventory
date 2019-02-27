package com.example.inventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor cursor) {super(context, cursor, 0);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvItemName = (TextView) view.findViewById(R.id.list_tv_item);
        TextView tvItemQuantity = (TextView) view.findViewById(R.id.list_tv_quantity);
        Button usedOneButton = (Button) view.findViewById(R.id.btn_used_one);

        String itemName = cursor.getString(cursor.getColumnIndex("item"));
        String itemQuantity = cursor.getString(cursor.getColumnIndex("quantity"));

        tvItemName.setText(itemName);
        if (TextUtils.isEmpty(itemQuantity)) {
            tvItemQuantity.setText("0");
        } else {
            tvItemQuantity.setText(itemQuantity + " stk");
        }
    }
}

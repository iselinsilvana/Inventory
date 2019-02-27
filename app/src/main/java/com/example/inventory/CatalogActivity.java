package com.example.inventory;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.inventory.data.InventoryContract.InventoryEntry;
import com.example.inventory.data.InventoryDbHelper;

import android.support.annotation.NonNull;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.widget.Toast;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int INVENTORY_LOADER = 0;
    InventoryCursorAdapter mCursorAdapter;
    Button usedOneButton;
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Log.v(LOG_TAG, "TEST: starting empty editor activity");
                startActivity(intent);
            }
        });

        ListView inventoryListView = (ListView) findViewById(R.id.list_view_item);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                Log.v(LOG_TAG, "TEST: Item on list clicked, uri = " + currentUri);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        usedOneButton = (Button) findViewById(R.id.btn_used_one);
        usedOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedOne(int position, long id);
            }
        });
    }

    @Override
    protected void onStart() { super.onStart(); }

    private void usedOne(int position, long id) {
        Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int quantity = cursor.getInt(quantityColumnIndex);

        ContentValues value = new ContentValues();
        value.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
        int rowsAffected = getContentResolver().update(currentUri, value, null, null);
    }

    private void insertItem() {
        ContentValues values = new ContentValues(); // column names are the keys, the attributes (like paprika) are the values.
        values.put(InventoryEntry.COLUMN_INVENTORY_ITEM, "paprika");
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, 2);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage("Er du sikker på at du vil slette alt i lista?");
        builder.setPositiveButton("Slett alt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteWholeInventory();
            }
        });
        builder.setNegativeButton("Gå tilbake", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) { dialog.dismiss();}
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteWholeInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Det skjedde ein feil. Ingenting blei sletta", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lista er sletta", Toast.LENGTH_SHORT).show();
        }
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Define a projection that specifies which columns from the table we care about
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_ITEM,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY };

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCursorAdapter.swapCursor(null);
    }
}

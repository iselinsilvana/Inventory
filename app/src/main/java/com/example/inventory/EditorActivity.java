package com.example.inventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventory.data.InventoryContract.InventoryEntry;

import com.example.inventory.data.InventoryContract;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mItemNameEditText;
    private EditText mQuantityTextView;
    private Button mMinusButton;
    private Button mPlusButton;
    private Button mGalleryButton;
    private Button mPhotoButton;
    private ImageView mImageView;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE_ID = 234; // the number doesn't matter
    private static final int INVENTORY_LOADER = 0;
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private Uri mCurrentItemUri;
    private boolean mItemHasChanged;
    private Bitmap image = null;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentItemUri = getIntent().getData();
        Log.v(LOG_TAG, "TEST: The current uri is " + mCurrentItemUri);

       //gjere dette i onLoadFinished?
        if (mCurrentItemUri == null) {
            setTitle(R.string.title_new_item);
            invalidateOptionsMenu();
        }
        else {
            setTitle(R.string.title_edit_item);
            getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        }

        mItemNameEditText = (EditText) findViewById(R.id.editor_tv_item);
        mQuantityTextView = (EditText) findViewById(R.id.editor_tv_quantity);
        mMinusButton = (Button) findViewById(R.id.btn_minus);
        mPlusButton = (Button) findViewById(R.id.btn_plus);
        this.mImageView = (ImageView) findViewById(R.id.iv_item);
        mGalleryButton = (Button) findViewById(R.id.btn_gallery);
        mPhotoButton = (Button) findViewById(R.id.btn_photo);

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusOne();
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusOne();
            }
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hent bilde frå galleri
                Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK);
                photoGalleryIntent.setType("image/*");
                startActivityForResult(photoGalleryIntent, PICK_IMAGE_ID);
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }

            }
        });

        mItemNameEditText.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);
    }

    private void minusOne(){
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString) - 1;
        if (quantityInt < 0) {
            quantityInt = 0;
        }
        mQuantityTextView.setText(Integer.toString(quantityInt));
    }

    private void plusOne() {
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString) + 1;
        mQuantityTextView.setText(Integer.toString(quantityInt));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == RESULT_OK) {
           switch (requestCode) {
               case PICK_IMAGE_ID:
                   Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                   image = bitmap;
                   mImageView.setImageBitmap(image);
                   break;
               case CAMERA_REQUEST:
                   image = (Bitmap) data.getExtras().get("data");
                   mImageView.setImageBitmap(image);
               default:
                   super.onActivityResult(requestCode, resultCode, data);
                   break;
           }
       }
    }

/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        if (resultCode == Activity.RESULT_OK) {
            image = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(image);
        }
    }
    */


    // convert image between bitmap and byte array
    //from bitmap to byte[], to database
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        Log.v(LOG_TAG, "TEST: the image is bein converted to database.");
        return stream.toByteArray();
    }
    //from byte[] to bitmap, from database
    public static Bitmap getBitmap(byte[] byteImage) {
        if (byteImage == null) {
            Log.v(LOG_TAG, "TEST: the image in the database is a null array.");
        }
        return BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
    }


    private void saveItem() {
        String itemString = mItemNameEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity =Integer.parseInt(quantityString);
        }

        //Check if it's a new item or an edit
        //and check if all the fields in the editor are blank
        if (TextUtils.isEmpty(itemString)) {
            // Since there were no modified fields, we can return early and do not create a new item.
            Toast.makeText(this, getString(R.string.error_saving_nameless_item_toast), Toast.LENGTH_SHORT).show();
        } else {
            // Create a ContentValues object where column names are the keys,
            // and item attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_INVENTORY_ITEM, itemString);
            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            //checking if the image is empty or not
            if (image != null) {
                byte[] bytesImage = getBytes(image);
                values.put(InventoryEntry.COLUMN_INVENTORY_IMAGE, bytesImage);
            }

            if (mCurrentItemUri == null) {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the row ID is -1, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.error_saving_item), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast with the row ID.
                    Toast.makeText(this, getString(R.string.save_item_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.error_saving_item), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.save_item_success), Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.item_delete_confirmation);
        builder.setPositiveButton(R.string.action_delete_entry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Bli her", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {dialog.dismiss(); }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 1) {
                Toast.makeText(this, "Vara blei sletta", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Vara blei ikkje sletta", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // if this is a new item, hide the delete menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard_changes_negative, discardButtonClickListener);
        builder.setNegativeButton(R.string.discard_changes_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the table we care about
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_ITEM,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_IMAGE
        };
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mCurrentItemUri == null || cursor.getCount() < 1) {
            return;
        }
        if(cursor.moveToFirst()) {
            int itemColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_ITEM);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);

            String item = cursor.getString(itemColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            byte[] imageByte = cursor.getBlob(imageColumnIndex);
            if ( imageByte != null) {
                Log.v(LOG_TAG,"TEST: Retreiving image from database. length: " + imageByte.length);
            image = getBitmap(imageByte); }

            mItemNameEditText.setText(item);
            mQuantityTextView.setText(Integer.toString(quantity));
            mImageView.setImageBitmap(image);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mItemNameEditText.setText("");
        mQuantityTextView.setText("");
    }
}

package com.example.liya.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ProductInfoActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProductInfoActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private ProductDbHelper dbHelper;
    long currentItem;
    EditText productNameET;
    EditText priceET;
    EditText quantityET;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    EditText supplierNameET;
    EditText supplierEmailET;
    Button imageButton;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean itemHasChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productNameET = findViewById(R.id.product_name_et);
        priceET = findViewById(R.id.price_et);
        quantityET = findViewById(R.id.quantity_et);
        supplierNameET = findViewById(R.id.supplier_name_et);
        supplierEmailET = findViewById(R.id.supplier_email_et);
        decreaseQuantity = findViewById(R.id.decrease_quantity);
        increaseQuantity = findViewById(R.id.increase_quantity);
        imageButton = findViewById(R.id.choose_image);
        imageView = findViewById(R.id.image_preview);

        dbHelper = new ProductDbHelper(this);
        currentItem = getIntent().getLongExtra("item", 0);

        if (currentItem == 0) {
            setTitle(getString(R.string.add_item));
        } else {
            setTitle(getString(R.string.edit_item));
            addValuesToEditItem(currentItem);
        }

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractQuantity();
                itemHasChange = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuantity();
                itemHasChange = true;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
                itemHasChange = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!itemHasChange) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractQuantity() {
        String previousValueString = quantityET.getText().toString();
        int previousValue;
        if (!previousValueString.isEmpty() && !previousValueString.equals("0")) {
            previousValue = Integer.parseInt(previousValueString);
            quantityET.setText(String.valueOf(previousValue - 1));
        }
    }

    private void addQuantity() {
        String previousValueString = quantityET.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityET.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.i("onCreate", "menu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItem == 0) {
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.action_delete_item);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order_more);
            deleteOneItemMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // add/save item in DB
                if (!addItemToDb()) {
                    //user clicked button
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!itemHasChange) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductInfoActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order_more:
                //intent to supplier email
                showOrderConfirmationDialog();
                return true;
            case R.id.action_delete_item:
                // delete one product
                showDeleteConfirmationDialog(currentItem);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDb() {
        boolean isAllOk = true;
        if (!checkIfValueSet(productNameET, "name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(priceET, "price")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(quantityET, "quantity")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierNameET, "supplier name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierEmailET, "supplier email")) {
            isAllOk = false;
        }
        if (actualUri == null && currentItem == 0) {
            isAllOk = false;
            imageButton.setError("Missing image");
        }
        if (!isAllOk) {
            return false;
        }

        if (currentItem == 0) {
            Product item = new Product(
                    productNameET.getText().toString().trim(),
                    priceET.getText().toString().trim(),
                    Integer.parseInt(quantityET.getText().toString().trim()),
                    supplierNameET.getText().toString().trim(),
                    supplierEmailET.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertProduct(item);
        } else {
            int quantity = Integer.parseInt(quantityET.getText().toString().trim());
            dbHelper.updateProduct(currentItem, quantity);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValuesToEditItem(long item) {
        Cursor cursor = dbHelper.readProduct(item);
        cursor.moveToFirst();
        productNameET.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME)));
        priceET.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));
        quantityET.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY)));
        supplierNameET.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)));
        supplierEmailET.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE))));
        productNameET.setEnabled(false);
        priceET.setEnabled(false);
        supplierNameET.setEnabled(false);
        supplierEmailET.setEnabled(false);
        imageButton.setEnabled(false);
    }


    private int deleteItemFromDb(long item) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(item)};
        int rowsDeleted = database.delete(
                ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItemFromDb(item);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_email);
        builder.setNeutralButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmailET.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Proceed new order");
                String bodyMessage = "Dear " + supplierNameET.getText().toString() + '\n' +
                        "Please send us more " + productNameET.getText().toString().trim() +
                        "s.";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openImageSelector() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}

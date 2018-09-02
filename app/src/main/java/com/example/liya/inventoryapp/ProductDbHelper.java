package com.example.liya.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class ProductDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "products.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = ProductDbHelper.class.getCanonicalName();

    public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductContract.ProductEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DB_NAME");
        onCreate(db);
    }

    public void insertProduct(Product item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, item.getProductName());
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, item.getPrice());
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(ProductContract.ProductEntry.COLUMN_IMAGE, item.getImage());
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
    }

    public Cursor readStockProducts() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_IMAGE
        };
        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readProduct(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_IMAGE
        };
        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };

        Cursor cursor = db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateProduct(long currentItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(currentItemId) };
        db.update(ProductContract.ProductEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellOneProduct(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity -1;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, newQuantity);
        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(ProductContract.ProductEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }
}

package com.example.liya.inventoryapp;

import android.provider.BaseColumns;

public class ProductContract {
    public ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplierEmail";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_STOCK = "CREATE TABLE " +
                ProductContract.ProductEntry.TABLE_NAME + "(" +
                ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                ProductContract.ProductEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                ProductEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";
    }
}

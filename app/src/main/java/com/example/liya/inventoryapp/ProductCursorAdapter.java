package com.example.liya.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductCursorAdapter extends CursorAdapter {
    private static final String TAG = ProductCursorAdapter.class.getSimpleName();
    private final MainActivity activity;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.activity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView product_name = view.findViewById(R.id.list_product_name);
        TextView product_quantity = view.findViewById(R.id.list_quantity);
        TextView product_price = view.findViewById(R.id.list_price);
        Button product_sale = view.findViewById(R.id.list_sale);
        ImageView imageIV = view.findViewById(R.id.list_image_view);

        String name = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY));
        String price = "Price $" + cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE));

        imageIV.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE))));

        product_name.setText(name);
        product_quantity.setText(String.valueOf(quantity));
        product_price.setText(price);

        final long id = cursor.getLong(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        product_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnSale(id,
                        quantity);
            }
        });

    }
}

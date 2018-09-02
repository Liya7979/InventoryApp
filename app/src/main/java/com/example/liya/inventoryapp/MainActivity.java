package com.example.liya.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    ProductDbHelper dbHelper;
    ProductCursorAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbHelper = new ProductDbHelper(this);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductInfoActivity.class);
                startActivity(intent);
            }
        });
        final ListView listView = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStockProducts();

        adapter = new ProductCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) return;
                final int currentVisibleItem = view.getFirstVisiblePosition();
                if (currentVisibleItem > lastVisibleItem) {
                    fab.show();
                } else if (currentVisibleItem < lastVisibleItem) {
                    fab.hide();
                }
                lastVisibleItem = currentVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStockProducts());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, ProductInfoActivity.class);
        intent.putExtra("item", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneProduct(id, quantity);
        adapter.swapCursor(dbHelper.readStockProducts());
    }

}

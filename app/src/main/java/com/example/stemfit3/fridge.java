package com.example.stemfit3;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

public class fridge extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fridge);
        setupBottomNavigationView();

    }

    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(fridge.this, navigationbar);
        Menu menu = navigationbar.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void fridgeMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setGravity(Gravity.END);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add_item_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.add_item){
            AlertDialog.Builder builder = new AlertDialog.Builder(fridge.this);
            View view = getLayoutInflater().inflate(R.layout.fridge_dialog, null);
            builder.setTitle("Add new item");
            builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            return true;
        }
        else
            return false;
        }

    public void addItem(View v){
        LinearLayout myRoot = (LinearLayout) findViewById(R.id.fridgecontent);
        LayoutInflater inf = LayoutInflater.from(this);
        View child;
        child = inf.inflate(R.layout.fridge_item, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(285 * getResources().getDisplayMetrics().density), (int)(140 * getResources().getDisplayMetrics().density));
        params.setMargins(0, (int)(30* getResources().getDisplayMetrics().density), 0, 0);
        child.setLayoutParams(params);
        myRoot.addView(child);
        dialog.dismiss();
    }
}

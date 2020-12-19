package com.example.stemfit3;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {
    public static void enableNavigation(final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.settings:
                        Intent intent1 = new Intent(context, Settings.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent1);
                        break;
                    case R.id.kcal:
                        Intent intent2 = new Intent(context, kcal.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent2);
                        break;
                    case R.id.sleep:
                        Intent intent3 = new Intent(context, sleep.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent3);
                        break;
                    case R.id.water:
                        Intent intent4 = new Intent(context, water.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent4);
                        break;
                    case R.id.fridge:
                        Intent intent5 = new Intent(context, ScrollingActivitytest.class);
                        intent5.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent5);
                        break;
                }
                return false;
            }
        });
    }
}

package com.example.stemfit3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Settings extends AppCompatActivity {

    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        setupBottomNavigationView();

        logout = findViewById(R.id.logOut);

        logout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("remember", "false");
            editor.apply();

            Intent intent = new Intent(Settings.this, LogIn.class);
            startActivity(intent);
        });
    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(Settings.this, navigationBar);
        Menu menu = navigationBar.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    public void openUserInfo(View v){
        Intent intent = new Intent(this, UserInfo.class);
        startActivity(intent);
    }

    public void openChangePassword(View v){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }

}





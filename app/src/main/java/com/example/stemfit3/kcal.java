package com.example.stemfit3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class kcal extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kcal);
        setupBottomNavigationView();

    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(kcal.this, navigationbar);
        Menu menu = navigationbar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    public void menuopen(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.kcal_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                openMealAdd();
                return true;
            case R.id.create:
                openMealCreate();
                return true;
            default:
                return false;
        }
    }

    public void openMealAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_add, null);
        builder.setTitle("Add meal");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        //kcal_dialog_add dialog = new kcal_dialog_add();
        //dialog.show(getSupportFragmentManager(), "dialog_add");
    }

    public void openMealCreate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_create, null);
        builder.setTitle("Create meal");
        final Spinner units = (Spinner) view.findViewById(R.id.Units);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.kcal_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        units.setAdapter(adapter);
        units.setOnItemSelectedListener(this);
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        // kcal_dialog_create dialog = new kcal_dialog_create();
        // dialog.show(getSupportFragmentManager(), "dialog_create");

    }

    public void openMealInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_meal_info, null);
        builder.setTitle("Meal information");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
       // kcal_meal_info dialog = new kcal_meal_info();
       // dialog.show(getSupportFragmentManager(), "dialog_info");
    }

    public void onClickMeal(View v){
        switch(v.getId())
        {
            case R.id.customMealButton1:
                openMealInfo();
                break;

            case R.id.customMealButton2:
                openMealInfo();
                break;

            case R.id.customMealButton3:
                openMealInfo();
                break;

            case R.id.customMealButton4:
                openMealInfo();
                break;

            case R.id.customMealButton5:
                openMealInfo();
                break;
        }
    }

    public void addMeal(View v){
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        //delete this ^ when done
    }

    public void addIngredient(View v){
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        //delete this ^ when done
    }

    public void onClickAddMeal(View v){
        switch(v.getId())
        {
            case R.id.customAddMealButton1:
                Toast.makeText(this, "button1", Toast.LENGTH_SHORT).show();
                //delete this ^ when done
                break;

            case R.id.customAddMealButton2:
                Toast.makeText(this, "button2", Toast.LENGTH_SHORT).show();
                //delete this ^ when done
                break;

            case R.id.customAddMealButton3:
                Toast.makeText(this, "button3", Toast.LENGTH_SHORT).show();
                //delete this ^ when done
                break;

            case R.id.customAddMealButton4:
                Toast.makeText(this, "button4", Toast.LENGTH_SHORT).show();
                //delete this ^ when done
                break;

            case R.id.customAddMealButton5:
                Toast.makeText(this, "button5", Toast.LENGTH_SHORT).show();
                //delete this ^ when done
                break;
        }
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}






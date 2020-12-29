package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ListMenuPresenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserInfoRegister extends AppCompatActivity {

    private RadioGroup editGender;
    private RadioButton editMale, editFemale;
    private Spinner editActivity;
    private EditText editAge, editHeight, editWeight;
    private Button mContinue;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = currentUser.getUid();

    public static final String AGE = "Age";
    public static final String GENDER = "Gender";
    public static final String HEIGHT = "Height";
    public static final String WEIGHT = "Weight";

    private String Age;
    private int Gender;
    private String Height;
    private String Weight;

    SharedPreferences LastSelect;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_register);

        SharedPreferences sharedPreferencesFirstTime = getSharedPreferences(uid, MODE_PRIVATE);
        boolean firstTimeMessage = sharedPreferencesFirstTime.getBoolean(uid, true);

        if(!firstTimeMessage) {
            firstTimeWelcome();
        }

        LastSelect = getSharedPreferences(uid, Context.MODE_PRIVATE);
        editor = LastSelect.edit();

        final int LastClick = LastSelect.getInt("LastClick", 0);

        mContinue = (Button) findViewById(R.id.continueBtn);
        editAge = (EditText)findViewById(R.id.age_select);
        editGender = (RadioGroup)findViewById(R.id.radioGenderGroup);
        editMale = (RadioButton)findViewById(R.id.radioMale);
        editFemale = (RadioButton)findViewById(R.id.radioFemale);
        editHeight = (EditText)findViewById(R.id.set_height);
        editWeight = (EditText)findViewById(R.id.set_weight);
        editActivity = (Spinner)findViewById(R.id.set_activity);

        Context context=getApplicationContext();
        String[] foo_array = context.getResources().getStringArray(R.array.Activity);

        ArrayAdapter adapter = new ArrayAdapter<>
                (UserInfoRegister.this,R.layout.support_simple_spinner_dropdown_item, foo_array);
        editActivity.setAdapter(adapter);
        editActivity.setSelection(LastClick);

        editActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveData();

                        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
                        uid = currentUser.getUid();

                        String Age = editAge.getText().toString();
                        if(Age.isEmpty()) {
                            editAge.setError("This field is required");
                            editAge.requestFocus();
                        }
                        else{
                            mDatabase.child(uid).child("Age").setValue(Age);
                        }

                        String Height = editHeight.getText().toString();
                        if(Height.isEmpty()){
                            editHeight.setError("This field is required");
                            editHeight.requestFocus();
                        }
                        else{
                            mDatabase.child(uid).child("Height").setValue(Height);
                        }

                        String Weight = editWeight.getText().toString();
                        if(Weight.isEmpty()){
                            editWeight.setError("This field is required");
                            editWeight.requestFocus();
                        }
                        else{
                            mDatabase.child(uid).child("Weight").setValue(Weight);
                        }

                        String Activity = editActivity.getSelectedItem().toString();
                        mDatabase.child(uid).child("Activity").setValue(Activity);

                        String Gender1 = editMale.getText().toString();
                        String Gender2 = editFemale.getText().toString();
                        if(editMale.isChecked()){
                            mDatabase.child(uid).child("Gender").setValue(Gender1);
                        }
                        else if(editFemale.isChecked()){
                            mDatabase.child(uid).child("Gender").setValue(Gender2);
                        }
                        else if((!editMale.isChecked()) && (!editFemale.isChecked())) {
                            Toast.makeText(getApplicationContext(), "You must choose a gender", Toast.LENGTH_SHORT).show();
                        }
                        editor.putInt("LastClick", position).apply();
                        if((!Age.isEmpty()) && (!Height.isEmpty()) && (!Weight.isEmpty()) && ((editMale.isChecked()) || (editFemale.isChecked()))) {
                            startActivity(new Intent(UserInfoRegister.this, Settings.class));
                            Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        uid = currentUser.getUid();

        loadData();
        updateViews();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(AGE, editAge.getText().toString());

        editor.putInt(GENDER, editGender.getCheckedRadioButtonId());

        editor.putString(HEIGHT, editHeight.getText().toString());
        editor.putString(WEIGHT, editWeight.getText().toString());

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);


        Age = sharedPreferences.getString(AGE, "");

        Gender = sharedPreferences.getInt(GENDER, 0);

        Height = sharedPreferences.getString(HEIGHT, "");
        Weight = sharedPreferences.getString(WEIGHT, "");
    }

    public void updateViews() {
        editAge.setText(Age);
        editHeight.setText(Height);
        editWeight.setText(Weight);
        editGender.check(Gender);
    }

    public void exitUserInfo(View v){
        finish();
    }

    private void firstTimeWelcome() {
        new AlertDialog.Builder(UserInfoRegister.this)
                .setTitle("Welcome!")
                .setMessage("To finalize your registration, finish your profile." +
                        "\n\nThis information is used for calculations.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();

        SharedPreferences sharedPreferencesFirstTime = getSharedPreferences(uid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesFirstTime.edit();
        editor.putBoolean(uid, false);
        editor.apply();
    }
}
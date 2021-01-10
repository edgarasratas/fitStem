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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserInfoRegister extends AppCompatActivity {

    private RadioGroup editGender;
    private RadioButton editMale, editFemale;
    private Spinner editActivity;
    private EditText editAge, editHeight, editWeight;
    private Button mContinue;
    private DatabaseReference mDatabase;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid;

    {
        assert currentUser != null;
        uid = currentUser.getUid();
    }

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

            firstTimeWelcome();

        LastSelect = getSharedPreferences(uid, Context.MODE_PRIVATE);
        editor = LastSelect.edit();

        final int LastClick = LastSelect.getInt("LastPositionClicked", 0);

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

                        mDatabase.child(uid).child("neededCal").setValue("");
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
                        editor.putInt("LastPositionClicked", position).apply();
                        if((!Age.isEmpty()) && (!Height.isEmpty()) && (!Weight.isEmpty()) && ((editMale.isChecked()) || (editFemale.isChecked()))) {
                            startActivity(new Intent(UserInfoRegister.this, Settings.class));
                            Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                        }
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Water").child(uid);
                        ref.child("waterValue").setValue("250");
                        for (int i = 1; i < 8; i++) {
                            ref.child("day").child(String.valueOf(i)).setValue(0);
                        }
                        ref.child("currentWaterIntake").setValue(0);
                        if(editMale.isChecked()){
                            switch (Activity) {
                                case "Sedentary: little or no exercise":
                                    ref.child("neededWater").setValue("3750");
                                    break;
                                case "Light: exercise 1-3/week":
                                    ref.child("neededWater").setValue("3750");
                                    break;
                                case "Moderate: exercise 4-5/week":
                                    ref.child("neededWater").setValue("4000");
                                    break;
                                case "Active: intense exercise 3-4/week":
                                    ref.child("neededWater").setValue("4000");
                                    break;
                                case "Very active: intense exercise 6-7/week":
                                    ref.child("neededWater").setValue("4250");
                                    break;
                                case "Extra active: very intense exercise daily":
                                    ref.child("neededWater").setValue("4500");
                                    break;
                            }
                        }
                        if(editFemale.isChecked()){
                            switch (Activity) {
                                case "Sedentary: little or no exercise":
                                    ref.child("neededWater").setValue("2750");
                                    break;
                                case "Light: exercise 1-3/week":
                                    ref.child("neededWater").setValue("3000");
                                    break;
                                case "Moderate: exercise 4-5/week":
                                    ref.child("neededWater").setValue("3500");
                                    break;
                                case " Active: intense exercise 3-4/week":
                                    ref.child("neededWater").setValue("3750");
                                    break;
                                case "Very active: intense exercise 6-7/week":
                                    ref.child("neededWater").setValue("4000");
                                    break;
                                case "Extra active: very intense exercise daily":
                                    ref.child("neededWater").setValue("4250");
                                    break;
                            }
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
                .setPositiveButton("Got it", (dialog, which) -> {
                })
                .create().show();
    }
}
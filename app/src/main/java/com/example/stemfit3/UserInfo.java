package com.example.stemfit3;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;

public class UserInfo extends AppCompatActivity {

    private RadioButton editMale, editFemale;
    private Spinner editActivity;
    private EditText editAge, editHeight, editWeight;
    private Button mSave;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mSave = (Button) findViewById(R.id.Save);
        editAge = (EditText)findViewById(R.id.age_select);
        editMale = (RadioButton)findViewById(R.id.radioMale);
        editFemale = (RadioButton)findViewById(R.id.radioFemale);
        editHeight = (EditText)findViewById(R.id.set_height);
        editWeight = (EditText)findViewById(R.id.set_weight);
        editActivity = (Spinner)findViewById(R.id.set_activity);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        uid = currentUser.getUid();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Age = editAge.getText().toString();
                if(Age.isEmpty()) {
                    mDatabase.child(uid).child("Age").setValue("Not specified");
                }
                else{
                    mDatabase.child(uid).child("Age").setValue(Age);
                }

                String Height = editHeight.getText().toString();
                if(Height.isEmpty()){
                    mDatabase.child(uid).child("Height").setValue("Not specified");
                }
                else{
                    mDatabase.child(uid).child("Height").setValue(Height);
                }

                String Weight = editWeight.getText().toString();
                if(Weight.isEmpty()){
                    mDatabase.child(uid).child("Weight").setValue("Not specified");
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
                else{
                    mDatabase.child(uid).child("Gender").setValue(Gender2);
                }
            }

        });
    }

    public void exitUserInfo(View v){
        finish();
    }
}
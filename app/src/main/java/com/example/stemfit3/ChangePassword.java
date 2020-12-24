package com.example.stemfit3;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;

public class ChangePassword extends AppCompatActivity {

    private Button mSave;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mSave = (Button)findViewById(R.id.Save);
        currentPassword = (EditText) findViewById(R.id.CurrentPassword);
        newPassword = (EditText)findViewById(R.id.NewPassword);
        confirmPassword = (EditText)findViewById(R.id.ConfirmPassword);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String oldPass = currentPassword.getText().toString().trim();
                if(oldPass.isEmpty()) {
                    currentPassword.setError("This field is required");
                    currentPassword.requestFocus();
                    return;
                }
                String newPass = newPassword.getText().toString().trim();
                if(newPass.isEmpty()) {
                    newPassword.setError("This field is required");
                    newPassword.requestFocus();
                    return;
                }
                String confirmPass = confirmPassword.getText().toString().trim();
                if(confirmPass.isEmpty()) {
                    confirmPassword.setError("This field is required");
                    confirmPassword.requestFocus();
                    return;
                }
                if(newPass.length() < 8) {
                    newPassword.setError("Password length must be at least 8 characters...");
                    newPassword.requestFocus();
                }
                if(!newPass.equals(confirmPass)) {
                    confirmPassword.setError("Passwords do not match...");
                    confirmPassword.requestFocus();
                }

                confirmPassword(oldPass, newPass, confirmPass);
            }

            });
    }

    private void confirmPassword(String oldPass, String newPass, String confirmPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if((task.isSuccessful()) && (newPass.equals(confirmPass) && (newPass.length() >= 8))) {
                            user.updatePassword(newPass);
                            finish();
                            Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                        }
                        else if(!task.isSuccessful()) {
                            currentPassword.setError("Current password is incorrect...");
                            currentPassword.requestFocus();
                        }
                    }
                });
    }
    public void exitChangePassword (View v) {
        finish();
    }
}
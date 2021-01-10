package com.example.stemfit3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button mSave = (Button) findViewById(R.id.Save);
        currentPassword = (EditText) findViewById(R.id.CurrentPassword);
        newPassword = (EditText)findViewById(R.id.NewPassword);
        confirmPassword = (EditText)findViewById(R.id.ConfirmPassword);

        mSave.setOnClickListener(v -> {
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
        });
    }

    private void confirmPassword(String oldPass, String newPass, String confirmPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPass);
        user.reauthenticate(authCredential)
                .addOnCompleteListener(task -> {
                    if((task.isSuccessful()) && (newPass.equals(confirmPass) && (newPass.length() >= 8))) {
                        user.updatePassword(newPass);
                        finish();
                        Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                    }
                    else if(!task.isSuccessful()) {
                        currentPassword.setError("Current password is incorrect...");
                        currentPassword.requestFocus();
                    }
                });
    }
    public void exitChangePassword (View v) {
        finish();
    }
}
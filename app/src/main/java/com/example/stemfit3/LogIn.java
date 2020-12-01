package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    public CardView signIn;
    public EditText emailEditText, passwordEditText;
    public FirebaseAuth mAuth;
    public TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        signIn = (CardView) findViewById(R.id.cardView);
        signIn.setOnClickListener(this);

        emailEditText = (EditText)findViewById(R.id.usernameLogin);
        passwordEditText = (EditText) findViewById(R.id.passwordLogin);

        register = (TextView) findViewById(R.id.textView4);
        register.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textView4:
                startActivity(new Intent(this, Register.class));
                break;

            case R.id.cardView:
                userLogin();
                break;

        }
    }

    private void userLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please enter a valid email!");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length()<8) {
            passwordEditText.setError("Min passoword lenght is 6 charecters!");
            passwordEditText.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        startActivity(new Intent(LogIn.this,Settings.class));
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(LogIn.this, "Please verifily email", Toast.LENGTH_LONG).show();
                    }
                } else Toast.makeText(LogIn.this,"Failed to login, please check your credentials",Toast.LENGTH_LONG).show();

            }});
    }
}

package com.example.stemfit3;

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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Register extends AppCompatActivity implements View.OnClickListener {

    public EditText usernameEditText,passwordEditText,emailEditText,confirmPassEditText;
    public FirebaseAuth mAuth;
    public CardView register;
    public TextView account;
    public String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        confirmPassEditText  = findViewById(R.id.confirmPasswordRegister);
        mAuth =  FirebaseAuth.getInstance();

        register = (CardView) findViewById(R.id.cardView);
        register.setOnClickListener(this);

        usernameEditText = (EditText) findViewById(R.id.usernameRegister);
        passwordEditText= findViewById(R.id.passwordRegister);

        emailEditText = findViewById(R.id.emailRegister);

        account = (TextView) findViewById(R.id.textView4);
        account.setOnClickListener(this);


    }
//    public void logIn(View v){
//        Intent login = new Intent(this, LogIn.class);
//        startActivity(login);
//    }
//    public void register(View v){
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView4:
                startActivity(new Intent (this, LogIn.class));
                break;
            case R.id.cardView:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirm = confirmPassEditText.getText().toString().trim();
        TextView tempUsername = findViewById(R.id.usernameRegister);
        username = tempUsername.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(username.equals(snapshot.child("username").getValue().toString())){
                        usernameEditText.setError("Username is taken");
                        return;
                    }
             }

               }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if(email.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }
        if(usernameEditText.getText().toString().trim().isEmpty()){
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }
        if(!confirm.equals(password)){
            confirmPassEditText.setError("Passwords must match");
            confirmPassEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }
        if(password.length()<8){
            passwordEditText.setError("Password must be longer than 8 characters");
            return;
        }
        Log.i("tag1", "registerUser: ");
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("tag2", "registerUser: ");

                    User user = new User(usernameEditText.getText().toString().trim(),email);
                    FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this,"Please verify your email",Toast.LENGTH_LONG).show();
                                newUser.sendEmailVerification();
                                Intent intent = new Intent(getApplicationContext(),LogIn.class);
                                startActivity(intent);
                            }
                            else
                                Toast.makeText(Register.this, "Failed to register!",Toast.LENGTH_LONG).show();
                            Log.i("tag3", "registerUser: ");

                        }
                    });
                }else
                    Toast.makeText(Register.this, "Failed to register!",Toast.LENGTH_LONG).show();
            }
        });
        Log.i("tag4", "registerUser: ");


    }
}
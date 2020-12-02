package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    public CardView signIn;
    public EditText emailEditText, passwordEditText;
    public FirebaseAuth mAuth;
    public TextView register;
    String nickName,password;
    public List<User> users = new ArrayList<>();
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
    private void logIn(String email, String pass){
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
    private void userLogin() {


        Log.w("kek","1");
        nickName = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        if(nickName.isEmpty()){
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length()<8) {
            passwordEditText.setError("Min password lenght is 6 charecters!");
            passwordEditText.requestFocus();
            return;
        }
        if(nickName.contains("@"))
            logIn(nickName,password);
        else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int k=0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                           // logIn(nickName,password);
                            User user = snapshot.getValue(User.class);

                            users.add(user);
                         //   Log.w("kek",users.get(users.indexOf(nickName)).email);
                            if(users.get(k).username.equals(nickName)){
                                String mail = users.get(k).email.toString();
                                 logIn(mail,password);
                                 return;
                            }
                            k++;
                        }
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}


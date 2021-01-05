package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.DialogTitle;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.List;

public class LogIn extends AppCompatActivity implements View.OnClickListener{
    public CardView signIn;
    public EditText emailEditText, passwordEditText;
    public CheckBox rememberMe;
    public EditText emailTextBoxForgotPass;
    public FirebaseAuth mAuth;
    public TextView register;
    String nickName,password;
    public List<User> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = sharedPreferences.getString("remember","");
        if(checkbox.equals("true")) {
            Intent intent = new Intent(LogIn.this, Settings.class);
            startActivity(intent);
        }

        mAuth = FirebaseAuth.getInstance();

        signIn = (CardView) findViewById(R.id.cardView);
        signIn.setOnClickListener(this);

        emailEditText = (EditText)findViewById(R.id.usernameLogin);
        passwordEditText = (EditText) findViewById(R.id.passwordLogin);

        rememberMe = (CheckBox)findViewById(R.id.rememberMe);

        register = (TextView) findViewById(R.id.textView4);
        register.setOnClickListener(this);

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(compoundButton.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                }
                else if(!compoundButton.isChecked()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();
                }
            }
        });

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        SharedPreferences sharedPreferencesFirstTime = getSharedPreferences(uid, MODE_PRIVATE);
        boolean firstTimeMessage = sharedPreferencesFirstTime.getBoolean(uid, true);

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        if(!firstTimeMessage) {
                            firstTime();
                            startActivity(new Intent(LogIn.this, UserInfoRegister.class));
                        }
                        else {
                            startActivity(new Intent(LogIn.this,Settings.class));
                        }
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
    public void forgotPassword(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this)
                .setView(v)
                .setTitle("Reset password");
        View view = getLayoutInflater().inflate(R.layout.forgot_password_dialog, null);

        final EditText emailTextBoxForgotPass = (EditText)view.findViewById(R.id.emailTextInput);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Send", null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;

                String mail = emailTextBoxForgotPass.getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                databaseReference.orderByChild("email").equalTo(mail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            mAuth.sendPasswordResetEmail(mail);
                            Toast.makeText(getApplicationContext(),"Password reset link has been sent", Toast.LENGTH_SHORT).show();
                            if(!wantToCloseDialog)
                                dialog.dismiss();
                        }
                        else {
                            if(TextUtils.isEmpty(mail)) {
                                emailTextBoxForgotPass.setError("This field is required");
                                emailTextBoxForgotPass.requestFocus();
                            }
                            else if(!TextUtils.isEmpty(mail)) {
                                emailTextBoxForgotPass.setError("This email is either not registered or valid");
                                emailTextBoxForgotPass.requestFocus();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void openUserInfoRegister(View v) {
        Intent intent = new Intent(this, UserInfoRegister.class);
        startActivity(intent);
    }

    private void firstTime() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        SharedPreferences sharedPreferencesFirstTime = getSharedPreferences(uid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesFirstTime.edit();
        editor.putBoolean(uid, false);
        editor.apply();
    }
}


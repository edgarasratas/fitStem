package com.example.stemfit3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void logIn(View v){
        Intent login = new Intent(this, LogIn.class);
        startActivity(login);
    }
    public void register(View v){

    }
}
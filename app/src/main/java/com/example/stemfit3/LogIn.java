package com.example.stemfit3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
public class LogIn extends AppCompatActivity {
    private CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

    }
    public void register(View v){
        Intent signup = new Intent(this, Register.class);
        startActivity(signup);
    }

    public void openMain(View v){
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);

    }
}

package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Locale;

public class water extends AppCompatActivity {

    BarChart barchart;
    public Button waterIntakeButton;
    public int currentIntake=0;
    public TextView waterIntakeCounter;
    public SimpleDateFormat sfd;
    public SimpleDateFormat tempFormat;
    public TextView date;
    public FirebaseUser user;
    public String uId;
    public int neededWater;
    public double weekAvg;
    public DatabaseReference water = FirebaseDatabase.getInstance().getReference().child("Water");
    public Calendar dayOfWeek = Calendar.getInstance();
    public int Day = dayOfWeek.get(Calendar.DAY_OF_WEEK);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_water);
        setupBottomNavigationView();
        waterIntakeCounter = findViewById(R.id.waterIntakeCounter);
        waterIntakeButton = findViewById(R.id.waterIntakeButton);

        sfd = new SimpleDateFormat("EE",Locale.ENGLISH);
        date = findViewById(R.id.dateviewWater);
        tempFormat = new SimpleDateFormat("yyyy/MM/dd");
        date.setText(tempFormat.format(new Date()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        getInfo();

    }
    public void getInfo(){
        water.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                neededWater = Integer.parseInt(snapshot.child("neededWater").getValue().toString());
                waterIntakeButton.setText(snapshot.child("waterValue").getValue().toString());
                neededWater = Integer.parseInt(snapshot.child("neededWater").getValue().toString());
                currentIntake = Integer.parseInt(snapshot.child("currentWaterIntake").getValue().toString());
                waterIntakeCounter.setText(currentIntake+"/"+neededWater);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(water.this, navigationBar);
        Menu menu = navigationBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }
    public void addWater(View v){
        currentIntake += Integer.valueOf(waterIntakeButton.getText().toString().trim());
        if(currentIntake>10000&&currentIntake<28000){
            Toast.makeText(this,"You should stop drinking water!",Toast.LENGTH_SHORT).show();
        }
        if(currentIntake>30000){
            Toast.makeText(this,"You should be dead",Toast.LENGTH_SHORT).show();
            return;
        }
        waterIntakeCounter.setText(currentIntake+"/"+neededWater);
        water.child(uId).child("currentWaterIntake").setValue(currentIntake);
        switch (Day){
            case Calendar.MONDAY:
                addWaterToDay(Calendar.MONDAY);
                break;
            case Calendar.WEDNESDAY:
                addWaterToDay(Calendar.WEDNESDAY);
                break;
            case Calendar.TUESDAY:
                addWaterToDay(Calendar.TUESDAY);
                break;
            case Calendar.THURSDAY:
                addWaterToDay(Calendar.THURSDAY);
                break;
            case Calendar.FRIDAY:
                addWaterToDay(Calendar.FRIDAY);
                break;
            case Calendar.SATURDAY:
                addWaterToDay(Calendar.SATURDAY);
                break;
            case Calendar.SUNDAY:
                addWaterToDay(Calendar.SUNDAY);
                break;
        }

    }
    public void addWaterToDay(int currentDay){
        water.child(uId).child("day").child(String.valueOf(currentDay)).setValue(currentIntake);
    }
    public void cupSize(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_dialog_cup_size, null);
        builder.setTitle("Change cup size");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button changeWater250ml = dialog.findViewById(R.id.button250ml);
        Button changeWater500ml = dialog.findViewById(R.id.button500ml);
        Button changeWater1000ml = dialog.findViewById(R.id.button1000ml);
        Button changeWater1500ml = dialog.findViewById(R.id.button1500ml);
        Button changeWater2000ml = dialog.findViewById(R.id.button2000ml);


        changeWater250ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water.child(uId).child("waterValue").setValue(250);
                waterIntakeCounter.setText(String.valueOf(250));
                addWaterToDb(250);

            }
        });
        changeWater500ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water.child(uId).child("waterValue").setValue(500);
                waterIntakeCounter.setText(String.valueOf(500));
                addWaterToDb(500);

            }
        });
        changeWater1000ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water.child(uId).child("waterValue").setValue(1000);
                waterIntakeCounter.setText(String.valueOf(1000));
                addWaterToDb(1000);

            }
        });
        changeWater1500ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water.child(uId).child("waterValue").setValue(1500);
                waterIntakeCounter.setText(String.valueOf(1500));
                addWaterToDb(1500);

            }
        });
        changeWater2000ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water.child(uId).child("waterValue").setValue(2000);
                waterIntakeCounter.setText(String.valueOf(2000));
                addWaterToDb(2000);
            }
        });
    }
    public void addWaterToDb(int waterInt){
        water.child(uId).child("neededValue").setValue(waterInt);
    }
    public void reminderWater(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_dialog_reminder_frequency, null);
        final NumberPicker np = view.findViewById(R.id.reminderPickerWater);
        final NumberPicker from = view.findViewById(R.id.from);
        final NumberPicker to = view.findViewById(R.id.to);
        from.setMinValue(0);
        from.setMaxValue(23);
        from.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        to.setMinValue(0);
        to.setMaxValue(23);
        to.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        np.setMinValue(0);
        np.setMaxValue(3);
        np.setDisplayedValues(new String[] { "Every 15 minutes", "Every 30 minutes", "Every 45 minutes", "Every hour"});
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void statisticsWater(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_intake_report, null);

        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        barchart =  dialog.findViewById(R.id.barchart);

        ArrayList<Integer> dataVal = new ArrayList<Integer>();
        water.child(uId).child("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                weekAvg = 0;

                ArrayList<BarEntry> barEntryArrayList;
                ArrayList<String> labelName;
                labelName = new ArrayList<>();

                TextView avgConsumption = dialog.findViewById(R.id.averageWater);
                Log.i("TAG9", "statisticsWater: ");


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.i(String.valueOf(dataSnapshot.getChildrenCount()), "onDataChange: ");
                    int dayWater = Integer.parseInt(snapshot.getValue().toString());
                    Log.i(String.valueOf(dayWater), "onDataChange: ");
                    dataVal.add(dayWater);
                    weekAvg += dayWater;
                }
                DecimalFormat df = new DecimalFormat("#");
                String tempFormat = df.format(weekAvg/7);
                avgConsumption.setText(String.valueOf(tempFormat));


                labelName.add("M");
                labelName.add("T");
                labelName.add("W");
                labelName.add("T");
                labelName.add("F");
                labelName.add("S");
                labelName.add("S");


                barEntryArrayList = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    Log.i("TAG2", "statisticsWater: ");
                    int tempWater = dataVal.get(i);
                    Log.i("TAG2", "statisticsWater: ");
                    barEntryArrayList.add(new BarEntry(i,tempWater));
                    Log.i(String.valueOf(i), "statisticsWater: ");
                }
                BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Weekly intake");
                barDataSet.setColor(Color.GREEN);
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.5f);


                XAxis xAxis = barchart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setLabelCount(labelName.size());
                Description description = new Description();
                description.setText("");


                barchart.setDescription(description);
                barchart.setData(barData);
                barchart.animateY(2000);
                barchart.invalidate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }


}





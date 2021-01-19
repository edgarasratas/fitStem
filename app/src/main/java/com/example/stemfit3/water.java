package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class water extends AppCompatActivity {

    public BarChart barchart;
    public Button waterIntakeButton;
    public int currentIntake = 0;
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
    public TextView waterSwitchText;
    public TextView waterReminder;
    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    public static final String channelID = "20002";
    public androidx.appcompat.widget.SwitchCompat waterSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_water);
        setupBottomNavigationView();
        waterIntakeButton = findViewById(R.id.waterIntakeButton);

        sfd = new SimpleDateFormat("EE", Locale.ENGLISH);
        waterSwitch = (SwitchCompat) findViewById(R.id.waterSwitch);
        waterIntakeCounter = findViewById(R.id.waterIntakeCounter);
        waterIntakeButton = findViewById(R.id.waterIntakeButton);
        waterReminder = findViewById(R.id.waterReminderFrequencyText);
        waterSwitchText = findViewById(R.id.waterSwitchText);
        date = findViewById(R.id.dateviewWater);
        tempFormat = new SimpleDateFormat("yyyy/MM/dd");
        date.setText(tempFormat.format(new Date()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        getInfo();


        waterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waterSwitch.isChecked()){
                    setAlarm();
                    water.child(uId).child("notificationOption").setValue(true);
                }

                else{
                    cancelAlarm();
                    waterSwitchText.setText("Water intake reminder: OFF");
                    water.child(uId).child("notificationOption").setValue(false);

                }
            }
        });
    }

    private void cancelAlarm(){
        waterSwitchText.setText("Water intake reminder: ON");
        water.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Notification";
                    String description = "Channel for the reminder";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(channelID, name, importance);
                    channel.setDescription(description);

                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                    Intent intent = new Intent(water.this, waterNotification.class);
                    intent.setAction("Drink your water!");

                    pendingIntent = PendingIntent.getBroadcast(water.this, 0, intent, 0);
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance();
                    long time;
                    switch (snapshot.child("waterReminderTime").getValue().toString()) {
                        case "15":
                            Log.i("15", "onDataChange: ");
                            time = calendar.getTimeInMillis() + 6000;
                            //900000

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 60000, pendingIntent);
                            break;
                        case "30":
                            time = calendar.getTimeInMillis() + 900000 * 2;

                            Log.i("30", "onDataChange: ");

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 30 * 60000, pendingIntent);
                            break;
                        case "45":
                            time = calendar.getTimeInMillis() + 900000 * 3;

                            Log.i("45", "onDataChange: ");

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 45 * 60000, pendingIntent);
                            break;
                        case "60":
                            time = calendar.getTimeInMillis() + 900000 * 4;
                            Log.i("60", "onDataChange: ");

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 60 * 60000, pendingIntent);
                            break;
                    }
                    alarmManager.cancel(pendingIntent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAlarm(){
        waterSwitchText.setText("Water intake reminder: ON");
        water.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Notification";
                    String description = "Channel for the reminder";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(channelID, name, importance);
                    channel.setDescription(description);

                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                    Intent intent = new Intent(water.this,waterNotification.class);
                    intent.setAction("Drink your water!");

                   pendingIntent= PendingIntent.getBroadcast(water.this,0,intent,0);
                   alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance();
                    long time;
                   switch (snapshot.child("waterReminderTime").getValue().toString()){
                       case "15":
                           Log.i("15", "onDataChange: ");
                            time = calendar.getTimeInMillis()+900000;
                            //

                           alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time,900000,pendingIntent);
                           break;
                       case "30":
                           time = calendar.getTimeInMillis()+900000*2;

                           Log.i("30", "onDataChange: ");
                            long intervalTime30 = 900000 * 2;
                           alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time,intervalTime30,pendingIntent);
                           break;
                       case "45":
                           time = calendar.getTimeInMillis()+900000*3;
                           long intervalTime45 = 900000 * 2;

                           Log.i("45", "onDataChange: ");

                           alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time,intervalTime45,pendingIntent);
                           break;
                       case "60":
                           long intervalTime60 = 900000 * 2;
                           time = calendar.getTimeInMillis()+900000*4;
                           Log.i("60", "onDataChange: ");

                           alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,time,intervalTime60,pendingIntent);
                           break;
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        water.child(uId).child("notificationOption").setValue(true);
    }

    public void getInfo(){
        water.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                waterSwitch.setChecked(Boolean.valueOf(snapshot.child("notificationOption").getValue().toString()));
                neededWater = Integer.parseInt(snapshot.child("neededWater").getValue().toString());
                waterIntakeButton.setText(snapshot.child("waterValue").getValue().toString());
                neededWater = Integer.parseInt(snapshot.child("neededWater").getValue().toString());
//<<<<<<< HEAD
//                currentIntake = Integer.parseInt(snapshot.child("currentWaterIntake").getValue().toString());
//                waterIntakeCounter.setText(currentIntake+"/"+neededWater);
//=======
                currentIntake = Integer.parseInt(snapshot.child("day").child(String.valueOf(Day)).getValue().toString());
                waterIntakeCounter.setText(currentIntake+"/"+neededWater);
                waterReminder.setText("  Every " + snapshot.child("waterReminderTime").getValue().toString() + " minutes");
                if(waterSwitch.isChecked()){
                    String tempCheck= snapshot.child("notificationOption").getValue().toString();
                    if(tempCheck.equals("true")){
                        waterSwitchText.setText("Water intake reminder: ON");
                    }

                    else
                        waterSwitchText.setText("Water intake reminder: OFF");
                }


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
//<<<<<<< HEAD
//    public void addWater(View v){
//        currentIntake += Integer.valueOf(waterIntakeButton.getText().toString().trim());
//        if(currentIntake>10000&&currentIntake<28000){
//            Toast.makeText(this,"You should stop drinking water!",Toast.LENGTH_SHORT).show();
//        }
//        if(currentIntake>30000){
//            Toast.makeText(this,"You should be dead",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        waterIntakeCounter.setText(currentIntake+"/"+neededWater);
//        water.child(uId).child("currentWaterIntake").setValue(currentIntake);
//        switch (Day){
//=======
    public void addWater(View v) {
        if(currentIntake >= neededWater)
            cancelAlarm();
        currentIntake += Integer.valueOf(waterIntakeButton.getText().toString().trim());
        if (currentIntake > 10000 && currentIntake < 28000) {
            Toast.makeText(this, "You should stop drinking water!", Toast.LENGTH_SHORT).show();
        }
        if (currentIntake > 30000) {
            Toast.makeText(this, "You should be dead", Toast.LENGTH_SHORT).show();
            return;
        }
        waterIntakeCounter.setText(currentIntake + "/" + neededWater);
        water.child(uId).child("currentWaterIntake").setValue(currentIntake);
        switch (Day) {
            case Calendar.SUNDAY:
                addWaterToDay(Calendar.SUNDAY);
                Log.i("Monday", "1");
                break;
            case Calendar.MONDAY:
                addWaterToDay(Calendar.MONDAY);
                Log.i("Tuesday", "2");
                break;
            case Calendar.TUESDAY:
                addWaterToDay(Calendar.TUESDAY);
                Log.i("Wednesday", "3");
                break;
            case Calendar.WEDNESDAY:
                addWaterToDay(Calendar.WEDNESDAY);
                Log.i("Thursday", "4");
                break;
            case Calendar.THURSDAY:
                addWaterToDay(Calendar.THURSDAY);
                Log.i("Friday", "5");
                break;
            case Calendar.FRIDAY:
                addWaterToDay(Calendar.FRIDAY);
                Log.i("Saturday", "6");
                break;
            case Calendar.SATURDAY:
                addWaterToDay(Calendar.SATURDAY);
                Log.i("Sunday", "7");
                break;
        }



        }



    private void addWaterToDay(int day) {
        water.child(uId).child("day").child(String.valueOf(day)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tempWater = Integer.valueOf(snapshot.getValue().toString());
                tempWater += Integer.valueOf( waterIntakeButton.getText().toString());
                water.child(uId).child("day").child(String.valueOf(day)).setValue(tempWater);
                waterIntakeCounter.setText(tempWater + "/" +snapshot.getValue());
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


//    public void addWaterToDay(int currentDay){
//        water.child(uId).child("day").child(String.valueOf(currentDay)).setValue(currentIntake);
//    }
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
//<<<<<<< HEAD
//        final NumberPicker from = view.findViewById(R.id.from);
//        final NumberPicker to = view.findViewById(R.id.to);
//        from.setMinValue(0);
//        from.setMaxValue(23);
//        from.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
//        to.setMinValue(0);
//        to.setMaxValue(23);
//        to.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
//=======
//
//>>>>>>> Bugz
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
                String spinnerValue = String.valueOf(np.getValue());
                Log.i(spinnerValue, "onClick: ");
                switch (spinnerValue){
                    case  "0":
                        water.child(uId).child("waterReminderTime").setValue(15);
                        break;

                    case "1":
                        water.child(uId).child("waterReminderTime").setValue(30);
                        break;

                    case  "2":
                        water.child(uId).child("waterReminderTime").setValue(45);
                        break;

                    case  "3":
                        water.child(uId).child("waterReminderTime").setValue(60);
                        break;
                }
                if(waterSwitch.isChecked()){
                    setAlarm();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                water.child(uId).child("waterReminderTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        waterReminder.setText("  Every " + snapshot.getValue().toString() + " minutes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

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





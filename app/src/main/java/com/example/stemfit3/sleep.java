
package com.example.stemfit3;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

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
import java.util.Locale;
import java.util.Objects;

public class sleep extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public TextView dateText;
    public DigitalClock clockText;
    public SwitchCompat reminderSleep;
    public TextView bedTimeReminder;
    public AppCompatButton bedTime;
    public AppCompatButton wakeUpTime;
    public AppCompatButton sleepGoal;
    public AppCompatButton sleepStats;
    public ImageView sleepSwitch;
    public double weekAvg;
    public int currentSleepHour;
    public int currentSleepMinute;
    public Calendar dayOfWeek = Calendar.getInstance();
    public int Day = dayOfWeek.get(Calendar.DAY_OF_WEEK);
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    BarChart barchart;

    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid;

    {
        assert currentUser != null;
        uid = currentUser.getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sleep);
        setupBottomNavigationView();


        createNotificationChannel();

        SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);

        sleepSwitch = findViewById(R.id.sleepSwitchButton);
        bedTime = findViewById(R.id.bedTimeButton);
        wakeUpTime = findViewById(R.id.wakeTimeButton);
        dateText = findViewById(R.id.dateViewSleep);
        clockText = findViewById(R.id.fridgeClock);
        reminderSleep = findViewById(R.id.sleepSwitch);
        bedTimeReminder = findViewById(R.id.sleepSwitchText);
        sleepGoal = findViewById(R.id.sleepGoalButton);
        sleepStats = findViewById(R.id.sleepStatsButton);

        String switchStateOff = "Bedtime reminder: OFF";
        String switchStateOn = "Bedtime reminder: ON";

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat timeOnly = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String dateString = sdf.format(date);
        String timeString = timeOnly.format(date);

        //show present date
        dateText.setText(dateString);

        //show present time
        clockText.setText(timeString);

        reminderSleep.setTextOff("OFF");
        reminderSleep.setTextOn("ON");

        String bedTimeSP = sharedPreferences.getString("BEDTIME", "Bedtime\n22:00");
        bedTime.setText(bedTimeSP);

        String wakeUpTimeSP = sharedPreferences.getString("WAKE UP TIME", "Wake up time\n08:00");
        wakeUpTime.setText(wakeUpTimeSP);

        String sleepGoalSP = sharedPreferences.getString("SLEEP GOAL", "Sleep goal\n8 h 0 min");
        sleepGoal.setText(sleepGoalSP);

        reminderSleep.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences lastSwitchState;

            lastSwitchState = getSharedPreferences(uid, Context.MODE_PRIVATE);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            if(isChecked) {
                sleepSwitch.setOnClickListener(v -> reminderSleep());
                bedTimeReminder.setText(switchStateOn);
                mDatabase.child(uid).child("sleepReminder").setValue(switchStateOn);

                SharedPreferences.Editor editor = lastSwitchState.edit();
                editor.putBoolean("lastClick", true);
                editor.apply();
            }
            else {
                sleepSwitch.setOnClickListener(v -> reminderOffDialog());
                bedTimeReminder.setText(switchStateOff);
                mDatabase.child(uid).child("sleepReminder").setValue(switchStateOff);

                SharedPreferences.Editor editor = getSharedPreferences(uid, MODE_PRIVATE).edit();
                editor.putBoolean("lastClick", false);
                editor.apply();
            }
        });
        reminderSleep.setChecked(sharedPreferences.getBoolean("lastClick", true));
        getInfo();

                switch (Day) {
                    case Calendar.SUNDAY:
                        addSleepHoursToDay(Calendar.SUNDAY);
                        Log.i("Monday", "1");
                        break;
                    case Calendar.MONDAY:
                        addSleepHoursToDay(Calendar.MONDAY);
                        Log.i("Tuesday", "2");
                        break;
                    case Calendar.TUESDAY:
                        addSleepHoursToDay(Calendar.TUESDAY);
                        Log.i("Wednesday", "3");
                        break;
                    case Calendar.WEDNESDAY:
                        addSleepHoursToDay(Calendar.WEDNESDAY);
                        Log.i("Thursday", "4");
                        break;
                    case Calendar.THURSDAY:
                        addSleepHoursToDay(Calendar.THURSDAY);
                        Log.i("Friday", "5");
                        break;
                    case Calendar.FRIDAY:
                        addSleepHoursToDay(Calendar.FRIDAY);
                        Log.i("Saturday", "6");
                        break;
                    case Calendar.SATURDAY:
                        addSleepHoursToDay(Calendar.SATURDAY);
                        Log.i("Sunday", "7");
                        break;
                }
    }

    public void getInfo() {
        mDatabase.child("Sleep").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentSleepHour = Integer.parseInt(Objects.requireNonNull(snapshot.child("Bed time (hour)").getValue()).toString());
                currentSleepMinute = Integer.parseInt(Objects.requireNonNull(snapshot.child("Bed time (minute)").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addSleepHoursToDay(int currentDay) {
        mDatabase.child("Sleep").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hourBedTime = Objects.requireNonNull(snapshot.child("Bed time (hour)").getValue()).toString();
                String minuteBedTime = Objects.requireNonNull(snapshot.child("Bed time (minute)").getValue()).toString();
                String hourWakeUpTime = Objects.requireNonNull(snapshot.child("Wake up time (hour)").getValue()).toString();
                String minuteWakeUpTime = Objects.requireNonNull(snapshot.child("Wake up time (minute)").getValue()).toString();

                double sleptTimeMin = (Double.parseDouble(minuteBedTime) - Double.parseDouble(minuteWakeUpTime)) / 60;
                final double v = Integer.parseInt(hourWakeUpTime) - Integer.parseInt(hourBedTime) - sleptTimeMin;
                if((Integer.parseInt(hourBedTime) >= 0) && (Integer.parseInt(hourBedTime) < 10) && (Integer.parseInt(hourWakeUpTime) >= 10) && (Integer.parseInt(hourWakeUpTime) < 24)) {
                    double sleptTime = Math.abs(v);
                    mDatabase.child("Sleep").child(uid).child("day").child(String.valueOf(currentDay)).setValue(sleptTime);
                    Log.i("if", "1");
                }
                else if((Integer.parseInt(hourBedTime) >= 10) && (Integer.parseInt(hourBedTime) < 24) && (Integer.parseInt(hourWakeUpTime) >= 10) && (Integer.parseInt(hourWakeUpTime) < 24)){
                    double sleptTime = Math.abs(v);
                    mDatabase.child("Sleep").child(uid).child("day").child(String.valueOf(currentDay)).setValue(sleptTime);
                    Log.i("if", "2");
                }
                else if((Integer.parseInt(hourBedTime) >= 10) && (Integer.parseInt(hourBedTime) < 24) && (Integer.parseInt(hourWakeUpTime) >= 0) && (Integer.parseInt(hourWakeUpTime) < 10)){
                    double sleptTime = Math.abs((24 - Integer.parseInt(hourBedTime) + Integer.parseInt(hourWakeUpTime) - sleptTimeMin));
                    mDatabase.child("Sleep").child(uid).child("day").child(String.valueOf(currentDay)).setValue(sleptTime);
                    Log.i("if", "3");
                }
                else if((Integer.parseInt(hourBedTime) >= 0) && (Integer.parseInt(hourBedTime) < 10) && (Integer.parseInt(hourWakeUpTime) >= 0) && (Integer.parseInt(hourWakeUpTime) < 10)){
                    double sleptTime = Math.abs(v);
                    mDatabase.child("Sleep").child(uid).child("day").child(String.valueOf(currentDay)).setValue(sleptTime);
                    Log.i("if", "4");
                }
                else {
                    Log.i("if", "NULL");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupBottomNavigationView() {
        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(sleep.this, navigationBar);
        Menu menu = navigationBar.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

    public void bedTime(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_dialog_timepicker, null);
        builder.setTitle("Set Bed Time");
        AppCompatButton bedTime = findViewById(R.id.bedTimeButton);
        final NumberPicker nph = view.findViewById(R.id.hoursPicker);
        final NumberPicker npm = view.findViewById(R.id.minutesPicker);
        nph.setMinValue(0);
        nph.setMaxValue(23);
        npm.setMinValue(0);
        npm.setMaxValue(59);
        nph.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        npm.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        builder.setNegativeButton("close", (dialog, which) -> {
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            String uid = currentUser.getUid();
            if((nph.getValue() < 10) && (npm.getValue() < 10)) {
                String bedTimeText = "Bedtime\n" + "0" + nph.getValue() + ":0" + npm.getValue();
                bedTime.setText(bedTimeText);
                mDatabase.child(uid).child("day").child(String.valueOf(Calendar.DAY_OF_WEEK));
                mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                editor.putString("BEDTIME", "Bedtime\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                editor.apply();
            }
            else if((nph.getValue() < 10) && (npm.getValue() >= 10)) {
                String bedTimeText = "Bedtime\n0" + nph.getValue() + ":" + npm.getValue();
                bedTime.setText(bedTimeText);
                mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                editor.putString("BEDTIME", "Bedtime\n" + "0" + nph.getValue() + ":" + npm.getValue());
                editor.apply();
            }
            else if((nph.getValue() >= 10) && (npm.getValue() < 10)) {
                String bedTimeText = "Bedtime\n" + nph.getValue() + ":0" + npm.getValue();
                bedTime.setText(bedTimeText);
                mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                editor.putString("BEDTIME", "Bedtime\n" + nph.getValue() + ":0" + npm.getValue());
                editor.apply();
            }
            else if ((nph.getValue() >= 10) && (npm.getValue() >= 10)) {
                String bedTimeText = "Bedtime\n" + nph.getValue() + ":" + npm.getValue();
                bedTime.setText(bedTimeText);
                mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                editor.putString("BEDTIME", "Bedtime\n" + nph.getValue() + ":" + npm.getValue());
                editor.apply();
            }
            String bedTimeSP = sharedPreferences.getString("BEDTIME", "");
            bedTime.setText(bedTimeSP);
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void wakeUpTime(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_dialog_timepicker, null);
        builder.setTitle("Set Wake Time");
        AppCompatButton wakeUpTime = findViewById(R.id.wakeTimeButton);
        final NumberPicker nph = view.findViewById(R.id.hoursPicker);
        final NumberPicker npm = view.findViewById(R.id.minutesPicker);
        nph.setMinValue(0);
        nph.setMaxValue(23);
        npm.setMinValue(0);
        npm.setMaxValue(59);
        nph.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        npm.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        builder.setNegativeButton("close", (dialog, which) -> {

        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            DatabaseReference sleepDB = FirebaseDatabase.getInstance().getReference().child("Sleep");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            String uid = currentUser.getUid();
            if((nph.getValue() < 10) && (npm.getValue() < 10)) {
                String wakeUpTimeText = "Wake up time\n" + "0" + nph.getValue() + ":0" + npm.getValue();
                wakeUpTime.setText(wakeUpTimeText);
                sleepDB.child(uid).child("Wake up time (hour)").setValue(nph.getValue());
                sleepDB.child(uid).child("Wake up time (minute)").setValue(npm.getValue());
                mDatabase.child(uid).child("Wake up time").setValue("0" + nph.getValue() + ":0" + npm.getValue());
                editor.putString("WAKE UP TIME", "Wake up time\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                editor.apply();
            }
            else if((nph.getValue() < 10) && (npm.getValue() >= 10)) {
                String wakeUpTimeText = "Wake up time\n" + "0" + nph.getValue() + ":" + npm.getValue();
                wakeUpTime.setText(wakeUpTimeText);
                sleepDB.child(uid).child("Wake up time (hour)").setValue(nph.getValue());
                sleepDB.child(uid).child("Wake up time (minute)").setValue(npm.getValue());
                mDatabase.child(uid).child("Wake up time").setValue("0" + nph.getValue() + ":" + npm.getValue());
                editor.putString("WAKE UP TIME", "Wake up time\n" + "0" + nph.getValue() + ":" + npm.getValue());
                editor.apply();
            }
            else if((nph.getValue() >= 10) && (npm.getValue() < 10)) {
                String wakeUpTimeText = "Wake up time\n" + nph.getValue() + ":0" + npm.getValue();
                wakeUpTime.setText(wakeUpTimeText);
                sleepDB.child(uid).child("Wake up time (hour)").setValue(nph.getValue());
                sleepDB.child(uid).child("Wake up time (minute)").setValue(npm.getValue());
                mDatabase.child(uid).child("Wake up time").setValue(nph.getValue() + ":0" + npm.getValue());
                editor.putString("WAKE UP TIME", "Wake up time\n" + nph.getValue() + ":0" + npm.getValue());
                editor.apply();
            }
            else if ((nph.getValue() >= 10) && (npm.getValue() >= 10)) {
                String wakeUpTimeText = "Wake up time\n" + nph.getValue() + ":" + npm.getValue();
                wakeUpTime.setText(wakeUpTimeText);
                sleepDB.child(uid).child("Wake up time (hour)").setValue(nph.getValue());
                sleepDB.child(uid).child("Wake up time (minute)").setValue(npm.getValue());
                mDatabase.child(uid).child("Wake up time").setValue(nph.getValue() + ":" + npm.getValue());
                editor.putString("WAKE UP TIME", "Wake up time\n" + nph.getValue() + ":" + npm.getValue());
                editor.apply();
            }
            String bedTimeSP = sharedPreferences.getString("WAKE UP TIME", "");
            wakeUpTime.setText(bedTimeSP);
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sleepGoal(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_dialog_timepicker, null);
        builder.setTitle("Set Sleep Goal");
        AppCompatButton sleepGoal = findViewById(R.id.sleepGoalButton);
        final NumberPicker nph = view.findViewById(R.id.hoursPicker);
        final NumberPicker npm = view.findViewById(R.id.minutesPicker);
        nph.setMinValue(0);
        nph.setMaxValue(23);
        npm.setMinValue(0);
        npm.setMaxValue(59);
        nph.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        npm.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
        builder.setNegativeButton("close", (dialog, which) -> {

        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            String uid = currentUser.getUid();

            String sleepGoalText = "Sleep goal\n" + nph.getValue() + " h " + npm.getValue() + " min";

                sleepGoal.setText(sleepGoalText);
                mDatabase.child(uid).child("Sleep goal").setValue(nph.getValue() + " h " + npm.getValue() + " min");
                editor.putString("SLEEP GOAL", "Sleep goal\n" + nph.getValue() + " h " + npm.getValue() + " min");
                editor.apply();

            String bedTimeSP = sharedPreferences.getString("SLEEP GOAL", "");
            sleepGoal.setText(bedTimeSP);
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void statisticsSleep(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_statistics_dialog, null);

        builder.setNegativeButton("close", (dialog, which) -> {

        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        barchart = view.findViewById(R.id.barchart);

        ArrayList<Double> dataVal = new ArrayList<>();

        uid = currentUser.getUid();

        mDatabase.child("Sleep").child(uid).child("day").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weekAvg = 0;

                ArrayList<BarEntry> barEntryArrayList;
                ArrayList<String> xAxisLabel;
                xAxisLabel = new ArrayList<>();

                TextView avgSleep = dialog.findViewById(R.id.averageSleep);

                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    double sleepHours = Double.parseDouble(Objects.requireNonNull(snapshot1.getValue()).toString());
                    dataVal.add(sleepHours);
                    weekAvg += sleepHours;
                }
                DecimalFormat decimalFormat = new DecimalFormat("#");
                String tempFormat = decimalFormat.format(weekAvg/7);
                assert avgSleep != null;
                avgSleep.setText(tempFormat);

                xAxisLabel.add("M");
                xAxisLabel.add("T");
                xAxisLabel.add("W");
                xAxisLabel.add("T");
                xAxisLabel.add("F");
                xAxisLabel.add("S");
                xAxisLabel.add("S");

                barEntryArrayList = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    double tempSleep = dataVal.get(i);
                    barEntryArrayList.add(new BarEntry(i, (float) tempSleep));
                }

                BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Your sleep during the past week");
                barDataSet.setColor(Color.GREEN);
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.5f);


                XAxis xAxis = barchart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setLabelCount(xAxisLabel.size());
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

    public void reminderOffDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        builder.setTitle("!!!");
        builder.setMessage("You must have bed time reminder on!");
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void reminderSleep(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_bedtime_reminder, null);
        final NumberPicker np = view.findViewById(R.id.reminderPickerSleep);
        np.setMinValue(0);
        np.setMaxValue(4);

        np.setDisplayedValues(new String[] {"At Bedtime", "15 minutes before", "30 minutes before", "45 minutes before", "An hour before"});
        builder.setNegativeButton("close", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Confirm", (DialogInterface.OnClickListener) (dialog, which) -> {

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep");
            FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser1 != null;
            String uid1 = currentUser1.getUid();

            Calendar calendar = Calendar.getInstance();

            mDatabase.child(uid1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String bedTimeReminderHour = Objects.requireNonNull(snapshot.child("Bed time (hour)").getValue()).toString();
                    String bedTimeReminderMinute = Objects.requireNonNull(snapshot.child("Bed time (minute)").getValue()).toString();

                    if(np.getValue() == 0) {
                        Intent intent = new Intent(sleep.this, MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                        calendar.set(Calendar.SECOND, 0);

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                                pendingIntent);

                        reminderSleep.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if(!isChecked) {
                                alarmManager.cancel(pendingIntent);
                            }
                        });
                        Log.i("Test", "0");
                    }
                    else if(np.getValue() == 1) {
                        Intent intent = new Intent(sleep.this, MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                        calendar.set(Calendar.SECOND, 0);

                        int minutes_15 = 15 * 60000;

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - minutes_15, AlarmManager.INTERVAL_DAY,
                                pendingIntent);
                        Log.i("Test", "1");
                    }
                    else if(np.getValue() == 2) {
                        Intent intent = new Intent(sleep.this, MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                        calendar.set(Calendar.SECOND, 0);

                        int minutes_30 = 30 * 60000;

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - minutes_30, AlarmManager.INTERVAL_DAY,
                                pendingIntent);
                        Log.i("Test", "2");
                    }
                    else if(np.getValue() == 3) {
                        Intent intent = new Intent(sleep.this, MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                        calendar.set(Calendar.SECOND, 0);

                        int minutes_45 = 45 * 60000;

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - minutes_45, AlarmManager.INTERVAL_DAY,
                                pendingIntent);
                        Log.i("Test", "3");
                    }
                    else if(np.getValue() == 4) {
                        Intent intent = new Intent(sleep.this, MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                        calendar.set(Calendar.SECOND, 0);


                        int minutes_60 = 60 * 60000;

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - minutes_60, AlarmManager.INTERVAL_DAY,
                                pendingIntent);
                        Log.i("Test", "4");
                    }
                    else {
                        Log.i("Test", "5");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Channel for the reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }
}

package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
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

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;

public class sleep extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private NotificationManagerCompat notificationManager;

    public TextView dateText;
    public DigitalClock clockText;
    public SwitchCompat reminderSleep;
    public TextView bedTimeReminder;
    public AppCompatButton bedTime;
    public AppCompatButton wakeUpTime;
    public AppCompatButton sleepGoal;
    public ImageView sleepSwitch;
    public TextView sleepSwitchText;
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
        notificationManager = NotificationManagerCompat.from(this);

        SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);

        sleepSwitchText = findViewById(R.id.sleepSwitchText);
        sleepSwitch = findViewById(R.id.sleepSwitchButton);
        bedTime = findViewById(R.id.bedTimeButton);
        wakeUpTime = findViewById(R.id.wakeTimeButton);
        dateText = findViewById(R.id.dateviewSleep);
        clockText = findViewById(R.id.fridgeClock);
        reminderSleep = findViewById(R.id.sleepSwitch);
        bedTimeReminder = findViewById(R.id.sleepSwitchText);
        sleepGoal = findViewById(R.id.sleepGoalButton);

        boolean switchState = reminderSleep.isChecked();
        String switchStateOff = "Bedtime reminder: OFF";
        String switchStateOn = "Bedtime reminder: ON";

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeonly = new SimpleDateFormat("hh:mm");
        String dateString = sdf.format(date);
        String timeString = timeonly.format(date);
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

        reminderSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences lastSwitchState;

                lastSwitchState = getSharedPreferences(uid, Context.MODE_PRIVATE);

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
                if(isChecked) {
                    bedTimeReminder.setText(switchStateOn);
                    mDatabase.child(uid).child("sleepReminder").setValue(switchStateOn);

                    SharedPreferences.Editor editor = lastSwitchState.edit();
                    editor.putBoolean("lastClick", true);
                    editor.apply();
                }
                else {
                    bedTimeReminder.setText(switchStateOff);
                    mDatabase.child(uid).child("sleepReminder").setValue(switchStateOff);

                    SharedPreferences.Editor editor = getSharedPreferences(uid, MODE_PRIVATE).edit();
                    editor.putBoolean("lastClick", false);
                    editor.apply();
                }
            }
        });
        reminderSleep.setChecked(sharedPreferences.getBoolean("lastClick", true));
    }

    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(sleep.this, navigationbar);
        Menu menu = navigationbar.getMenu();
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
        nph.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();
                if((nph.getValue() < 10) && (npm.getValue() < 10)) {
                    bedTime.setText("Bedtime\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                    mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                    mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                    editor.putString("BEDTIME", "Bedtime\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                    editor.apply();
                }
                else if((nph.getValue() < 10) && (npm.getValue() >= 10)) {
                    bedTime.setText("Bedtime\n" + "0" + nph.getValue() + ":" + npm.getValue());
                    mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                    mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                    editor.putString("BEDTIME", "Bedtime\n" + "0" + nph.getValue() + ":" + npm.getValue());
                    editor.apply();
                }
                else if((nph.getValue() >= 10) && (npm.getValue() < 10)) {
                    bedTime.setText("Bedtime\n" + nph.getValue() + ":0" + npm.getValue());
                    mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                    mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                    editor.putString("BEDTIME", "Bedtime\n" + nph.getValue() + ":0" + npm.getValue());
                    editor.apply();
                }
                else if ((nph.getValue() >= 10) && (npm.getValue() >= 10)) {
                    bedTime.setText("Bedtime\n" + nph.getValue() + ":" + npm.getValue());
                    mDatabase.child(uid).child("Bed time (hour)").setValue(nph.getValue());
                    mDatabase.child(uid).child("Bed time (minute)").setValue(npm.getValue());
                    editor.putString("BEDTIME", "Bedtime\n" + nph.getValue() + ":" + npm.getValue());
                    editor.apply();
                }
                String bedTimeSP = sharedPreferences.getString("BEDTIME", "");
                bedTime.setText(bedTimeSP);
            }
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
        nph.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();
                if((nph.getValue() < 10) && (npm.getValue() < 10)) {
                    wakeUpTime.setText("Wake up time\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                    mDatabase.child(uid).child("Wake up time").setValue("0" + nph.getValue() + ":0" + npm.getValue());
                    editor.putString("WAKE UP TIME", "Wake up time\n" + "0" + nph.getValue() + ":0" + npm.getValue());
                    editor.apply();
                }
                else if((nph.getValue() < 10) && (npm.getValue() >= 10)) {
                    wakeUpTime.setText("Wake up time\n" + "0" + nph.getValue() + ":" + npm.getValue());
                    mDatabase.child(uid).child("Wake up time").setValue("0" + nph.getValue() + ":" + npm.getValue());
                    editor.putString("WAKE UP TIME", "Wake up time\n" + "0" + nph.getValue() + ":" + npm.getValue());
                    editor.apply();
                }
                else if((nph.getValue() >= 10) && (npm.getValue() < 10)) {
                    wakeUpTime.setText("Wake up time\n" + nph.getValue() + ":0" + npm.getValue());
                    mDatabase.child(uid).child("Wake up time").setValue(nph.getValue() + ":0" + npm.getValue());
                    editor.putString("WAKE UP TIME", "Wake up time\n" + nph.getValue() + ":0" + npm.getValue());
                    editor.apply();
                }
                else if ((nph.getValue() >= 10) && (npm.getValue() >= 10)) {
                    wakeUpTime.setText("Wake up time\n" + nph.getValue() + ":" + npm.getValue());
                    mDatabase.child(uid).child("Wake up time").setValue(nph.getValue() + ":" + npm.getValue());
                    editor.putString("WAKE UP TIME", "Wake up time\n" + nph.getValue() + ":" + npm.getValue());
                    editor.apply();
                }
                String bedTimeSP = sharedPreferences.getString("WAKE UP TIME", "");
                wakeUpTime.setText(bedTimeSP);
            }
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
        nph.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        npm.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences(uid, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();

                    sleepGoal.setText("Sleep goal\n" + nph.getValue() + " h " + npm.getValue() + " min");
                    mDatabase.child(uid).child("Sleep goal").setValue(nph.getValue() + " h " + npm.getValue() + " min");
                    editor.putString("SLEEP GOAL", "Sleep goal\n" + nph.getValue() + " h " + npm.getValue() + " min");
                    editor.apply();

                String bedTimeSP = sharedPreferences.getString("SLEEP GOAL", "");
                sleepGoal.setText(bedTimeSP);
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<BarEntry> dataValue1() {
        mDatabase.child("UserInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bedTime = snapshot.child("Bed time").getValue().toString();
                String wakeUpTime = snapshot.child("Wake up time").getValue().toString();

                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm");
                Date date1 = null;
                try {
                    date1 = dateFormat.parse(bedTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date2 = null;
                try {
                    date2 = dateFormat.parse(wakeUpTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = date2.getTime() - date1.getTime();
                int time = (int) (diff / 1000 / 3600);
/*                long timeInSeconds = diff / 1000;
                long hours, minutes, seconds;
                hours = timeInSeconds / 3600;
                timeInSeconds = timeInSeconds - (hours * 3600);
                minutes = timeInSeconds / 60;
                timeInSeconds = timeInSeconds - (minutes * 60);*/
                Log.i("Test", String.valueOf(time));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<BarEntry> dataVal = new ArrayList<>();
        dataVal.add(new BarEntry(0,9));
        dataVal.add(new BarEntry(1,8));
        dataVal.add(new BarEntry(2,6));
        dataVal.add(new BarEntry(3,5));
        dataVal.add(new BarEntry(4,4));
        dataVal.add(new BarEntry(5,8));
        dataVal.add(new BarEntry(6,12));
        return dataVal;
    }

    public void statisticsSleep(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_statistics_dialog, null);

        barchart = view.findViewById(R.id.barchart);

        BarDataSet barDataSet1 = new BarDataSet(dataValue1(), "Dataset 1");
        barDataSet1.setColor(Color.GREEN);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet1);

        barData.setBarWidth(0.5f);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("M");
        xAxisLabel.add("T");
        xAxisLabel.add("W");
        xAxisLabel.add("T");
        xAxisLabel.add("F");
        xAxisLabel.add("S");
        xAxisLabel.add("S");

        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barchart.getDescription().setEnabled(false);

        barchart.setData(barData);
        barchart.invalidate();

        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void reminderSleep(View v){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_bedtime_reminder, null);
        final NumberPicker np = view.findViewById(R.id.reminderPickerSleep);
        np.setMinValue(0);
        np.setMaxValue(4);
        np.setDisplayedValues(new String[] {"At Bedtime", "15 minutes before", "30 minutes before", "45 minutes before", "An hour before"});
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Sleep");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();

                Calendar calendar = Calendar.getInstance();

                mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bedTimeReminderHour = snapshot.child("Bed time (hour)").getValue().toString();
                        String bedTimeReminderMinute = snapshot.child("Bed time (minute)").getValue().toString();

                        if(np.getValue() == 0) {
                            Intent intent = new Intent(sleep.this, MyReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(sleep.this, 0, intent, 0);

                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(bedTimeReminderHour));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(bedTimeReminderMinute));
                            calendar.set(Calendar.SECOND, 0);

                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                                    pendingIntent);

                            Log.i("test", "1");
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

                            Log.i("test", "2");
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

                            Log.i("test", "3");
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

                            Log.i("test", "4");
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

                            Log.i("test", "5");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
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
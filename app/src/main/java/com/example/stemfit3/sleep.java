package com.example.stemfit3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class sleep extends AppCompatActivity {

    BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sleep);
        setupBottomNavigationView();

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

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void wakeTime(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_dialog_timepicker, null);
        builder.setTitle("Set Wake Time");
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

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<BarEntry> dataValue1(){
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

    public void statisticsSleep(View v){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(sleep.this);
        View view = getLayoutInflater().inflate(R.layout.sleep_bedtime_reminder, null);
        final NumberPicker np = view.findViewById(R.id.reminderPickerSleep);
        np.setMinValue(0);
        np.setMaxValue(4);
        np.setDisplayedValues(new String[] {"At Bedtime", "15 minutes before", "30 minutes before", "45 minutes before", "An hour before"});
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }

}
package com.example.stemfit3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class water extends AppCompatActivity {

    BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_water);
        setupBottomNavigationView();

    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationBar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(water.this, navigationBar);
        Menu menu = navigationBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }

    public void cupSize(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_dialog_cup_size, null);
        builder.setTitle("Change cup size");
        builder.setNegativeButton("close", (dialog, which) -> {

        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        builder.setNegativeButton("close", (dialog, which) -> {

        });
        builder.setPositiveButton("set", (dialog, which) -> {

        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ArrayList<BarEntry> dataValue1(){
        ArrayList<BarEntry> dataVal = new ArrayList<>();
        dataVal.add(new BarEntry(0,2750));
        dataVal.add(new BarEntry(1,2500));
        dataVal.add(new BarEntry(2,1750));
        dataVal.add(new BarEntry(3,1500));
        dataVal.add(new BarEntry(4,1000));
        dataVal.add(new BarEntry(5,2000));
        dataVal.add(new BarEntry(6,3000));
        return dataVal;
    }


    public void statisticsWater(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_intake_report, null);

        barchart = view.findViewById(R.id.barchart);

        BarDataSet barDataSet1 = new BarDataSet(dataValue1(), "DataSet 1");
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

        builder.setNegativeButton("close", (dialog, which) -> {

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





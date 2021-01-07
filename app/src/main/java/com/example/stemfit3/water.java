package com.example.stemfit3;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupMenu;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class water extends AppCompatActivity {

    BarChart barchart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_water);
        setupBottomNavigationView();

    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(water.this, navigationbar);
        Menu menu = navigationbar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
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
    }

    public void reminderWater(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(water.this);
        View view = getLayoutInflater().inflate(R.layout.water_dialog_reminder_frequency, null);
        final NumberPicker np = view.findViewById(R.id.reminderPickerWater);
        final NumberPicker from = view.findViewById(R.id.from);
        final NumberPicker to = view.findViewById(R.id.to);
        from.setMinValue(0);
        from.setMaxValue(23);
        from.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        to.setMinValue(0);
        to.setMaxValue(23);
        to.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, Settings.class);
        finish();
        startActivity(intent);
    }


}





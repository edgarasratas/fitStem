package com.example.stemfit3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.parser.Parser;
import org.w3c.dom.Text;

import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class fridge extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    AlertDialog dialog;
    public String uId;
    public String itemName;
    public TextView date;
    public FirebaseUser user;
    public fridgeItem item;
    public CalendarView itemExpDate;
    public Date startDate;
    public Date endDate;
    public long daysLeft;
    public SimpleDateFormat tempFormat;
    public String tempDate;
    public int tillExp = 0;

    public boolean checkForDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fridge);
        setupBottomNavigationView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        date = findViewById(R.id.fridgeDate);
        tempFormat = new SimpleDateFormat("yyyy/MM/dd");
        date.setText(tempFormat.format(new Date()));
        createItems();
        TextView expWarining = (TextView) findViewById(R.id.itemsExp);

    }

    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(fridge.this, navigationbar);
        Menu menu = navigationbar.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
    }

    public void fridgeMenu(View v) {

        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add_item_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.add_item){
            AlertDialog.Builder builder = new AlertDialog.Builder(fridge.this);
            View view = getLayoutInflater().inflate(R.layout.fridge_dialog, null);
            builder.setTitle("Add new item");
            builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            itemExpDate = (CalendarView) dialog.findViewById(R.id.itemExpDate);
            tempDate = tempFormat.format(new Date());
            checkForDays= false;
            itemExpDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                    if(month<10&&dayOfMonth<10)
                     tempDate =  year +"/0" + (month+1) + "/0" +dayOfMonth;
                    else if(month<10)
                        tempDate =  year +"/0" + (month+1) + "/" +dayOfMonth;
                    else if(dayOfMonth<10)
                        tempDate =  year +"/" + (month+1) + "/0" +dayOfMonth;
                    else
                        tempDate =  year +"/" + (month+1) + "/" +dayOfMonth;
                    Log.i("logch1", String.valueOf(checkForDays));
                    try {
                        startDate = tempFormat.parse(tempDate);
                        endDate = tempFormat.parse(date.getText().toString());
                        printDifference(startDate,endDate);
                        Log.i("logch2", String.valueOf(checkForDays));
                    }
                    catch (ParseException e){
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }
        else
            return false;
        }


    public void createItems(){
        LinearLayout myRoot = (LinearLayout) findViewById(R.id.fridgecontent);
        LayoutInflater inf = LayoutInflater.from(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(285 * getResources().getDisplayMetrics().density), (int)(140 * getResources().getDisplayMetrics().density));
        params.setMargins(0, (int)(30* getResources().getDisplayMetrics().density), 0, 0);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Item").child(uId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                View fridgeItem = inf.inflate(R.layout.fridge_item, null);
                fridgeItem = inf.inflate(R.layout.fridge_item,null);
                try {
                    Date startDate = tempFormat.parse(tempFormat.format(new Date()));
                    Date endDate = tempFormat.parse(snapshot.child("date").getValue().toString());
                    Log.i(startDate.toString(), "startDate: ");
                    Log.i(endDate.toString(), "endDate: ");

                    dayCountTillExp(endDate,startDate);
                    if(daysLeft<=0){
                        deleteItem(snapshot.child("fridgeItemName").getValue().toString());
                        return;
                    }
                    Log.i(String.valueOf(daysLeft), "onDataChange: ");
                    TextView tempDaysLeft = fridgeItem.findViewById(R.id.daysLeft);
                    tempDaysLeft.setText("Days left: "+daysLeft);
                    ProgressBar timeLeftProgress = fridgeItem.findViewById(R.id.expProgress);
                    if(daysLeft>10)
                        timeLeftProgress.setProgress(100);
                    else
                        timeLeftProgress.setProgress((int)daysLeft*10);
                }

                catch (ParseException e){
                    e.printStackTrace();
                }
                TextView tempItemName = fridgeItem.findViewById(R.id.itemName);
                TextView tempItemDate = fridgeItem.findViewById(R.id.expiringDate);
                tempItemName.setText(snapshot.child("fridgeItemName").getValue().toString());
                tempItemDate.setText("Expiring on: "+snapshot.child("date").getValue().toString());
                TextView tempButton = ((TextView)fridgeItem.findViewById(R.id.xButton));
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(snapshot.child("fridgeItemName").getValue().toString());
                        Intent intent = new Intent(getApplicationContext(),fridge.class);
                        startActivity(intent);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(),fridge.class);
                                startActivity(intent);
                            }
                        },350);
                    }
                });
               // TextView tempItemName = fridgeItem.findViewById(R.id.itemName);



                fridgeItem.setLayoutParams(params);
                myRoot.addView(fridgeItem);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }
    void dayCountTillExp(Date startDate, Date endDate){
        long difference = (startDate.getTime() - endDate.getTime())/100000;
        daysLeft = Integer.parseInt(String.valueOf(difference))/864;
        if(daysLeft<3){
            TextView tempText = (TextView) findViewById(R.id.itemsExp);
            tillExp++;
            tempText.setText(tillExp+" itmes nearing expiration!");
        }

    }
     void printDifference(Date startDate, Date endDate){
        long difference = (startDate.getTime() - endDate.getTime())/100000;
        daysLeft = Integer.parseInt(String.valueOf(difference))/864;
        if(daysLeft>0){
            checkForDays = true;
        }
        else
            checkForDays= false;
    }
    public void addItem(View v){
        EditText tempText = dialog.findViewById(R.id.fridgeItemName);
        itemName = tempText.getText().toString();
        if(itemName.matches("")){
            tempText.setError("Must not be empty");
            tempText.requestFocus();
            return;
        }
        if(!checkForDays){
            tempText.setError("Please select a proper date");
            return;
        }
        else
            tempText.setError(null);
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child("Item").child(uId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getValue().equals(itemName))
                    {
                        tempText.setError("Already exists");
                        tempText.requestFocus();
                        return;
                    }
                }

                item = new com.example.stemfit3.fridgeItem(itemName,tempDate);
                reference.child(item.fridgeItemName).setValue(item);

               // reference.child(itemName).setValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
           dialog.dismiss();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),fridge.class);
                    startActivity(intent);
                }
            },350);

    }
    void deleteItem(String fridgeItemName){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Item").child(uId).child(fridgeItemName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
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

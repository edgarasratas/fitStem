package com.example.stemfit3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Application;

import com.example.stemfit3.BottomNavigationViewHelper;
//import LoginRegister.LogIn;
//import LoginRegister.Register;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.stemfit3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.IgnoreExtraProperties;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class kcal extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    private EditText Ingredient;
    private EditText mealName;
    private TextView Recipy;
    private EditText Count;
    private Spinner Units;
    private Button AddIng;
    private TextView CaloryCount;
    private String username= "";
    public static final String UserName = "";
    public static final String SHARED_PREFS = "sharedPrefs";
    private String result= "";
    private int countIng = 0;
    private int totalCal = 0;
    public boolean exist;
    public double BMR=0;
    private DatabaseReference mealDatabase;
    private com.example.stemfit3.meal Meal;
    private Button newBtn;
    private int getTotalCal =0;
    LinearLayout linearLayout;
    List<com.example.stemfit3.ingredient> Ingredients;
    public boolean check = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kcal);
        setupBottomNavigationView();
        Ingredients  = new ArrayList<>();
        createMeals();
    }
    private void setupBottomNavigationView() {
        BottomNavigationView navigationbar = (BottomNavigationView) findViewById(R.id.nav);
        BottomNavigationViewHelper.enableNavigation(kcal.this, navigationbar);
        Menu menu = navigationbar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    public void menuopen(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.kcal_menu, popup.getMenu());
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                openMealAdd();
                return true;
            case R.id.create:
                userMealExists();
                openMealCreate();
                return true;
            default:
                return false;
        }
    }
    void createMeals(){
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference referenceUsername  = FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("username");
        referenceUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.getValue().toString();

                check = true;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Meal").child(username);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        linearLayout = (LinearLayout) findViewById(R.id.mealButtonsLayout);
                        int count = 0;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(check) {

                                for (int i = 0; i < Integer.parseInt(snapshot.child("mealCount").getValue().toString()); i++) {
                                    getTotalCal += Integer.parseInt(snapshot.child("totalKcal").getValue().toString());
                                    newBtn = new Button(linearLayout.getContext());
                                    newBtn.setText(snapshot.getKey());
                                    newBtn.setId(count);
                                    newBtn.setTextSize(20);
                                    newBtn.setBackground(ContextCompat.getDrawable(kcal.this, R.drawable.button_border));
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            (int)(290 * getResources().getDisplayMetrics().density),
                                            (int)(55 * getResources().getDisplayMetrics().density)
                                    );
                                    params.setMargins(0,(int)(30 * getResources().getDisplayMetrics().density),0,0);
                                    newBtn.setLayoutParams(params);
                                    linearLayout.addView(newBtn);

                                    Button tempButton = ((Button) findViewById(count));
                                    tempButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(v.getContext(),"Meal Addded",Toast.LENGTH_LONG).show();
                                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            openMealInfo(tempButton.getText().toString());
                                        }
                                    });
                                    count++;
                                }
                            }

                        }
                        check = false;
                        TextView tempText = (TextView) findViewById(R.id.textView9);
                        DatabaseReference neededCal = FirebaseDatabase.getInstance().getReference();
                        neededCal.child("UserInfo").child(uId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        double height,weight,age;
                                        String temp = snapshot.child("Height").getValue().toString();
                                        height = Integer.parseInt(snapshot.child("Height").getValue().toString());
                                        weight = Integer.parseInt(snapshot.child("Weight").getValue().toString());
                                        age = Integer.parseInt(snapshot.child("Age").getValue().toString());
                                if(snapshot.child("Gender").getValue().toString().equals("Male")){
                                    if(snapshot.child("Activity").getValue().toString().equals("Sedentary: little or no exercise")){
                                        BMR = (10*weight+6.25*height-5*age +5)*1.2;
                                    }
                                    else if(snapshot.child("Activity").getValue().toString().equals("Light: exercise 1-3/week")){
                                        BMR = (10*weight+6.25*height-5*age +5)*1.375;
                                    }
                                    else if(snapshot.child("Activity").getValue().toString().equals("Active: intense exercise 3-4/week")){
                                        BMR =( 10*weight+6.25*height-5*age +5)*1.5;
                                    }
                                    else if(snapshot.child("Activity").getValue().toString().equals("Moderate: exercise 4-5/week")){
                                        BMR = (10*weight+6.25*height-5*age +5)*1.6;
                                    }
                                    else if(snapshot.child("Activity").getValue().toString().equals("Very active: intense exercise 6-7/week")){
                                        BMR = (10*weight+6.25*height-5*age +5)*1.725;
                                    }
                                    else if(snapshot.child("Activity").getValue().toString().equals("Extra active: very intense exercise daily")){
                                        BMR = (10*weight+6.25*height-5*age +5)*1.9;
                                    }
                                }
                                else {
                                    if (snapshot.child("Activity").getValue().toString().equals("Sedentary: little or no exercise")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.2;
                                    } else if (snapshot.child("Activity").getValue().toString().equals("Light: exercise 1-3/week")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.375;
                                    } else if (snapshot.child("Activity").getValue().toString().equals("Active: intense exercise 3-4/week")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.5;
                                    } else if (snapshot.child("Activity").getValue().toString().equals("Moderate: exercise 4-5/week")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.6;
                                    } else if (snapshot.child("Activity").getValue().toString().equals("Very active: intense exercise 6-7/week")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.725;
                                    } else if (snapshot.child("Activity").getValue().toString().equals("Extra active: very intense exercise daily")) {
                                        BMR = (10 * weight + 6.25 * height - 5 * age - 161) * 1.9;
                                    }
                                }

                                DecimalFormat df = new DecimalFormat("#");
                                        String tempFormat  =df.format(BMR);
                                neededCal.child("UserInfo").child(uId).child("neededCal").setValue(tempFormat);
                                tempText.setText(String.valueOf(getTotalCal)+"/"+tempFormat);
                            }

                                @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //if(neededCal.child("Gender").getKey().equals("female"))


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void openMealAdd(){
        check = true;
        Log.i("buttons", "button1");
        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_add, null);
        builder.setTitle("Add meal");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference();
        reference.child("Meal").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.buttonlayout);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.i("children2", dataSnapshot.getChildren().toString());
                    if(check) {
                        newBtn = new Button(linearLayout.getContext());
                        newBtn.setText(snapshot.getKey());
                        newBtn.setId(count);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                (int)(270 * getResources().getDisplayMetrics().density),
                                (int)(45 * getResources().getDisplayMetrics().density)
                        );
                        params.setMargins(0,(int)(10 * getResources().getDisplayMetrics().density),0,0);
                        newBtn.setBackground(ContextCompat.getDrawable(kcal.this, R.drawable.edit_text_border));
                        newBtn.setLayoutParams(params);
                        linearLayout.addView(newBtn);

                    Button tempButton = ((Button) dialog.findViewById(count));
                    tempButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(),"Meal Addded",Toast.LENGTH_LONG).show();
                            DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("Meal").child(username).child(tempButton.getText().toString());
                            int a = Integer.parseInt(snapshot.child("mealCount").getValue().toString());
                            a++;
                            myDatabase.child("mealCount").setValue(a);
                            finish();
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(),kcal.class);
                            startActivity(intent);
                        }
                    });
                    tempButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            openMealInfo(snapshot.getKey());
                            return false;
                        }
                    });
                    count++;
                }
                }
                check = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //kcal_dialog_add dialog = new kcal_dialog_add();
        //dialog.show(getSupportFragmentManager(), "dialog_add");
    }
    public void openMealCreate(){

        result = "";
        //  Meal.totalKcal = 0;
        countIng = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_create, null);
        builder.setTitle("Create meal");
        final Spinner units = (Spinner) view.findViewById(R.id.Units);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.kCal_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        units.setAdapter(adapter);
        units.setOnItemSelectedListener(this);

        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        Ingredient = (EditText) dialog.findViewById(R.id.ingredient);
        Recipy = (TextView) dialog.findViewById(R.id.ingredientList);
        Count = (EditText) dialog.findViewById(R.id.ingredientCount);
        AddIng = (Button) dialog.findViewById(R.id.addIngredientButton);
        AddIng = (Button) dialog.findViewById(R.id.addIngredientButton);
        Units =  (Spinner) dialog.findViewById(R.id.Units);
        CaloryCount  = (TextView) dialog.findViewById(R.id.calorieCount);
        mealName = (EditText) dialog.findViewById(R.id.mealName);

        AddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( Ingredient.getText().toString().isEmpty()){
                    Ingredient.setError("Must not be empty!");
                    return;
                }

                String url = "https://api.wolframalpha.com/v2/query?input=";
                if(Units.getSelectedItem().equals("vnt"))
                    url +=  Count.getText().toString()+"+"+ Ingredient.getText().toString()+ "+" +"Kcal";
                else
                    url +=Ingredient.getText().toString() + "+" + Count.getText().toString()+"grams" + "+"+"Kcal";
                url+="&appid=WK328G-8UXH23UPJT&includepodid=Result";

                Log.i("url", url);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String allText = response.body().string();
                        kcal.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  Recipy.setText(allText);
                                Pattern pattern = Pattern.compile("alt='(.*?) Cal ");
                                Matcher matcher = pattern.matcher(allText);
                                if(matcher.find()){
                                    com.example.stemfit3.ingredient TempIngredient = new com.example.stemfit3.ingredient(Ingredient.getText().toString(),Integer.parseInt(matcher.group(1)),Integer.parseInt(Count.getText().toString()),Units.getSelectedItem().toString());
                                    Ingredients.add(TempIngredient);

                                    result += String.valueOf(countIng+1) + ". " +Ingredient.getText().toString() +" "+Count.getText().toString()+ Units.getSelectedItem().toString()+ " " +matcher.group(1) + "Kcal"+ '\n';
                                    Recipy.setText(result);
                                    totalCal += Integer.parseInt(matcher.group(1));

                                    CaloryCount.setText(String.valueOf(totalCal));
                                }
                                if(countIng>=4)
                                    Recipy.setMovementMethod(new ScrollingMovementMethod());
                                countIng++;
                            }
                        });

                    }
                });
            }
        });
    }

    public void openMealInfo(String btnName){
        TextView mealName;
        TextView recipe;
        TextView calories;
        AlertDialog.Builder builder = new AlertDialog.Builder(kcal.this);
        View view = getLayoutInflater().inflate(R.layout.kcal_dialog_meal_info, null);
        builder.setTitle("Meal0= information");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();

        dialog.show();

        mealName = (TextView) dialog.findViewById(R.id.mealNameInfo);

        recipe = (TextView) dialog.findViewById(R.id.ingredientListInfo);

        calories = (TextView) dialog.findViewById(R.id.mealCaloriesInfo);

        mealName.setText(btnName);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Meal").child(username).child(btnName);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                calories.setText(snapshot.child("totalKcal").getValue().toString()+" Kcal");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ingName,ingKcal,ingCount,ingType,rez = "",tempIngRez;
                int count = 1;
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ingCount=  snapshot.child("Count").getValue().toString();
                    ingKcal =  snapshot.child("Kcal").getValue().toString();
                    ingName =  snapshot.child("ingredientName").getValue().toString();
                    ingType =  snapshot.child("unitType").getValue().toString();

                    if(ingType.equals("g")){
                        tempIngRez = String.valueOf(count) + ". "+ingName + " " +ingCount +" "+ingKcal +"g "+ '\n';
                    }
                    else{
                        tempIngRez = String.valueOf(count) + ". "+ingName + " " +ingCount+" "+ingKcal +"vnt "+'\n';
                    }
                    count++;
                    rez+=tempIngRez;

                }
                if(count>=4)
                    recipe.setMovementMethod(new ScrollingMovementMethod());
                recipe.setText(rez);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void userMealExists(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Meal");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:  dataSnapshot.getChildren())
                    if(username.equals(snapshot.getValue().toString())) {
                        exist = true;
                        Log.i("url1", String.valueOf(exist));
                        return;
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addMeal(View v){

        if(mealName.getText().toString().isEmpty()){
            mealName.setError("Must not be empty!");
            return;
        }
        mealDatabase = FirebaseDatabase.getInstance().getReference();
        Meal = new com.example.stemfit3.meal(mealName.getText().toString(),totalCal,Ingredients);
        Log.i("url2", String.valueOf(exist));

        Log.i("check1", "Toast1");
        creteNewMeal();
        Log.i("check4", "Toast4");
        //    Toast.makeText(this, "Added meal", Toast.LENGTH_SHORT).show();
        //delete this ^ when done
    }
    public void creteNewMeal(){
        Log.i("url", username);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Meal").child(username);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(Meal.mealName).setValue(Meal);
                Log.i("check2", "Toast2");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.i("check3", "Toast3");

        return;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, LogIn.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}





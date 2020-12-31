package com.example.stemfit3;

import java.util.List;

public class meal {
    public String mealName;
    public int totalKcal;
    public int mealCount;
    public List<ingredient> ingredients;
    public meal(String mealName,int totalKcal, List<ingredient> ingredients){
        this.totalKcal = totalKcal;
        this.mealName = mealName;
        this.ingredients = ingredients;

    }
}


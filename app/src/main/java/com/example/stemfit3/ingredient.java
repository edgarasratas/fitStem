package com.example.stemfit3;

public class ingredient {
    public String ingredientName =  null;
    public int Kcal = 0;
    public int Count = 0;// kiek gramu/vienetu
    public String unitType = null;//gramai arba vienetai
    public ingredient(String ingredientName, int Kcal, int Count, String unitType){
        this.ingredientName = ingredientName;
        this.Kcal = Kcal;
        this.Count = Count;
        this.unitType = unitType;

    }
}

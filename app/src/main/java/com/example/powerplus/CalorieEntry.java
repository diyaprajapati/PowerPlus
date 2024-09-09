package com.example.powerplus;

public class CalorieEntry {
    private String date;
    private int calories;

    public CalorieEntry(String date, int calories) {
        this.date = date;
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public int getCalories() {
        return calories;
    }
}
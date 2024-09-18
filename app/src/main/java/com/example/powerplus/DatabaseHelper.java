package com.example.powerplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UsersDB";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_FOOD_ITEMS = "food_items";
    private static final String COLUMN_FOOD_ID = "id";
    private static final String COLUMN_FOOD_NAME = "name";
    private static final String COLUMN_FOOD_CALORIES = "calories";

    private static final String TABLE_FOOD_CONSUMPTION = "food_consumption";
    private static final String COLUMN_CONSUMPTION_ID = "id";
    private static final String COLUMN_CONSUMPTION_FOOD_ID = "food_id";
    private static final String COLUMN_CONSUMPTION_DATE = "date";
    private static final String COLUMN_CONSUMPTION_QUANTITY = "quantity";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USERNAME + " TEXT PRIMARY KEY, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_FOOD_ITEMS_TABLE = "CREATE TABLE " + TABLE_FOOD_ITEMS + "("
                + COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FOOD_NAME + " TEXT, "
                + COLUMN_FOOD_CALORIES + " INTEGER)";
        db.execSQL(CREATE_FOOD_ITEMS_TABLE);

        String CREATE_FOOD_CONSUMPTION_TABLE = "CREATE TABLE " + TABLE_FOOD_CONSUMPTION + "("
                + COLUMN_CONSUMPTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONSUMPTION_FOOD_ID + " INTEGER, "
                + COLUMN_CONSUMPTION_DATE + " TEXT, "
                + COLUMN_CONSUMPTION_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_CONSUMPTION_FOOD_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "("
                + COLUMN_FOOD_ID + "))";
        db.execSQL(CREATE_FOOD_CONSUMPTION_TABLE);

        // Create settings table
        String CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value INTEGER)";
        db.execSQL(CREATE_SETTINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_CONSUMPTION);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // returns true if the insertion was successful
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_USERNAME };
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    public long addFoodItem(String name, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_CALORIES, calories);
        return db.insert(TABLE_FOOD_ITEMS, null, values);
    }

    public long addFoodConsumption(int foodId, String date, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONSUMPTION_FOOD_ID, foodId);
        values.put(COLUMN_CONSUMPTION_DATE, date);
        values.put(COLUMN_CONSUMPTION_QUANTITY, quantity);
        return db.insert(TABLE_FOOD_CONSUMPTION, null, values);
    }

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FOOD_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                FoodItem foodItem = new FoodItem();
                foodItem.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_ID)));
                foodItem.setName(cursor.getString(cursor.getColumnIndex(COLUMN_FOOD_NAME)));
                foodItem.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_CALORIES)));
                foodItems.add(foodItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return foodItems;
    }

    public int getTotalCaloriesForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(f." + COLUMN_FOOD_CALORIES + " * c." + COLUMN_CONSUMPTION_QUANTITY
                + " / 100) as total_calories " +
                "FROM " + TABLE_FOOD_CONSUMPTION + " c " +
                "JOIN " + TABLE_FOOD_ITEMS + " f ON c." + COLUMN_CONSUMPTION_FOOD_ID + " = f." + COLUMN_FOOD_ID + " " +
                "WHERE c." + COLUMN_CONSUMPTION_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { date });
        int totalCalories = 0;
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0);
        }
        cursor.close();
        return totalCalories;
    }

    public List<CalorieEntry> getCaloriesForLastThreeDays() {
        List<CalorieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 3; i++) {
            String date = dateFormat.format(calendar.getTime());
            int calories = getTotalCaloriesForDate(date);
            entries.add(new CalorieEntry(date, calories));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return entries;
    }

    public List<CalorieEntry> getCaloriesForLastWeek() {
        List<CalorieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            int calories = getTotalCaloriesForDate(date);
            entries.add(new CalorieEntry(date, calories));
            calendar.add(Calendar.DAY_OF_MONTH, -1); // Move to the previous day
        }

        return entries;
    }

    public List<CalorieEntry> getCaloriesForLastMonth() {
        List<CalorieEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 30; i++) {
            String date = dateFormat.format(calendar.getTime());
            int calories = getTotalCaloriesForDate(date);
            entries.add(new CalorieEntry(date, calories));
            calendar.add(Calendar.DAY_OF_MONTH, -1); // Move to the previous day
        }

        return entries;
    }

    public void logDatabaseInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("DatabaseHelper", "Database Path: " + db.getPath());
        Log.d("DatabaseHelper", "Database Version: " + db.getVersion());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Log.d("DatabaseHelper", "Tables in the database:");
        while (cursor.moveToNext()) {
            Log.d("DatabaseHelper", cursor.getString(0));
        }
        cursor.close();
    }

    public void setDailyCalorieGoal(int goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", "daily_calorie_goal");
        values.put("value", goal);
        db.insertWithOnConflict("settings", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getDailyCalorieGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("settings", new String[] { "value" }, "key=?", new String[] { "daily_calorie_goal" },
                null, null, null);
        int goal = 2000; // Default value
        if (cursor.moveToFirst()) {
            goal = cursor.getInt(0);
        }
        cursor.close();
        return goal;
    }
}
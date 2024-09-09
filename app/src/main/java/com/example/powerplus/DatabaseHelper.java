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
    private static final int DATABASE_VERSION = 1;
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
                + "FOREIGN KEY(" + COLUMN_CONSUMPTION_FOOD_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COLUMN_FOOD_ID + "))";
        db.execSQL(CREATE_FOOD_CONSUMPTION_TABLE);
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
        String[] columns = {COLUMN_USERNAME};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

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

    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FOOD_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                FoodItem item = new FoodItem();
                item.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(COLUMN_FOOD_NAME)));
                item.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_CALORIES)));
                foodItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return foodItems;
    }

    public long addFoodConsumption(int foodId, String date, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONSUMPTION_FOOD_ID, foodId);
        values.put(COLUMN_CONSUMPTION_DATE, date);
        values.put(COLUMN_CONSUMPTION_QUANTITY, quantity);

        // Check if an entry already exists for this food and date
        String selection = COLUMN_FOOD_ID + " = ? AND " + COLUMN_CONSUMPTION_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(foodId), date};
        Cursor cursor = db.query(TABLE_FOOD_CONSUMPTION, null, selection, selectionArgs, null, null, null);

        long result;
        if (cursor.moveToFirst()) {
            // Entry exists, update the quantity
            int existingQuantity = cursor.getInt(cursor.getColumnIndex(COLUMN_CONSUMPTION_QUANTITY));
            int newQuantity = existingQuantity + quantity;
            values.put(COLUMN_CONSUMPTION_QUANTITY, newQuantity);
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_CONSUMPTION_ID));
            result = db.update(TABLE_FOOD_CONSUMPTION, values, COLUMN_CONSUMPTION_ID + " = ?", new String[]{String.valueOf(id)});
        } else {
            // No existing entry, insert a new one
            result = db.insert(TABLE_FOOD_CONSUMPTION, null, values);
        }

        cursor.close();
        return result;
    }

    public int getTotalCaloriesForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(fi." + COLUMN_FOOD_CALORIES + " * fc." + COLUMN_CONSUMPTION_QUANTITY + ") as total_calories " +
                "FROM " + TABLE_FOOD_CONSUMPTION + " fc " +
                "JOIN " + TABLE_FOOD_ITEMS + " fi ON fc." + COLUMN_CONSUMPTION_FOOD_ID + " = fi." + COLUMN_FOOD_ID + " " +
                "WHERE fc." + COLUMN_CONSUMPTION_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});
        int totalCalories = 0;
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(cursor.getColumnIndex("total_calories"));
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
}
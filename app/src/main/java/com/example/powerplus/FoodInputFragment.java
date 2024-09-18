package com.example.powerplus;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FoodInputFragment extends Fragment {

    private Spinner spFoodItems;
    private EditText etFoodQuantity;
    private Button btnAddSelectedFood;
    private EditText etNewFoodName;
    private EditText etNewFoodCalories;
    private Button btnAddNewFood;
    private TextView tvFoodList;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_input, container, false);

        spFoodItems = view.findViewById(R.id.spFoodItems);
        etFoodQuantity = view.findViewById(R.id.etFoodQuantity);
        btnAddSelectedFood = view.findViewById(R.id.btnAddSelectedFood);
        etNewFoodName = view.findViewById(R.id.etNewFoodName);
        etNewFoodCalories = view.findViewById(R.id.etNewFoodCalories);
        btnAddNewFood = view.findViewById(R.id.btnAddNewFood);
        tvFoodList = view.findViewById(R.id.tvFoodList);

        databaseHelper = new DatabaseHelper(getContext());

        loadFoodItems();

        btnAddSelectedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSelectedFood();
            }
        });

        btnAddNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFood();
            }
        });

        return view;
    }

    private void loadFoodItems() {
        List<FoodItem> foodItems = databaseHelper.getAllFoodItems();
        ArrayAdapter<FoodItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                foodItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFoodItems.setAdapter(adapter);
    }

    private void addSelectedFood() {
        FoodItem selectedFood = (FoodItem) spFoodItems.getSelectedItem();
        String quantityStr = etFoodQuantity.getText().toString();
        if (selectedFood != null && !quantityStr.isEmpty()) {
            int quantity = Integer.parseInt(quantityStr);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            long result = databaseHelper.addFoodConsumption(selectedFood.getId(), currentDate, quantity);
            if (result != -1) {
                Toast.makeText(getContext(), "Food consumption added", Toast.LENGTH_SHORT).show();
                updateFoodList();
                etFoodQuantity.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to add food consumption", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please select a food item and enter quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewFood() {
        String foodName = etNewFoodName.getText().toString();
        String caloriesStr = etNewFoodCalories.getText().toString();
        if (!foodName.isEmpty() && !caloriesStr.isEmpty()) {
            int calories = Integer.parseInt(caloriesStr);
            long result = databaseHelper.addFoodItem(foodName, calories);
            if (result != -1) {
                Toast.makeText(getContext(), "New food item added", Toast.LENGTH_SHORT).show();
                loadFoodItems();
                etNewFoodName.setText("");
                etNewFoodCalories.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to add new food item", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please enter food name and calories", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFoodList() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int totalCalories = databaseHelper.getTotalCaloriesForDate(currentDate);
        tvFoodList.setText("Total calories consumed today: " + totalCalories);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFoodList();
    }
}
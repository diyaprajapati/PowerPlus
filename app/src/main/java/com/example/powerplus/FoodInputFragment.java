package com.example.powerplus;

import android.os.Bundle;
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

    private List<FoodItem> foodItems = new ArrayList<>();
    private ArrayAdapter<FoodItem> foodAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_input, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        spFoodItems = view.findViewById(R.id.spFoodItems);
        etFoodQuantity = view.findViewById(R.id.etFoodQuantity);
        btnAddSelectedFood = view.findViewById(R.id.btnAddSelectedFood);
        etNewFoodName = view.findViewById(R.id.etNewFoodName);
        etNewFoodCalories = view.findViewById(R.id.etNewFoodCalories);
        btnAddNewFood = view.findViewById(R.id.btnAddNewFood);
        tvFoodList = view.findViewById(R.id.tvFoodList);

        loadFoodItems();

        foodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, foodItems);
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFoodItems.setAdapter(foodAdapter);

        btnAddSelectedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodItem selectedFood = (FoodItem) spFoodItems.getSelectedItem();
                String quantity = etFoodQuantity.getText().toString();
                if (selectedFood != null && !quantity.isEmpty()) {
                    addFoodConsumption(selectedFood.getId(), Integer.parseInt(quantity));
                    etFoodQuantity.setText("");
                    updateFoodList();
                } else {
                    Toast.makeText(getContext(), "Please select a food item and enter quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFoodName = etNewFoodName.getText().toString();
                String newFoodCalories = etNewFoodCalories.getText().toString();
                if (!newFoodName.isEmpty() && !newFoodCalories.isEmpty()) {
                    long newFoodId = databaseHelper.addFoodItem(newFoodName, Integer.parseInt(newFoodCalories));
                    if (newFoodId != -1) {
                        loadFoodItems();
                        etNewFoodName.setText("");
                        etNewFoodCalories.setText("");
                        Toast.makeText(getContext(), "New food item added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to add new food item", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please enter food name and calories", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadFoodItems() {
        foodItems.clear();
        foodItems.addAll(databaseHelper.getAllFoodItems());
        if (foodAdapter != null) {
            foodAdapter.notifyDataSetChanged();
        }
    }

    private void addFoodConsumption(int foodId, int quantity) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long result = databaseHelper.addFoodConsumption(foodId, currentDate, quantity);
        if (result != -1) {
            Toast.makeText(getContext(), "Food consumption added/updated", Toast.LENGTH_SHORT).show();
            updateFoodList(); // Make sure to update the displayed food list
        } else {
            Toast.makeText(getContext(), "Failed to add/update food consumption", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFoodList() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int totalCalories = databaseHelper.getTotalCaloriesForDate(currentDate);
        tvFoodList.setText("Total calories consumed today: " + totalCalories);
    }
}
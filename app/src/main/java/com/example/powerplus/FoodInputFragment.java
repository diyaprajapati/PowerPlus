package com.example.powerplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class FoodInputFragment extends Fragment {

    private EditText etFoodName;
    private EditText etFoodAmount;
    private Button btnAddFood;
    private TextView tvFoodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_input, container, false);

        etFoodName = view.findViewById(R.id.etFoodName);
        etFoodAmount = view.findViewById(R.id.etFoodAmount);
        btnAddFood = view.findViewById(R.id.btnAddFood);
        tvFoodList = view.findViewById(R.id.tvFoodList);

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = etFoodName.getText().toString();
                String foodAmount = etFoodAmount.getText().toString();

                // TODO: Calculate protein and energy based on food input
                // For now, we'll just add the food to the list
                String currentList = tvFoodList.getText().toString();
                tvFoodList.setText(currentList + "\n" + foodName + " - " + foodAmount + "g");

                // Clear input fields
                etFoodName.setText("");
                etFoodAmount.setText("");
            }
        });

        return view;
    }
}
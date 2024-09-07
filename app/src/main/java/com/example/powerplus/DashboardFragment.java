package com.example.powerplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private TextView tvCalorieGoal;
    private TextView tvCaloriesConsumed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);

        // TODO: Load actual data from SharedPreferences or database
        tvCalorieGoal.setText("Daily Calorie Goal: 2000");
        tvCaloriesConsumed.setText("Calories Consumed Today: 1500");

        return view;
    }
}
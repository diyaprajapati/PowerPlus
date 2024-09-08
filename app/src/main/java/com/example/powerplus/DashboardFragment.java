package com.example.powerplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.example.powerplus.FoodItem;

public class DashboardFragment extends Fragment {

    private TextView tvCalorieGoal;
    private TextView tvCaloriesConsumed;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        databaseHelper = new DatabaseHelper(getContext());

        // TODO: Load actual calorie goal from SharedPreferences or database
        tvCalorieGoal.setText("Daily Calorie Goal: 2000");

        updateCaloriesConsumed();

        return view;
    }

    private void updateCaloriesConsumed() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int caloriesConsumed = databaseHelper.getTotalCaloriesForDate(currentDate);
        tvCaloriesConsumed.setText("Calories Consumed Today: " + caloriesConsumed);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCaloriesConsumed();
    }
}
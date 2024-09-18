package com.example.powerplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static android.content.Context.MODE_PRIVATE;
import com.example.powerplus.FoodItem;
import android.util.Log;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private TextView tvCalorieGoal;
    private TextView tvCaloriesConsumed;
    private DatabaseHelper databaseHelper;
    private BroadcastReceiver calorieGoalReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        databaseHelper = new DatabaseHelper(getContext());

        updateCalorieGoal();
        updateCaloriesConsumed();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCalorieGoal();
        updateCaloriesConsumed();
        registerCalorieGoalReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterCalorieGoalReceiver();
    }

    private void registerCalorieGoalReceiver() {
        try {
            if (calorieGoalReceiver == null) {
                calorieGoalReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (MainActivity.ACTION_CALORIE_GOAL_UPDATED.equals(intent.getAction())) {
                            updateCalorieGoal();
                        }
                    }
                };
            }
            IntentFilter filter = new IntentFilter(MainActivity.ACTION_CALORIE_GOAL_UPDATED);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(calorieGoalReceiver, filter);
            Log.d(TAG, "Calorie goal receiver registered");
        } catch (Exception e) {
            Log.e(TAG, "Error registering calorie goal receiver", e);
            Toast.makeText(getContext(), "Error registering receiver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void unregisterCalorieGoalReceiver() {
        try {
            if (calorieGoalReceiver != null) {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(calorieGoalReceiver);
                Log.d(TAG, "Calorie goal receiver unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering calorie goal receiver", e);
        }
    }

    private void updateCalorieGoal() {
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            int calorieGoal = sharedPreferences.getInt("dailyCalorieGoal", 0);
            Log.d(TAG, "Updating calorie goal: " + calorieGoal);
            tvCalorieGoal.setText("Daily Calorie Goal: " + calorieGoal);
        } catch (Exception e) {
            Log.e(TAG, "Error updating calorie goal", e);
            Toast.makeText(getContext(), "Error updating calorie goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCaloriesConsumed() {
        try {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            int caloriesConsumed = databaseHelper.getTotalCaloriesForDate(currentDate);
            Log.d(TAG, "Updating calories consumed: " + caloriesConsumed);
            tvCaloriesConsumed.setText("Calories Consumed Today: " + caloriesConsumed);
        } catch (Exception e) {
            Log.e(TAG, "Error updating calories consumed", e);
            Toast.makeText(getContext(), "Error updating calories consumed: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
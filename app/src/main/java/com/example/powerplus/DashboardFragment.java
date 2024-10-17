//package com.example.powerplus;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.fragment.app.Fragment;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import static android.content.Context.MODE_PRIVATE;
//import com.example.powerplus.FoodItem;
//import android.util.Log;
//
//public class DashboardFragment extends Fragment {
//
//    private static final String TAG = "DashboardFragment";
//    private TextView tvCalorieGoal;
//    private TextView tvCaloriesConsumed;
//    private DatabaseHelper databaseHelper;
//    private BroadcastReceiver calorieGoalReceiver;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
//
//        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
//        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
//        databaseHelper = new DatabaseHelper(getContext());
//
//        updateCalorieGoal();
//        updateCaloriesConsumed();
//
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        updateCalorieGoal();
//        updateCaloriesConsumed();
//        registerCalorieGoalReceiver();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        unregisterCalorieGoalReceiver();
//    }
//
//    private void registerCalorieGoalReceiver() {
//        try {
//            if (calorieGoalReceiver == null) {
//                calorieGoalReceiver = new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        if (MainActivity.ACTION_CALORIE_GOAL_UPDATED.equals(intent.getAction())) {
//                            updateCalorieGoal();
//                        }
//                    }
//                };
//            }
//            IntentFilter filter = new IntentFilter(MainActivity.ACTION_CALORIE_GOAL_UPDATED);
//            LocalBroadcastManager.getInstance(getContext()).registerReceiver(calorieGoalReceiver, filter);
//            Log.d(TAG, "Calorie goal receiver registered");
//        } catch (Exception e) {
//            Log.e(TAG, "Error registering calorie goal receiver", e);
//            Toast.makeText(getContext(), "Error registering receiver: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void unregisterCalorieGoalReceiver() {
//        try {
//            if (calorieGoalReceiver != null) {
//                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(calorieGoalReceiver);
//                Log.d(TAG, "Calorie goal receiver unregistered");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error unregistering calorie goal receiver", e);
//        }
//    }
//
//    private void updateCalorieGoal() {
//        try {
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
//            int calorieGoal = sharedPreferences.getInt("dailyCalorieGoal", 0);
//            Log.d(TAG, "Updating calorie goal: " + calorieGoal);
//            tvCalorieGoal.setText("Daily Calorie Goal: " + calorieGoal);
//        } catch (Exception e) {
//            Log.e(TAG, "Error updating calorie goal", e);
//            Toast.makeText(getContext(), "Error updating calorie goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void updateCaloriesConsumed() {
//        try {
//            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//            int caloriesConsumed = databaseHelper.getTotalCaloriesForDate(currentDate);
//            Log.d(TAG, "Updating calories consumed: " + caloriesConsumed);
//            tvCaloriesConsumed.setText("Calories Consumed Today: " + caloriesConsumed);
//        } catch (Exception e) {
//            Log.e(TAG, "Error updating calories consumed", e);
//            Toast.makeText(getContext(), "Error updating calories consumed: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }
//}

package com.example.powerplus;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private TextView tvCalorieGoal;
    private TextView tvCaloriesConsumed;
    private TextView btnChangeCalorieGoal;  // Button for changing calorie goal
    private DatabaseHelper databaseHelper;
    private BroadcastReceiver calorieGoalReceiver;
    private int calorieGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvCalorieGoal = view.findViewById(R.id.tvCalorieGoal);
        tvCaloriesConsumed = view.findViewById(R.id.tvCaloriesConsumed);
        btnChangeCalorieGoal = view.findViewById(R.id.btnChangeCalorieGoal);  // Initialize the button

        databaseHelper = new DatabaseHelper(getContext());

        // Update calorie data on view creation
        updateCalorieGoal();
        updateCaloriesConsumed();

        // Set up the button click listener
        btnChangeCalorieGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalorieGoalDialog();
            }
        });

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
    }

    private void unregisterCalorieGoalReceiver() {
        if (calorieGoalReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(calorieGoalReceiver);
        }
    }

    private void updateCalorieGoal() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
        calorieGoal = sharedPreferences.getInt("dailyCalorieGoal", 2000);  // Default to 2000 if not set
        tvCalorieGoal.setText("Daily Calorie Goal: " + calorieGoal);
    }

    private void updateCaloriesConsumed() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int caloriesConsumed = databaseHelper.getTotalCaloriesForDate(currentDate);
        tvCaloriesConsumed.setText("Calories Consumed Today: " + caloriesConsumed);

        checkCalorieNotifications(caloriesConsumed);
    }

    private void openCalorieGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Daily Calorie Goal");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newGoal = Integer.parseInt(input.getText().toString());
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("dailyCalorieGoal", newGoal);
                editor.apply();
                updateCalorieGoal();
                Toast.makeText(getContext(), "Calorie goal updated to " + newGoal, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void checkCalorieNotifications(int caloriesConsumed) {
        if (caloriesConsumed >= calorieGoal) {
            Toast.makeText(getContext(), "You have reached your daily calorie goal!", Toast.LENGTH_SHORT).show();
        } else if (caloriesConsumed >= calorieGoal * 0.9) {
            Toast.makeText(getContext(), "You are close to reaching your calorie goal!", Toast.LENGTH_SHORT).show();
        } else if (caloriesConsumed >= calorieGoal / 2) {
            Toast.makeText(getContext(), "You have consumed half of your daily calories!", Toast.LENGTH_SHORT).show();
        }
    }
}

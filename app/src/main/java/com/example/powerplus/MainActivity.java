package com.example.powerplus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public static final String ACTION_CALORIE_GOAL_UPDATED = "com.example.powerplus.CALORIE_GOAL_UPDATED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            viewPager = findViewById(R.id.viewPager);
            tabLayout = findViewById(R.id.tabLayout);

            if (viewPager == null || tabLayout == null) {
                throw new IllegalStateException("ViewPager or TabLayout not found in layout");
            }

            ViewPagerAdapter adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Dashboard");
                                break;
                            case 1:
                                tab.setText("Graphs");
                                break;
                            case 2:
                                tab.setText("Food Input");
                                break;
                        }
                    }).attach();

            checkAndShowCalorieGoalDialog();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error in MainActivity: " + e.getMessage() + "\n" + "Stack trace: "
                    + Log.getStackTraceString(e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, errorMessage);
        }
    }

    private void checkAndShowDailyCalorieDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String lastLoginDate = sharedPreferences.getString("lastLoginDate", "");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!currentDate.equals(lastLoginDate)) {
            showDailyCalorieDialog();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastLoginDate", currentDate);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Perform logout
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDailyCalorieDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Daily Calorie Goal");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String calorieGoal = input.getText().toString();
                        if (!calorieGoal.isEmpty()) {
                            int goal = Integer.parseInt(calorieGoal);
                            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("dailyCalorieGoal", goal);
                            editor.apply();
                            Log.d(TAG, "Daily calorie goal set to: " + goal);
                            Toast.makeText(MainActivity.this, "Daily calorie goal set to " + goal, Toast.LENGTH_SHORT)
                                    .show();

                            // Broadcast that the goal has been updated using LocalBroadcastManager
                            Intent intent = new Intent(ACTION_CALORIE_GOAL_UPDATED);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                        } else {
                            Log.w(TAG, "Empty input for calorie goal");
                            Toast.makeText(MainActivity.this, "Please enter a calorie goal.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid input for calorie goal", e);
                        Toast.makeText(MainActivity.this, "Invalid input. Please enter a number.", Toast.LENGTH_SHORT)
                                .show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting calorie goal", e);
                        Toast.makeText(MainActivity.this, "Error setting calorie goal: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing daily calorie dialog", e);
            Toast.makeText(this, "Error showing calorie goal dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new DashboardFragment();
                case 1:
                    return new GraphFragment();
                case 2:
                    return new FoodInputFragment();
                default:
                    return new DashboardFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private void checkAndShowCalorieGoalDialog() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            int calorieGoal = sharedPreferences.getInt("dailyCalorieGoal", 0);
            Log.d(TAG, "Current calorie goal: " + calorieGoal);
            if (calorieGoal == 0) {
                showDailyCalorieDialog();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking calorie goal", e);
            Toast.makeText(this, "Error checking calorie goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
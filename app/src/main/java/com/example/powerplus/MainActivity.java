package com.example.powerplus;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;
import android.widget.EditText;
import android.content.DialogInterface;
import android.text.InputType;

import androidx.annotation.NonNull;
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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;

    public static final String ACTION_CALORIE_GOAL_UPDATED = "com.example.powerplus.CALORIE_GOAL_UPDATED";

    private SwitchMaterial themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        checkAndShowCalorieGoalDialog();

        themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Set the switch state based on the current theme
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        themeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                selectedFragment = new DashboardFragment();
                break;
            case R.id.nav_graph:
                selectedFragment = new GraphFragment();
                break;
            case R.id.nav_food_input:
                selectedFragment = new FoodInputFragment();
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
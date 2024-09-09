package com.example.powerplus;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

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
                    }
            ).attach();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error in MainActivity: " + e.getMessage() + "\n" + "Stack trace: " + Log.getStackTraceString(e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e("MainActivity", errorMessage);
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
//package com.example.powerplus;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.fragment.app.Fragment;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class GraphFragment extends Fragment {
//
//    private LineChart chart;
//    private DatabaseHelper databaseHelper;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_graph, container, false);
//
//        chart = view.findViewById(R.id.chart);
//        databaseHelper = new DatabaseHelper(getContext());
//
//        setupChart();
//        loadChartData();
//
//        return view;
//    }
//
//    private void setupChart() {
//        chart.getDescription().setEnabled(false);
//        chart.setDrawGridBackground(false);
//        chart.setPinchZoom(false);
//        chart.setScaleEnabled(false);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f);
//        xAxis.setLabelCount(3);
//
//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getAxisRight().setEnabled(false);
//        chart.getLegend().setEnabled(false);
//    }
//
//    private void loadChartData() {
//        List<CalorieEntry> entries = databaseHelper.getCaloriesForLastThreeDays();
//        Collections.reverse(entries);
//
//        List<Entry> lineEntries = new ArrayList<>();
//        List<String> labels = new ArrayList<>();
//
//        for (int i = 0; i < entries.size(); i++) {
//            CalorieEntry entry = entries.get(i);
//            lineEntries.add(new Entry(i, entry.getCalories()));
//            labels.add(entry.getDate());
//        }
//
//        LineDataSet dataSet = new LineDataSet(lineEntries, "Calorie Intake");
//        dataSet.setColor(Color.BLUE);
//        dataSet.setCircleColor(Color.BLUE);
//        dataSet.setDrawValues(true);
//
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//
//        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
//        chart.invalidate();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        loadChartData();
//    }
//}

package com.example.powerplus;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphFragment extends Fragment {

    private LineChart chart;
    private DatabaseHelper databaseHelper;
    private Spinner timeRangeSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        chart = view.findViewById(R.id.chart);
        timeRangeSpinner = view.findViewById(R.id.timeRangeSpinner);
        databaseHelper = new DatabaseHelper(getContext());

        setupChart();
        setupSpinner();
        loadChartData("3-day");

        return view;
    }

    private void setupChart() {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(3);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
    }

    private void setupSpinner() {
        // Create an ArrayAdapter using a simple spinner layout and string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_ranges, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeRangeSpinner.setAdapter(adapter);

        // Set listener for the spinner
        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String timeRange = parentView.getItemAtPosition(position).toString();
                loadChartData(timeRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed here
            }
        });
    }

    private void loadChartData(String timeRange) {
        List<CalorieEntry> entries;
        switch (timeRange) {
            case "Weekly":
                entries = databaseHelper.getCaloriesForLastWeek();
                break;
            case "Monthly":
                entries = databaseHelper.getCaloriesForLastMonth();
                break;
            default:
                entries = databaseHelper.getCaloriesForLastThreeDays();
                break;
        }
        Collections.reverse(entries);

        List<Entry> lineEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            CalorieEntry entry = entries.get(i);
            lineEntries.add(new Entry(i, entry.getCalories()));
            labels.add(entry.getDate());
        }

        LineDataSet dataSet = new LineDataSet(lineEntries, "Calorie Intake");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChartData(timeRangeSpinner.getSelectedItem().toString());
    }
}

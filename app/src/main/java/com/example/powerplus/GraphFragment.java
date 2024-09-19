package com.example.powerplus;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
        // Customize the chart
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);

        // Customize X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.graphTextColor));
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(ContextCompat.getColor(requireContext(), R.color.graphGridColor));

        // Customize Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.graphTextColor));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(requireContext(), R.color.graphGridColor));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Customize Legend
        Legend legend = chart.getLegend();
        legend.setTextColor(ContextCompat.getColor(requireContext(), R.color.graphTextColor));

        // Set data
        setData();
    }

    private void setData() {
        ArrayList<Entry> values = new ArrayList<>();
        // Add your data points here
        // For example:
        // values.add(new Entry(0, 1000));
        // values.add(new Entry(1, 1500));
        // ...

        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Calories Consumed");
            set1.setDrawIcons(false);
            set1.setColor(ContextCompat.getColor(requireContext(), R.color.graphLineColor));
            set1.setCircleColor(ContextCompat.getColor(requireContext(), R.color.graphLineColor));
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormSize(15.f);
            set1.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.graphTextColor));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
        chart.invalidate();
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

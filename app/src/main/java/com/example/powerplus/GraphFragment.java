package com.example.powerplus;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment {

    private LineChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        chart = view.findViewById(R.id.chart);
        setupChart();
        return view;
    }

    private void setupChart() {
        if (chart == null) {
            return;  // Exit if chart is not initialized
        }

        List<Entry> entries = new ArrayList<>();
        // TODO: Replace with actual data
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, (float) (Math.random() * 2000)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Calorie Intake");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
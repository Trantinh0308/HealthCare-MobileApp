package com.example.healthcare.models;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class MyXAxisValueFormatter extends ValueFormatter {
    private List<String> xAxisLabels;

    public MyXAxisValueFormatter(List<String> xAxisLabels) {
        this.xAxisLabels = xAxisLabels;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        return xAxisLabels.get(index);
    }
}

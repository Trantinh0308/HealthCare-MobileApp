package com.example.healthcare.models;

import com.example.healthcare.adapters.QuestionAdapter;

import java.util.List;

public interface OnFragmentInteractionListener {
    void checkDataFromFragment(List<String> data);
    void getIndexSelectedItem(QuestionAdapter questionAdapter);
    void getFinalSymptom(String symptoms);
    void listenResponseFromServer(int statusCode);
}

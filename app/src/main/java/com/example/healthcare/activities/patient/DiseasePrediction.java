package com.example.healthcare.activities.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.PredictAdapter;
import com.example.healthcare.adapters.QuestionAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.testCallingVideo.Test;
import com.example.healthcare.utils.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiseasePrediction extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout btnChoose, blockBottom, notice;
    TextView textQuestion, textResult,choose, textSymptoms;
    RecyclerView recyclerViewQuestions;
    QuestionAdapter questionAdapter;
    PredictAdapter predictAdapter;
    ProgressBar progressBar,progressQuestions;
    String indexSelected = "";
    int order = 0;
    int sizeData;
    int totalProgress;
    int currentProgress = 0;
    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_prediction);
        initView();
        setOnclick();
        getData();
    }

    private void getData() {
        List<String> symptoms = new ArrayList<>();
        ApiClient.getSymptoms(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray foundSymptomsArray = jsonResponse.optJSONArray("found_symptoms");
                    if (foundSymptomsArray != null) {
                        for (int i = 0; i < foundSymptomsArray.length(); i++) {
                            String symptom = foundSymptomsArray.optString(i, "Unknown");
                            symptoms.add(symptom);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                setupUI(symptoms);
                            }
                        });
                    } else {
                        Log.d("API", "Không tìm thấy triệu chứng.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("API", "Lỗi khi xử lý dữ liệu JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void setupUI(List<String> symptoms) {
        sizeData = symptoms.size();
        if (symptoms.size() == 0){
            notice.setVisibility(View.VISIBLE);
            choose.setText("Trang chủ");
            return;
        }
        choose.setText("Xác nhận");
        recyclerViewQuestions.setVisibility(View.VISIBLE);
        questionAdapter = new QuestionAdapter(symptoms, new QuestionAdapter.IViewHolder() {
            @Override
            public void OnclickItem(int position) {

            }
        });

        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerViewQuestions.setAdapter(questionAdapter);
    }

    private void setOnclick() {
        btnChoose.setOnClickListener(this);
    }

    private void initView() {
        textQuestion = findViewById(R.id.text_question);
        btnChoose = findViewById(R.id.btn_enter);
        blockBottom = findViewById(R.id.bottom_layout);
        choose = findViewById(R.id.choose);
        textResult = findViewById(R.id.title_result);
        btnBack = findViewById(R.id.back_btn);
        textSymptoms = findViewById(R.id.list_symptom);
        recyclerViewQuestions = findViewById(R.id.list_question);
        progressBar = findViewById(R.id.progress_bar);
        notice = findViewById(R.id.block_notice);
        progressQuestions = findViewById(R.id.progress_questions);
    }
    public void resetView(){
        notice.setVisibility(View.GONE);
        recyclerViewQuestions.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_enter){
            if (order == 0){
                if (choose.getText().toString().contains("Trang chủ")){
                    Intent intent = new Intent(this, Main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    indexSelected = questionAdapter.getCheckedItems();
                    if (indexSelected.isEmpty()){
                        CustomToast.showToast(this,"Chọn triệu chứng",1000);
                        return;
                    }
                    resetView();
                    sendSymptomToServer(indexSelected);
                    progressQuestions.setVisibility(View.VISIBLE);
                }
            }
            else {
                currentProgress ++ ;
                setupProgressBarUI();
                if (choose.getText().toString().contains("Trang chủ")){
                    Intent intent = new Intent(this, Main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    if (choose.getText().toString().contains("Bỏ qua")){
                        resetView();
                        getSymptomByPage(order + 1);
                    }
                    else {
                        indexSelected = questionAdapter.getCheckedItems();
                        if (indexSelected.isEmpty()){
                            CustomToast.showToast(this,"Chọn triệu chứng",1000);
                            return;
                        }
                        resetView();
                        sendSymptomByPage(indexSelected);
                    }
                }
            }
        }
    }

    private void setupProgressBarUI() {
        if (currentProgress <= totalProgress) {
            int progress = (currentProgress * 100) / totalProgress;
            Drawable draw = ContextCompat.getDrawable(DiseasePrediction.this, R.drawable.progress_bar);
            progressQuestions.setProgressDrawable(draw);
            progressQuestions.setProgress(progress);
        }
    }

    private void sendSymptomByPage(String indexSelected) {
        ApiClient.postSelectedSymptomsByPage(order, indexSelected, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSymptomByPage(order + 1);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                CustomToast.showToast(DiseasePrediction.this,"ERROR",1000);
            }
        });
    }

    private void sendSymptomToServer(String indexSelected) {
        ApiClient.postSelectedSymptoms(indexSelected, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDictSymptomSize();
                        getSymptomByPage(order + 1);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                CustomToast.showToast(DiseasePrediction.this,"ERROR",1000);
            }
        });
    }

    private void getDictSymptomSize() {
        ApiClient.getDictSize(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int size = jsonResponse.getInt("symptom_count_size");
                    totalProgress = (size % 5 == 0) ? (size / 5) : (size / 5 + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("API", "Lỗi khi gọi API: " + error);
            }
        });
    }


    private void getSymptomByPage(int pageNumber) {
        List<String> symptoms = new ArrayList<>();
        ApiClient.getSymptomsByPage(pageNumber, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                order ++;
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray foundSymptomsArray = jsonResponse.optJSONArray("symptoms");
                    if (foundSymptomsArray != null) {
                        for (int i = 0; i < foundSymptomsArray.length(); i++) {
                            String symptom = foundSymptomsArray.optString(i, "Unknown");
                            symptoms.add(symptom);
                        }
                        runOnUiThread(() -> {
                            setupSymptomByPageUI(symptoms);
                        });
                    } else {
                        Log.d("API", "Không tìm thấy triệu chứng.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("API", "Lỗi khi xử lý dữ liệu JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("API", "Lỗi khi gọi API: " + error);
            }
        });
    }

    private void setupSymptomByPageUI(List<String> symptoms) {
        if (symptoms.size() == 0) {
            progressQuestions.setVisibility(View.GONE);
            textQuestion.setVisibility(View.GONE);
            textResult.setVisibility(View.VISIBLE);
            getPredictResult();
        } else {
            setupSymptomUI(symptoms);
        }
    }

    private void setupSymptomUI(List<String> symptoms) {
        progressBar.setVisibility(View.GONE);
        choose.setText("Bỏ qua");
        recyclerViewQuestions.setVisibility(View.VISIBLE);
        questionAdapter = new QuestionAdapter(symptoms, new QuestionAdapter.IViewHolder() {
            @Override
            public void OnclickItem(int position) {
                indexSelected = questionAdapter.getCheckedItems();
                if (indexSelected.isEmpty()){
                    choose.setText("Bỏ qua");
                }
                else choose.setText("Xác nhận");
            }
        });
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewQuestions.setAdapter(questionAdapter);
    }

    private void getPredictResult() {
        Map<String,Double> results = new LinkedHashMap<>();
        StringBuilder symptoms = new StringBuilder();
        ApiClient.getDiseasePrediction(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray resultArray = jsonResponse.optJSONArray("result_predict");
                    JSONArray symptomArray = jsonResponse.optJSONArray("final_symptoms");
                    if (resultArray != null) {
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONArray predictArray = resultArray.optJSONArray(i);
                            if (predictArray != null && predictArray.length() > 0) {
                                String predict = predictArray.optString(2, "Unknown");
                                double percent = predictArray.optDouble(1,0.55);
                                if (!results.containsKey(predict))results.put(predict,percent);
                            }
                        }

                        for (int i = 0; i < symptomArray.length(); i++) {
                            String symptom = symptomArray.optString(i, "Unknown");
                            if (i > 0) {
                                symptoms.append(", ");
                            }
                            symptoms.append(symptom);
                        }

                        runOnUiThread(() -> {
                            setupResultUI(results,symptoms.toString());
                        });
                    } else {
                        Log.d("API", "Không tìm thấy triệu chứng.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("API", "Lỗi khi xử lý dữ liệu JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void setupResultUI(Map<String, Double> results, String string) {
        progressBar.setVisibility(View.GONE);
        recyclerViewQuestions.setVisibility(View.VISIBLE);
        choose.setText("Trang chủ");
        textSymptoms.setVisibility(View.VISIBLE);
        textSymptoms.setText(string);
        List<String> predicts = new ArrayList<>();
        for (Map.Entry<String, Double> entry : results.entrySet()) {
            predicts.add(entry.getKey() + "    " + String.format("%.2f", entry.getValue() * 100) + "%");
        }
        predictAdapter = new PredictAdapter(predicts, position -> {
            String specialist = predicts.get(position);
            Intent intent = new Intent(this, AppointmentCalendar.class);
            intent.putExtra("specialist", specialist);
            startActivity(intent);
        });
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewQuestions.setAdapter(predictAdapter);
    }
}
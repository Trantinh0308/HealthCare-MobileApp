package com.example.healthcare.activities.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.SpecialistAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Specialist extends AppCompatActivity implements View.OnClickListener {
    ImageView iconOne,iconTwo,iconThree,iconFour,iconFive;
    ImageButton btnBack;
    ImageView btnHome;
    View lineOne,lineTwo,lineThree,lineFour;
    RelativeLayout btnSkip, blockIconOne,
            blockIconTwo, blockIconThree,blockIconFour,blockIconFive;
    SpecialistAdapter specialistAdapter;
    RecyclerView recyclerView;
    int color,colorDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist);
        color = ContextCompat.getColor(this, R.color.white);
        colorDefault = ContextCompat.getColor(this, R.color.icon_notselect);
        initView();
        setOnclick();
        setUpViewIcon();
        setupData();
    }
    private void setUpViewIcon() {
        iconOne.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconOne.setBackgroundResource(R.drawable.bg_icon_selected);
        lineOne.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconTwo.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconTwo.setBackgroundResource(R.drawable.bg_icon_notselect);
        lineTwo.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconThree.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconThree.setBackgroundResource(R.drawable.bg_icon_notselect);
        lineThree.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconFour.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconFour.setBackgroundResource(R.drawable.bg_icon_notselect);
        lineFour.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconFive.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconFive.setBackgroundResource(R.drawable.bg_icon_notselect);
    }
    private void setupData() {
        List<String> specialist = new ArrayList<>();
        specialist.add("Tư vấn chuyên khoa");specialist.add("Hô Hấp");specialist.add("Tim Mạch");
        specialist.add("Da Liễu");specialist.add("Tai Mũi Họng");specialist.add("Tiêu Hóa");
        specialist.add("Ung Bướu");specialist.add("Nội Khoa");specialist.add("Thần Kinh");specialist.add("Cơ Xương Khớp");

        specialistAdapter = new SpecialistAdapter(specialist, new SpecialistAdapter.ISpecialistViewHolder() {
            @Override
            public void onClickItem(int position) {
                String specialistName = specialist.get(position);
                if (position == 0){
                    showDialogSymptom();
                }
                else {
                    Intent intent = new Intent(Specialist.this, AppointmentCalendar.class);
                    intent.putExtra("specialist",specialistName);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(specialistAdapter);
    }

    private void showDialogSymptom() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_symptom);
        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        if (windowAttributes.gravity == Gravity.BOTTOM) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        } else {
            dialog.setCancelable(false);
        }
        RelativeLayout btnSend;
        EditText textSymptom;
        TextView send;
        ProgressBar progressBar;

        send = dialog.findViewById(R.id.textSend);
        progressBar = dialog.findViewById(R.id.loadSend);
        btnSend = dialog.findViewById(R.id.btnSend);
        textSymptom = dialog.findViewById(R.id.symptom);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symptom = textSymptom.getText().toString();
                if (symptom.length() == 0){
                    CustomToast.showToast(getApplicationContext(),"Nhập triệu chứng",1000);
                    return;
                }
                send.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendSymptom(symptom,dialog,progressBar);
                    }
                }, 1000);
            }
        });

        dialog.show();
    }

    private void sendSymptom(String symptom, Dialog dialog, ProgressBar progressBar) {
        ApiClient.postSymptoms(symptom, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                        Intent intent = new Intent(Specialist.this, DiseasePrediction.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                        CustomToast.showToast(getApplicationContext(),"Lỗi kết nối tới dịch vụ",1000);
                    }
                });
                Timber.tag("TAG").e(error);
            }
        });
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        btnHome = findViewById(R.id.home);
        btnSkip = findViewById(R.id.btn_skip);
        recyclerView = findViewById(R.id.specialist);
        iconOne = findViewById(R.id.icon_one);
        iconTwo = findViewById(R.id.icon_two);
        iconThree = findViewById(R.id.icon_three);
        iconFour = findViewById(R.id.icon_four);
        iconFive= findViewById(R.id.icon_five);
        blockIconOne = findViewById(R.id.block_icon_one);
        blockIconTwo = findViewById(R.id.block_icon_two);
        blockIconThree= findViewById(R.id.block_icon_three);
        blockIconFour = findViewById(R.id.block_icon_four);
        blockIconFive = findViewById(R.id.block_icon_five);
        lineOne = findViewById(R.id.line_one);
        lineTwo = findViewById(R.id.line_two);
        lineThree = findViewById(R.id.line_three);
        lineFour = findViewById(R.id.line_four);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn)finish();
        else if (v.getId() == R.id.home)finish();
        else if (v.getId() == R.id.btn_skip){
            Intent intent = new Intent(Specialist.this, DoctorList.class);
            intent.putExtra("specialist","Tổng Hợp");
            startActivity(intent);
        }
    }
}
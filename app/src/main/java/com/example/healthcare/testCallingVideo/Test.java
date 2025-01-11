package com.example.healthcare.testCallingVideo;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.healthcare.R;

public class Test extends AppCompatActivity{
    private ProgressBar progressBar;
    Button btnNext;
    private int totalSteps = 7;  // Tổng số bước
    private int currentStep = 0;   // Số bước hiện tại
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        progressBar = findViewById(R.id.progressBar);
        btnNext = findViewById(R.id.next);
        // Bắt đầu quy trình
        btnNext.setOnClickListener(v -> {
            currentStep++;
            if (currentStep <= totalSteps) {
                // Tính toán phần trăm tiến độ
                int progress = (currentStep * 100) / totalSteps;

                // Cập nhật ProgressBar
                Drawable draw = ContextCompat.getDrawable(Test.this, R.drawable.progress_bar);
// set the drawable as progress drawable
                progressBar.setProgressDrawable(draw);

                // Cập nhật tiến độ
                progressBar.setProgress(progress);

            }
        });
    }
}
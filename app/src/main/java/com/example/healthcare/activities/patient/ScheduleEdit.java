package com.example.healthcare.activities.patient;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import timber.log.Timber;

public class ScheduleEdit extends AppCompatActivity implements View.OnClickListener {
    TextView textViewDrugName;
    TextView textStartDate, textEndDate,textViewFrequency,textDateSelected, textTime, textNote;
    ImageView drugImage;
    ProgressBar loadingSaveData;
    RelativeLayout btnAddTime,blockStartDate,btnSchedule,
            blockEndDate,blockFrequency,btnEnter;
    ImageView btnBack;
    Schedule schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);
        schedule = AndroidUtil.getScheduleFromIntent(getIntent());
        initView();
        setOnClick();
        setDataView();
    }

    private void setOnClick() {
        btnBack.setOnClickListener(this);
        btnSchedule.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
    }

    private void setDataView() {
        textViewDrugName.setText(schedule.getDrugName());
        setImage();
        textTime.setText(schedule.getTime());
        textNote.setText(schedule.getNote());
        textStartDate.setText(schedule.getStartDate());
        if (schedule.getEndDate() != null && !schedule.getEndDate().isEmpty()){
            textEndDate.setText(schedule.getEndDate());
        }
        textViewFrequency.setText(schedule.getFrequency());
    }

    private void setImage() {
        if (schedule.getImage() != null && !schedule.getImage().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(schedule.getImage(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                drugImage.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                drugImage.setImageResource(R.drawable.drugs);
            }
        } else {
            drugImage.setImageResource(R.drawable.drugs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnEnter.setVisibility(View.VISIBLE);
        loadingSaveData.setVisibility(View.GONE);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        textViewDrugName = findViewById(R.id.name_drug);
        drugImage = findViewById(R.id.drug_image);
        btnAddTime = findViewById(R.id.top_block_time);
        blockStartDate = findViewById(R.id.form_startDate);
        blockEndDate = findViewById(R.id.form_endDate);
        blockFrequency = findViewById(R.id.block_frequency);
        btnEnter = findViewById(R.id.btn_enter);
        textStartDate = findViewById(R.id.text_start_date);
        textEndDate = findViewById(R.id.text_end_date);
        textViewFrequency = findViewById(R.id.frequency);
        textDateSelected = findViewById(R.id.date_selected);
        btnSchedule = findViewById(R.id.schedule);
        loadingSaveData = findViewById(R.id.progress_bar);
        textTime = findViewById(R.id.time);
        textNote = findViewById(R.id.note);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            finish();
        }
        if (v.getId() == R.id.btn_enter){
            btnEnter.setVisibility(View.GONE);
            loadingSaveData.setVisibility(View.VISIBLE);
            saveEditedSchedule();
        }
        if (v.getId() == R.id.schedule){
            showDialogTime();
        }
    }

    private void saveEditedSchedule() {
        if (schedule == null) return;
        FirebaseUtil.scheduleCollectionById(schedule.getSid()).set(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag(TAG).d("onComplete: successful");
                Intent intent = new Intent(ScheduleEdit.this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDialogTime() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_detail);
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
        ImageView btnRemove;
        RelativeLayout btnEnter;
        NumberPicker numberPickerHour,numberPickerMinute;
        EditText editTextNote;
        btnRemove = dialog.findViewById(R.id.btn_remove);
        btnEnter = dialog.findViewById(R.id.btn_enter);
        numberPickerHour = dialog.findViewById(R.id.hours);
        numberPickerMinute = dialog.findViewById(R.id.minute);
        editTextNote = dialog.findViewById(R.id.note);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);

        numberPickerHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        numberPickerMinute.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        setDialogView(numberPickerHour,numberPickerMinute,editTextNote);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetail(numberPickerHour,numberPickerMinute,editTextNote);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getDetail(NumberPicker numberPickerHour, NumberPicker numberPickerMinute, EditText editTextNote) {
        @SuppressLint("DefaultLocale") String hours = String.format("%02d",numberPickerHour.getValue());
        @SuppressLint("DefaultLocale") String minute = String.format("%02d",numberPickerMinute.getValue());
        String editTime = hours + ":" + minute;
        String editNote = editTextNote.getText().toString();
        textTime.setText(editTime);
        textNote.setText(editNote);
        if (schedule != null){
            schedule.setTime(editTime);
            schedule.setNote(editNote);
        }
    }

    private void setDialogView(NumberPicker numberPickerHour, NumberPicker numberPickerMinute, EditText editTextNote) {
        if (schedule != null){
            String[] arr = schedule.getTime().split(":");
            numberPickerHour.setValue(Integer.parseInt(arr[0]));
            numberPickerMinute.setValue(Integer.parseInt(arr[1]));
            editTextNote.setText(schedule.getNote());
        }
    }

    private void validScheduleDetail() {
    }
}
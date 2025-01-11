package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DateAdapter;
import com.example.healthcare.adapters.DateOnlineAdapter;
import com.example.healthcare.adapters.DoctorAdapter2;
import com.example.healthcare.adapters.TimeOnlineAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppointmentCalendar extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    ImageView btnHome, iconMorning, iconAfternoon,iconOne,iconTwo,iconThree,iconFour,iconFive;
    TextView textViewMonth, textViewMorning, textViewTimeMorning, textViewAfternoon, textViewTimeAfternoon;
    RelativeLayout btnNotice, btnChooseMonth, btnTimeMorning, btnTimeAfternoon, btnEnter,
            blockIconOne, blockIconTwo, blockIconThree,blockIconFour,blockIconFive;
    View lineOne,lineTwo,lineThree,lineFour;
    RecyclerView recyclerViewDoctors, recyclerViewDate, recyclerViewTime;
    ProgressBar progressBarLoading, progressBarTime;
    DoctorAdapter2 doctorAdapter;
    DateOnlineAdapter dateOnlineAdapter;
    TimeOnlineAdapter timeOnlineAdapter;
    List<String> timeList;
    List<Boolean> indexList;
    OnlineDoctor doctorChoose;
    User currentUser;
    String dateChoose, timeChoose = "";
    boolean isMorning = true;
    int color,colorDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_calendar);
        color = ContextCompat.getColor(this, R.color.white);
        colorDefault = ContextCompat.getColor(this, R.color.icon_notselect);
        dateChoose = AndroidUtil.getCurrentDate();
        getCurrentUser();
        initView();
        setUpViewIcon();
        setOnclick();
        if (getIntent() != null ){
            String specialist = getIntent().getStringExtra("specialist");
            String doctorId = getIntent().getStringExtra("doctorId");
            if (specialist != null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDoctorsBySpecialty(AndroidUtil.normalizeString(specialist));
                    }
                }, 1000);
            }
            else if (doctorId != null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDoctorsById(doctorId);
                    }
                }, 1000);
            }
        }
    }

    private void getDoctorsById(String doctorId) {
        progressBarLoading.setVisibility(View.GONE);
        Query query = FirebaseUtil.allOnlineDoctorCollection()
                .whereEqualTo("doctorId", doctorId);
        FirestoreRecyclerOptions<OnlineDoctor> options = new FirestoreRecyclerOptions.Builder<OnlineDoctor>()
                .setQuery(query, OnlineDoctor.class).build();
        doctorAdapter = new DoctorAdapter2(options, getApplicationContext(), new DoctorAdapter2.IDoctorViewHolder() {
            @Override
            public void onClickView(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(AppointmentCalendar.this, DoctorDetail.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onClickBook(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                doctorChoose = doctor;
                showDialogCalendar(doctor);
            }

            @Override
            public void onDataLoaded(int size) {
                if (size == 0){
                    btnNotice.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerViewDoctors.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctors.setAdapter(doctorAdapter);
        doctorAdapter.startListening();
    }

    private void setUpViewIcon() {
        iconOne.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconOne.setBackgroundResource(R.drawable.bg_icon_selected);
        lineOne.setBackgroundTintList(ColorStateList.valueOf(color));

        iconTwo.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconTwo.setBackgroundResource(R.drawable.bg_icon_selected);
        lineTwo.setBackgroundTintList(ColorStateList.valueOf(color));

        iconThree.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconThree.setBackgroundResource(R.drawable.bg_icon_selected);
        lineThree.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconFour.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconFour.setBackgroundResource(R.drawable.bg_icon_notselect);
        lineFour.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconFive.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconFive.setBackgroundResource(R.drawable.bg_icon_notselect);
    }

    private void getCurrentUser() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentUser = task.getResult().toObject(User.class);
            }
        });
    }

    private void getDoctorsBySpecialty(String specialist) {
        progressBarLoading.setVisibility(View.GONE);
        Query query = FirebaseUtil.allOnlineDoctorCollection()
                .whereGreaterThanOrEqualTo("specialist", specialist)
                .whereLessThan("specialist", specialist + "\uf8ff");
        FirestoreRecyclerOptions<OnlineDoctor> options = new FirestoreRecyclerOptions.Builder<OnlineDoctor>()
                .setQuery(query, OnlineDoctor.class).build();
        doctorAdapter = new DoctorAdapter2(options, getApplicationContext(), new DoctorAdapter2.IDoctorViewHolder() {
            @Override
            public void onClickView(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(AppointmentCalendar.this, DoctorDetail.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onClickBook(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                doctorChoose = doctor;
                showDialogCalendar(doctor);
            }

            @Override
            public void onDataLoaded(int size) {
                if (size == 0){
                    btnNotice.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerViewDoctors.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctors.setAdapter(doctorAdapter);
        doctorAdapter.startListening();
    }

    private void showDialogCalendar(OnlineDoctor doctor) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_calendar);
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
        recyclerViewDate = dialog.findViewById(R.id.list_date);
        btnChooseMonth = dialog.findViewById(R.id.btnMonth);
        btnTimeMorning = dialog.findViewById(R.id.block_time_morning);
        btnTimeAfternoon = dialog.findViewById(R.id.block_time_afternoon);
        recyclerViewTime = dialog.findViewById(R.id.list_time);
        textViewMonth = dialog.findViewById(R.id.text_month);
        iconMorning = dialog.findViewById(R.id.icon_sun_morning);
        iconAfternoon = dialog.findViewById(R.id.icon_sun_afternoon);
        textViewMorning = dialog.findViewById(R.id.text_top);
        textViewAfternoon = dialog.findViewById(R.id.text_top_afternoon);
        textViewTimeMorning = dialog.findViewById(R.id.text_below);
        textViewTimeAfternoon = dialog.findViewById(R.id.text_below_afternoon);
        progressBarTime = dialog.findViewById(R.id.loadingTime);
        btnEnter = dialog.findViewById(R.id.btnEnter);

        btnChooseMonth.setOnClickListener(this);
        btnTimeMorning.setOnClickListener(this);
        btnTimeAfternoon.setOnClickListener(this);

        LocalDate today = LocalDate.now();
        setupTextViewMonth(today.getMonthValue(), today.getYear());
        setupRecyclerViewDate(today.getMonthValue(), today.getYear());
        setupRecyclerViewTime(doctor,AndroidUtil.getCurrentDate(),true);

        btnEnter.setOnClickListener(v -> {
            if (timeChoose.isEmpty()){
                CustomToast.showToast(getApplicationContext(),"Chọn giờ khám",1000);
                return;
            }
            getDataFromView();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupTextViewMonth(int monthValue, int year) {
        String formatMonth = (monthValue < 10) ? "0" + monthValue : String.valueOf(monthValue);
        textViewMonth.setText("Tháng "+formatMonth+", "+year);
    }

    private void getDataFromView() {
        OnlineAppointment appointment = new OnlineAppointment();
        appointment.setAppointmentDate(dateChoose);
        appointment.setBookDate(AndroidUtil.getCurrentDate());
        appointment.setTime(timeChoose);
        Intent intent = new Intent(this,Person_Online.class);
        if (doctorChoose != null)AndroidUtil.passOnlineDoctorAsIntent(intent,doctorChoose);
        AndroidUtil.passOnlineAppointmentAsIntent(intent,appointment);
        if (currentUser != null)intent.putExtra("userName",getNameFromFullName(currentUser.getFullName()));
        startActivity(intent);
    }

    private String getNameFromFullName(String fullName) {
        String[]str = fullName.split(" ");
        return str[str.length-1];
    }

    private void initTimeList(Boolean morning) {
        timeList = new ArrayList<>();
        if (morning){
            timeList.add("08:00");timeList.add("08:15");timeList.add("08:30");
            timeList.add("08:45");timeList.add("09:00");timeList.add("09:15");
            timeList.add("09:30");timeList.add("09:45");timeList.add("10:00");
            timeList.add("10:15");timeList.add("10:30");timeList.add("10:45");
            timeList.add("11:00");timeList.add("11:15");timeList.add("11:30");
        }
        else {
            timeList.add("13:00");timeList.add("13:15");timeList.add("13:30");
            timeList.add("13:45");timeList.add("14:00");timeList.add("14:15");
            timeList.add("14:30");timeList.add("14:45");timeList.add("15:00");
            timeList.add("15:15");timeList.add("15:30");timeList.add("15:45");
            timeList.add("16:00");timeList.add("16:15");timeList.add("16:30");
        }
        indexList = new ArrayList<>(timeList.size());
        for (int i = 0; i < timeList.size(); i++) {
            indexList.add(true);
        }
    }
    private void setupRecyclerViewTime(OnlineDoctor doctor, String dateChoose, boolean btnMorning) {
        initTimeList(btnMorning);
        FirebaseUtil.allOnlineAppointment()
                .whereEqualTo("doctor.doctorId", doctor.getDoctorId())
                .whereEqualTo("appointmentDate", dateChoose)
                .whereIn("stateAppointment", Arrays.asList(0, 1))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OnlineAppointment appointment = document.toObject(OnlineAppointment.class);
                            String time = appointment.getTime();
                            if (timeList.contains(time)){
                                int index = timeList.indexOf(time);
                                indexList.set(index,false);
                            }
                        }

                        if (dateChoose.equals(AndroidUtil.getCurrentDate())){
                            for (int i = 0 ; i < timeList.size() ; i++){
                                if (timeList.get(i).compareTo(AndroidUtil.currentTime()) < 0){
                                    indexList.set(i,false);
                                }
                            }
                        }
                        updateUI(timeList,indexList);
                    }
                });
    }

    private void updateUI(List<String> timeList, List<Boolean> indexList) {
        progressBarTime.setVisibility(View.GONE);
        recyclerViewTime.setVisibility(View.VISIBLE);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 5);
        timeOnlineAdapter = new TimeOnlineAdapter(timeList, indexList, new TimeOnlineAdapter.ITimeViewHolder() {
            @Override
            public void onClickItem(int position) {
                timeChoose = timeList.get(position);
            }
        });
        recyclerViewTime.setLayoutManager(layoutManager);
        recyclerViewTime.setAdapter(timeOnlineAdapter);
    }

    private void setupRecyclerViewDate(int monthValue, int year) {
        List<String> data = AndroidUtil.getDaysAndWeekdays(monthValue,year);
        dateOnlineAdapter = new DateOnlineAdapter(data, new DateOnlineAdapter.IDateViewHolder(){
            @Override
            public void onClickItem(int positon) {
                resetTimView();
                String[] dateStr = data.get(positon).split(",");
                String monthStr = textViewMonth.getText().toString().substring(6, 8);
                String yearStr = textViewMonth.getText().toString().substring(10);
                dateChoose = dateStr[1]+"/"+monthStr+"/"+yearStr;
                setupRecyclerViewTime(doctorChoose,dateChoose,isMorning);
            }
        });
        dateOnlineAdapter.selectedPosition = 0;
        recyclerViewDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDate.setAdapter(dateOnlineAdapter);
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnHome.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        recyclerViewDoctors = findViewById(R.id.list_doctors);
        progressBarLoading = findViewById(R.id.progress_bar);
        btnHome = findViewById(R.id.home);
        btnNotice = findViewById(R.id.notice);

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
        if (v.getId() == R.id.btnMonth){
            showDialogMonth();
        }
        else if (v.getId() == R.id.block_time_morning){
            resetTimView();
            setupButtonTime(true);
            setupRecyclerViewTime(doctorChoose,dateChoose,isMorning);
        }
        else if (v.getId() == R.id.block_time_afternoon){
            resetTimView();
            setupButtonTime(false);
            setupRecyclerViewTime(doctorChoose,dateChoose,isMorning);
        }
        else if (v.getId() == R.id.back_btn)finish();
    }

    private void showDialogMonth() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_month_year);
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
        NumberPicker numberPickerMonth,numberPickerYear;
        RelativeLayout btnEnter;

        numberPickerMonth = dialog.findViewById(R.id.month);
        numberPickerYear = dialog.findViewById(R.id.year);
        btnEnter = dialog.findViewById(R.id.btnEnter);

        numberPickerMonth.setMinValue(1);
        numberPickerMonth.setMaxValue(12);
        numberPickerYear.setMinValue(2024);
        numberPickerYear.setMaxValue(2040);

        setCurrentMonth(numberPickerMonth,numberPickerYear);

        numberPickerMonth.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        btnEnter.setOnClickListener(v -> {
            resetTimView();
            getDetail(numberPickerMonth,numberPickerYear);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void getDetail(NumberPicker numberPickerMonth, NumberPicker numberPickerYear) {
        LocalDate today = LocalDate.now();
        int monthValue = numberPickerMonth.getValue();
        int yearValue = numberPickerYear.getValue();
        int month = 0, year = 0;
        if (yearValue < today.getYear() || ((yearValue == today.getYear()) && (monthValue < today.getMonthValue()))){
            month = today.getMonthValue();
            year = today.getYear();
        }
        else {
            month = monthValue;
            year = yearValue;
        }
        String formatMonth = (month < 10) ? "0" + month : String.valueOf(month);
        String monthStr = "Tháng "+formatMonth+", "+year;

        List<String> dateList = AndroidUtil.getDaysAndWeekdays(month,year);
        String dateStr[] = dateList.get(0).split(",");
        dateChoose = dateStr[1]+"/"+formatMonth+"/"+year;

        textViewMonth.setText(monthStr);
        setupRecyclerViewDate(month,year);
        setupRecyclerViewTime(doctorChoose,dateChoose,isMorning);
    }

    private void setCurrentMonth(NumberPicker numberPickerMonth, NumberPicker numberPickerYear) {
        LocalDate today = LocalDate.now();
        numberPickerMonth.setValue(today.getMonthValue());
        numberPickerYear.setValue(today.getYear());
    }

    private void resetTimView(){
        timeChoose = "";
        recyclerViewTime.setVisibility(View.GONE);
        progressBarTime.setVisibility(View.VISIBLE);
    }
    private void setupButtonTime(boolean morning){
        if (morning){
            isMorning = true;
            btnTimeMorning.setBackgroundResource(R.drawable.custom_menu_selected);
            iconMorning.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            textViewMorning.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            textViewTimeMorning.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            btnTimeAfternoon.setBackgroundResource(R.drawable.custom_menu_not_selected);
            iconAfternoon.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            textViewAfternoon.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            textViewTimeAfternoon.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }
        else {
            isMorning = false;
            btnTimeAfternoon.setBackgroundResource(R.drawable.custom_menu_selected);
            iconAfternoon.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            textViewAfternoon.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            textViewTimeAfternoon.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            btnTimeMorning.setBackgroundResource(R.drawable.custom_menu_not_selected);
            iconMorning.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            textViewMorning.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            textViewTimeMorning.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
        }
    }
}
package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DateAdapter;
import com.example.healthcare.adapters.DateOnlineAdapter;
import com.example.healthcare.adapters.TimeOnlineAdapter;
import com.example.healthcare.models.Doctor;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calendar_Online extends AppCompatActivity implements View.OnClickListener {
    SimpleDateFormat dateFormat;
    Calendar currentCalendar;
    RelativeLayout btnPrev,btnNext,btnEnter;
    ImageButton btnBack;
    TextView nameDoctor;
    int indexChoose = -1, rcvChoose = -1;
    String dateChoose;
    List<String> listAllTime,listTimeMorning, listTimeAfternoon, listTimeNight;
    List<String> weekDays;
    RecyclerView recyclerViewDate, recyclerViewTimeMorning, recyclerViewTimeAfternoon, recyclerViewTimeNight;
    DateOnlineAdapter dateOnlineAdapter;
    TimeOnlineAdapter timeMorningAdapter, timeAfternoonAdapter, timeNightAdapter;
    Doctor doctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_online);
        if (getIntent()!= null)doctor = AndroidUtil.getDoctorFromIntent(getIntent());
        currentCalendar = Calendar.getInstance();
        dateChoose = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(new Date());
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        initView();
        setOnClick();
        setListDateView();
        setListTimeView(dateChoose);
    }

    private void setOnClick() {
        btnBack.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
    }

    private void setListTimeView(String dateChoose) {
        listAllTime = new ArrayList<>();

        listAllTime.add("08:00");listAllTime.add("08:15");listAllTime.add("08:30");
        listAllTime.add("08:45");listAllTime.add("09:00");listAllTime.add("09:15");
        listAllTime.add("09:30");listAllTime.add("09:45");listAllTime.add("10:00");
        listAllTime.add("10:15");listAllTime.add("10:30");listAllTime.add("10:45");
        listAllTime.add("11:00");listAllTime.add("11:15");listAllTime.add("11:30");listAllTime.add("11:45");

        listAllTime.add("13:00");listAllTime.add("13:15");listAllTime.add("13:30");
        listAllTime.add("13:45");listAllTime.add("14:00");listAllTime.add("14:15");
        listAllTime.add("14:30");listAllTime.add("14:45");listAllTime.add("15:00");
        listAllTime.add("15:15");listAllTime.add("15:30");listAllTime.add("15:45");
        listAllTime.add("16:00");listAllTime.add("16:15");listAllTime.add("16:30");listAllTime.add("16:45");

        listAllTime.add("20:00");listAllTime.add("20:15");listAllTime.add("20:30");
        listAllTime.add("20:45");listAllTime.add("21:00");listAllTime.add("21:15");
        listAllTime.add("21:30");listAllTime.add("21:45");

        getListOnlineAppointmentByDateChoose(dateChoose);
    }

    private void getListOnlineAppointmentByDateChoose(String dateChoose) {
        FirebaseUtil.allOnlineAppointmentByDoctorId(doctor.getDoctorId())
                .whereEqualTo("appointmentDate", formatDate(dateChoose))
                .whereIn("stateAppointment", Arrays.asList(0, 1))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> results = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OnlineAppointment appointment = document.toObject(OnlineAppointment.class);
                            listAllTime.remove(appointment.getTime());
                        }
                        if (formatDate(dateChoose).equals(getCurrentDate())){
                            for (String time : listAllTime){
                                if (time.compareTo(AndroidUtil.currentTime()) > 0){
                                    results.add(time);
                                }
                            }
                            updateUI(results);
                        }
                        else updateUI(listAllTime);
                    }
                });
    }

    private void updateUI(List<String> results) {
        listTimeMorning = new ArrayList<>();
        listTimeAfternoon = new ArrayList<>();
        listTimeNight = new ArrayList<>();

        for (String time : results){
            if (AndroidUtil.isTime2Greater(time,"12:00")){
                listTimeMorning.add(time);
            }
            else if (AndroidUtil.isTime2Greater(time,"18:00")){
                listTimeAfternoon.add(time);
            }
            else listTimeNight.add(time);
        }
        listTimeMorningView(listTimeMorning);
        listTimeAfternoon(listTimeAfternoon);
        listTimeNight(listTimeNight);
    }

    private void listTimeNight(List<String> listTimeNight) {

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        timeNightAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeNight, new TimeOnlineAdapter.ITimeViewHolder() {
            @Override
            public void onClickItem(int position) {
                setUpTimeView(position,3);
            }
        });
        recyclerViewTimeNight.setLayoutManager(layoutManager);
        recyclerViewTimeNight.setAdapter(timeNightAdapter);
    }

    private void listTimeAfternoon(List<String> listTimeAfternoon) {
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        timeAfternoonAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeAfternoon, new TimeOnlineAdapter.ITimeViewHolder() {
            @Override
            public void onClickItem(int position) {
                setUpTimeView(position,2);
            }
        });
        recyclerViewTimeAfternoon.setLayoutManager(layoutManager);
        recyclerViewTimeAfternoon.setAdapter(timeAfternoonAdapter);
    }

    private void listTimeMorningView(List<String> listTimeMorning) {
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        timeMorningAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeMorning, new TimeOnlineAdapter.ITimeViewHolder() {
            @Override
            public void onClickItem(int position) {
                setUpTimeView(position,1);
            }
        });
        recyclerViewTimeMorning.setLayoutManager(layoutManager);
        recyclerViewTimeMorning.setAdapter(timeMorningAdapter);
    }

    private void setListDateView() {
        weekDays = getWeekDays(currentCalendar);
        int dateSelectedPosition = weekDays.indexOf(dateChoose);

        dateOnlineAdapter = new DateOnlineAdapter(weekDays, new DateAdapter.IDateViewHolder() {
            @Override
            public void onClickItem(int positon) {
                dateChoose = weekDays.get(positon);
                String dateSelected = weekDays.get(positon);
                String formattedDate = formatDate(dateSelected);
            }
        });
        if (dateSelectedPosition != -1)dateOnlineAdapter.selectedPosition = dateSelectedPosition;
        recyclerViewDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDate.setAdapter(dateOnlineAdapter);
    }
    public static List<String> getWeekDays(Calendar calendar) {
        List<String> days = new ArrayList<>();

        Calendar tempCal = (Calendar) calendar.clone();
        int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int offsetToMonday = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
        tempCal.add(Calendar.DAY_OF_MONTH, offsetToMonday);

        for (int i = 0; i < 6; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            String dayFormatted = sdf.format(tempCal.getTime());
            days.add(dayFormatted);
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }
    private String formatDate(String input) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> numbersList = new ArrayList<>();
        while (matcher.find()) {
            numbersList.add(matcher.group());
        }

        String day = null, month = null;
        if (numbersList.size() == 2) {
            day = numbersList.get(0);
            month = numbersList.get(1);
        } else if (numbersList.size() == 3) {
            day = numbersList.get(1);
            month = numbersList.get(2);
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return day + "/" + month + "/" + currentYear;
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnEnter = findViewById(R.id.btn_enter);
        nameDoctor = findViewById(R.id.name_doctor);
        nameDoctor.setText(doctor.getFullName());
        recyclerViewDate = findViewById(R.id.list_date);
        recyclerViewTimeMorning = findViewById(R.id.list_time_morning);
        recyclerViewTimeAfternoon = findViewById(R.id.list_time_afternoon);
        recyclerViewTimeNight = findViewById(R.id.list_time_night);
    }

    private void setUpTimeView(int position, int rcv){
        if (indexChoose != -1 && rcvChoose != -1){
            if (rcv != rcvChoose){
                if (rcvChoose == 1){
                    timeMorningAdapter.selectedPosition = -1;
                    timeMorningAdapter.notifyItemChanged(indexChoose);
                }
                if (rcvChoose == 2){
                    timeAfternoonAdapter.selectedPosition = -1;
                    timeAfternoonAdapter.notifyItemChanged(indexChoose);
                }
                else {
                    timeNightAdapter.selectedPosition = -1;
                    timeNightAdapter.notifyItemChanged(indexChoose);
                }
            }
        }
        indexChoose = position; rcvChoose = rcv;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPrev){
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            setListDateView();
        }
        if (v.getId() == R.id.btnNext){
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            setListDateView();
        }
    }
    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return format.format(currentDate);
    }
}
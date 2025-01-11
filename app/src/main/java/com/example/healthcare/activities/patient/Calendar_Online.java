package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DateAdapter;
import com.example.healthcare.adapters.DateOnlineAdapter;
import com.example.healthcare.adapters.TimeOnlineAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Calendar_Online extends AppCompatActivity implements View.OnClickListener {
    SimpleDateFormat dateFormat;
    Calendar currentCalendar;
    ProgressBar loadData;
    boolean checkFirstLoad = true;
    RelativeLayout btnPrev,btnNext,btnEnter,blockData;
    ImageButton btnBack;
    TextView nameDoctor;
    ImageView imageViewDoctor;
    int indexChoose = -1, rcvChoose = -1;
    String dateChoose, timeChoose = "", room = "";
    List<String> listAllTime,listTimeMorning, listTimeAfternoon, listTimeNight;
    List<String> weekDays;
    RecyclerView recyclerViewDate, recyclerViewTimeMorning, recyclerViewTimeAfternoon, recyclerViewTimeNight;
    DateOnlineAdapter dateOnlineAdapter;
    TimeOnlineAdapter timeMorningAdapter, timeAfternoonAdapter, timeNightAdapter;
    OnlineDoctor doctor;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_online);
        if (getIntent()!= null)doctor = AndroidUtil.getOnlineDoctorFromIntent(getIntent());
        getCurrentUser();
        currentCalendar = Calendar.getInstance();
        dateChoose = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(new Date());
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        initView();
        setOnClick();
        setListDateView();
        setListTimeView(dateChoose);
    }

    private void getCurrentUser() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user = task.getResult().toObject(User.class);
            }
        });
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
        FirebaseUtil.allOnlineAppointment()
                .whereEqualTo("doctor.doctorId", doctor.getDoctorId())
                .whereEqualTo("appointmentDate", AndroidUtil.formatDate(dateChoose))
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
                        if (dateChoose.contains("CN")){
                            updateUI(new ArrayList<>());
                            return;
                        }
                        if (AndroidUtil.formatDate(dateChoose).equals(AndroidUtil.getCurrentDate())){
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
        if (checkFirstLoad){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData.setVisibility(View.GONE);
                    blockData.setVisibility(View.VISIBLE);
//                    listTimeMorningView(listTimeMorning);
//                    listTimeAfternoon(listTimeAfternoon);
//                    listTimeNight(listTimeNight);
                }
            }, 500);
        }
        else {
            loadData.setVisibility(View.VISIBLE);
            blockData.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData.setVisibility(View.GONE);
                    blockData.setVisibility(View.VISIBLE);
//                    listTimeMorningView(listTimeMorning);
//                    listTimeAfternoon(listTimeAfternoon);
//                    listTimeNight(listTimeNight);
                }
            }, 500);
        }
    }

//    private void listTimeNight(List<String> listTimeNight) {
//
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
//        timeNightAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeNight, new TimeOnlineAdapter.ITimeViewHolder() {
//            @Override
//            public void onClickItem(int position) {
//                timeChoose = listTimeNight.get(position);
//                setUpTimeView(position,3);
//            }
//        });
//        recyclerViewTimeNight.setLayoutManager(layoutManager);
//        recyclerViewTimeNight.setAdapter(timeNightAdapter);
//    }

//    private void listTimeAfternoon(List<String> listTimeAfternoon) {
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
//        timeAfternoonAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeAfternoon, new TimeOnlineAdapter.ITimeViewHolder() {
//            @Override
//            public void onClickItem(int position) {
//                timeChoose = listTimeAfternoon.get(position);
//                setUpTimeView(position,2);
//            }
//        });
//        recyclerViewTimeAfternoon.setLayoutManager(layoutManager);
//        recyclerViewTimeAfternoon.setAdapter(timeAfternoonAdapter);
//    }

//    private void listTimeMorningView(List<String> listTimeMorning) {
//        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
//        timeMorningAdapter = new TimeOnlineAdapter(getApplicationContext(), listTimeMorning, new TimeOnlineAdapter.ITimeViewHolder() {
//            @Override
//            public void onClickItem(int position) {
//                timeChoose = listTimeMorning.get(position);
//                setUpTimeView(position,1);
//            }
//        });
//        recyclerViewTimeMorning.setLayoutManager(layoutManager);
//        recyclerViewTimeMorning.setAdapter(timeMorningAdapter);
//    }

    private void setListDateView() {
        weekDays = getWeekDays(currentCalendar);
        int dateSelectedPosition = weekDays.indexOf(dateChoose);

        dateOnlineAdapter = new DateOnlineAdapter(weekDays, new DateOnlineAdapter.IDateViewHolder() {
            @Override
            public void onClickItem(int positon) {
                checkFirstLoad = false;
                dateChoose = weekDays.get(positon);
                setListTimeView(dateChoose);
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

    private void initView() {
        loadData = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.back_btn);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnEnter = findViewById(R.id.btn_enter);
        blockData = findViewById(R.id.blockData);
        nameDoctor = findViewById(R.id.name_doctor);
        nameDoctor.setText("Bác sĩ "+doctor.getFullName());
        recyclerViewDate = findViewById(R.id.list_date);
        recyclerViewTimeMorning = findViewById(R.id.list_time_morning);
        recyclerViewTimeAfternoon = findViewById(R.id.list_time_afternoon);
        recyclerViewTimeNight = findViewById(R.id.list_time_night);
        imageViewDoctor = findViewById(R.id.image_doctor);
        deCodeImage(imageViewDoctor,doctor.getImageCode());
    }

    private void deCodeImage(ImageView imageViewDoctor, String imageCode) {
        if (imageCode != null && !imageCode.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageCode, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageViewDoctor.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imageViewDoctor.setImageResource(R.drawable.drugs);
            }
        } else {
            imageViewDoctor.setImageResource(R.drawable.drugs);
        }
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
        if (v.getId() == R.id.back_btn){
            finish();
        }
        if (v.getId() == R.id.btnPrev){
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            setListDateView();
        }
        if (v.getId() == R.id.btnNext){
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            setListDateView();
        }
        if (v.getId() == R.id.btn_enter){
            validData();
        }
    }

    private void validData() {
        if (timeChoose.equals("")){
            CustomToast.showToast(this,"Chưa chọn giờ khám",1000);
            return;
        }
        if (dateChoose.contains("CN")){
            CustomToast.showToast(this,"Chưa chọn ngày khám",1000);
            return;
        }
        OnlineAppointment appointment = new OnlineAppointment();
        appointment.setAppointmentDate(AndroidUtil.formatDate(dateChoose));
        appointment.setBookDate(AndroidUtil.getCurrentDate());
        appointment.setTime(timeChoose);
        Intent intent = new Intent(this,Person_Online.class);
        AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
        AndroidUtil.passOnlineAppointmentAsIntent(intent,appointment);
        if (user != null)intent.putExtra("userName",getNameFromFullName(user.getFullName()));
        startActivity(intent);
    }

    private String getNameFromFullName(String fullName){
        String[]str = fullName.split(" ");
        return str[str.length-1];
    }
}
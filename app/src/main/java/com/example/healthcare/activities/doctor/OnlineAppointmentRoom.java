package com.example.healthcare.activities.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.healthcare.activities.patient.MedicalRecords;
import com.example.healthcare.adapters.ChatRecyclerAdapter;
import com.example.healthcare.models.BloodPressure;
import com.example.healthcare.models.BloodSugar;
import com.example.healthcare.models.Calor;
import com.example.healthcare.models.ChatMessage;
import com.example.healthcare.models.ChatRoom;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.HealthIndex;
import com.example.healthcare.models.HeartRate;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.Sleep;
import com.example.healthcare.models.User;
import com.example.healthcare.testCallingVideo.Contants;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OnlineAppointmentRoom extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack, btnSend;
    EditText editTextChat;
    TextView namePatient, textNumber;
    RelativeLayout menuRecords, menuHealth, menuCallVideo, menuChange,
            btnHeartRate, btnPressure, btnSugar, btnSleep,btnKalo, btnPrev, btnNext;
    TextView textHeartRate, textPressure, textSugar, textSleep, textNotice,textKalo, textDate, textStatistical;
    LineChart lineChart;
    BarChart barChart;
    ProgressBar loadData;
    List<RelativeLayout> viewBtn;
    List<TextView> viewTexts;
    int indexHealthSetup = 0;
    long startTimestamp, endTimestamp;
    RecyclerView recyclerViewChat;
    ChatRecyclerAdapter chatRecyclerAdapter;
    String chatroomId, currentAppointmentId = "";
    ChatRoom chatRoom;
    User user;
    ListenerRegistration listenerRegistration, getListenerRegistration;
    ZegoSendCallInvitationButton videoCallBtn;
    private String currentDate;
    private int currentPosition;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_appointment_room);
        getDoctorName();
        initView();
        setOnclick();
        setupNumberWaiting();
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    setVideoCall(user.getUserId());
                }
                else CustomToast.showToast(OnlineAppointmentRoom.this,"Bệnh nhân chưa vào",1000);
            }
        });
    }

    private void getDoctorName() {
        FirebaseUtil.getOnlineDoctorDetailsById(FirebaseUtil.currentUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineDoctor doctor = task.getResult().toObject(OnlineDoctor.class);
                startService(doctor.getDoctorId(),doctor.getFullName());
            }
        });
    }

    private void setupNumberWaiting() {
        getListenerRegistration = FirebaseUtil.allOnlineAppointment().where(Filter.and(
                Filter.equalTo("doctor.doctorId", FirebaseUtil.currentUserId()),
                Filter.equalTo("stateAppointment", 0)
        )).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error getting documents", e);
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                int numberOfDocuments = snapshot.size();
                textNumber.setText(String.valueOf(numberOfDocuments));
            } else {
                textNumber.setText("0");
            }
        });
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        menuRecords.setOnClickListener(this);
        menuHealth.setOnClickListener(this);
        menuCallVideo.setOnClickListener(this);
        menuChange.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        namePatient = findViewById(R.id.fullName_Chat);
        menuRecords = findViewById(R.id.menu_records);
        menuHealth = findViewById(R.id.menu_health);
        menuCallVideo = findViewById(R.id.menu_call);
        videoCallBtn = findViewById(R.id.icon_videoCall);
        menuChange = findViewById(R.id.menu_change);
        btnSend = findViewById(R.id.message_send_btn);
        editTextChat = findViewById(R.id.chat_message_input);
        recyclerViewChat = findViewById(R.id.chat_recycler_view);
        textNumber = findViewById(R.id.number);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            finish();
        }
        else if (v.getId() == R.id.menu_change){
            if (listenerRegistration != null)listenerRegistration.remove();
            getFirstPatientInList();
        }
        else if (v.getId() == R.id.menu_records){
            if (currentAppointmentId.isEmpty()){
                CustomToast.showToast(this,"Bệnh nhân chưa vào",1000);
                return;
            }
            AndroidUtil.sharedPreferences(getApplicationContext(),"appointmentId",currentAppointmentId);
            if (user != null){
                AndroidUtil.sharedPreferences(getApplicationContext(),"patientId",user.getUserId());
            }
            Intent intent = new Intent(this, MedicalRecords.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.message_send_btn){
            if (currentAppointmentId.isEmpty()){
                CustomToast.showToast(this,"Bệnh nhân chưa vào",1000);
                return;
            }
            String message = editTextChat.getText().toString().trim();
            if (message.isEmpty())
                return;
            editTextChat.setText("");
            sendMessageToPatient(message);
        }
        else if (v.getId() == R.id.menu_health){
            indexHealthSetup = 0;
            if (user != null){
                showDialogHealth();
            }
            else CustomToast.showToast(this,"Bệnh nhân chưa vào",1000);
        }
        else if (v.getId() == R.id.menu_heart_rate){
            setupViewByIndexSelected(0);
            resetViewChart();
            getDataHealth(0,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.menu_blood_pressure){
            setupViewByIndexSelected(1);
            resetViewChart();
            getDataHealth(1,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.menu_blood_sugar){
            setupViewByIndexSelected(2);
            resetViewChart();
            getDataHealth(2,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.menu_sleep){
            setupViewByIndexSelected(3);
            resetViewChart();
            getDataHealth(3,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.menu_kalo){
            setupViewByIndexSelected(4);
            resetViewChart();
            getDataHealth(4,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.btnPrev){
            updateTimeFrame(-1);
            resetViewChart();
            startTimestamp = AndroidUtil.calculateTimestampByCurrentPosition(startTimestamp,currentPosition,-1);
            endTimestamp = AndroidUtil.calculateTimestampByCurrentPosition(endTimestamp,currentPosition,-1);
            getDataHealth(indexHealthSetup,startTimestamp,endTimestamp);
        }
        else if (v.getId() == R.id.btnNext){
            updateTimeFrame(1);
            resetViewChart();
            startTimestamp = AndroidUtil.calculateTimestampByCurrentPosition(startTimestamp,currentPosition,1);
            endTimestamp = AndroidUtil.calculateTimestampByCurrentPosition(endTimestamp,currentPosition,1);
            getDataHealth(indexHealthSetup,startTimestamp,endTimestamp);
        }
    }


    private void showDialogHealth() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_health_patient);
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

        TabLayout tabLayout;
        ImageView btnClose;

        btnHeartRate = dialog.findViewById(R.id.menu_heart_rate);
        btnPressure = dialog.findViewById(R.id.menu_blood_pressure);
        btnSugar = dialog.findViewById(R.id.menu_blood_sugar);
        btnClose = dialog.findViewById(R.id.icon_close);
        lineChart = dialog.findViewById(R.id.lineChart);
        barChart = dialog.findViewById(R.id.barChart);
        loadData = dialog.findViewById(R.id.loadingBar);
        tabLayout = dialog.findViewById(R.id.simpleTabLayout);
        btnSleep = dialog.findViewById(R.id.menu_sleep);
        textHeartRate = dialog.findViewById(R.id.text_heart_rate);
        textPressure = dialog.findViewById(R.id.text_blood_pressure);
        textSugar = dialog.findViewById(R.id.text_blood_sugar);
        textSleep = dialog.findViewById(R.id.text_sleep);
        btnKalo = dialog.findViewById(R.id.menu_kalo);
        textKalo = dialog.findViewById(R.id.text_kalo);
        textNotice = dialog.findViewById(R.id.notice);
        btnPrev = dialog.findViewById(R.id.btnPrev);
        btnNext = dialog.findViewById(R.id.btnNext);
        textDate = dialog.findViewById(R.id.text_date);
        textStatistical = dialog.findViewById(R.id.statistical);

        viewBtn = new ArrayList<>();
        viewTexts = new ArrayList<>();
        viewBtn.add(btnHeartRate);viewBtn.add(btnPressure);viewBtn.add(btnSugar);
        viewBtn.add(btnSleep);viewBtn.add(btnKalo);
        viewTexts.add(textHeartRate);viewTexts.add(textPressure);
        viewTexts.add(textSugar);viewTexts.add(textSleep);viewTexts.add(textKalo);

        btnPressure.setOnClickListener(this);
        btnHeartRate.setOnClickListener(this);
        btnSugar.setOnClickListener(this);
        btnSleep.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnKalo.setOnClickListener(this);

        setupViewDate(0);
        setupTabLayout(tabLayout);

        getStartTimeAndEndTime();
        getDataHealth(indexHealthSetup,startTimestamp, endTimestamp);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getStartTimeAndEndTime() {
        long[] timestamps = AndroidUtil.getTimestampListByPosition(0);
        startTimestamp = timestamps[0];
        endTimestamp = timestamps[1];
    }

    private void setupViewDate(int position) {
        currentPosition = position;
        calendar = Calendar.getInstance();
        currentDate = AndroidUtil.getTimeFrame(currentPosition);
        textDate.setText(currentDate);
    }

    private void updateTimeFrame(int direction) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM");
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM/yyyy");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        Calendar newCalendar = (Calendar) calendar.clone();

        switch (currentPosition) {
            case 0:
                newCalendar.add(Calendar.DATE, direction);
                break;

            case 1:
                newCalendar.add(Calendar.WEEK_OF_YEAR, direction);
                break;

            case 2:
                newCalendar.add(Calendar.MONTH, direction);
                break;

            case 3:
                newCalendar.add(Calendar.YEAR, direction);
                break;
        }

        switch (currentPosition) {
            case 0:
                currentDate = dayFormat.format(newCalendar.getTime());
                break;

            case 1:
                newCalendar.set(Calendar.DAY_OF_WEEK, newCalendar.getFirstDayOfWeek());
                Date startOfWeek = newCalendar.getTime();
                newCalendar.add(Calendar.DAY_OF_WEEK, 6);
                Date endOfWeek = newCalendar.getTime();
                SimpleDateFormat weekFormat = new SimpleDateFormat("dd/MM");
                currentDate = "Ngày " + weekFormat.format(startOfWeek) + " - Ngày " + weekFormat.format(endOfWeek);
                break;

            case 2:
                String monthYearResult = monthYearFormat.format(newCalendar.getTime());
                currentDate = monthYearResult.substring(0, 1).toUpperCase() + monthYearResult.substring(1);
                break;

            case 3:
                currentDate = "Năm " + yearFormat.format(newCalendar.getTime());
                break;
        }
        calendar = newCalendar;
        textDate.setText(currentDate);
    }

    private void setupTabLayout(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText("Ngày"));
        tabLayout.addTab(tabLayout.newTab().setText("Tuần"));
        tabLayout.addTab(tabLayout.newTab().setText("Tháng"));
        tabLayout.addTab(tabLayout.newTab().setText("Năm"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                resetViewChart();
                TextView tabText = (TextView) tab.getCustomView();
                if (tabText != null) {
                    tabText.setPaintFlags(tabText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
                setupViewDate(position);

                long[] timestamps = AndroidUtil.getTimestampListByPosition(position);
                startTimestamp = timestamps[0];
                endTimestamp = timestamps[1];
                getDataHealth(indexHealthSetup,startTimestamp,endTimestamp);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabText = (TextView) tab.getCustomView();
                if (tabText != null) {
                    tabText.setPaintFlags(tabText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getDataHealth(int indexHealthSetup, long startTimestamp, long endTimestamp) {
        FirebaseUtil.heartIndexCollectionByUserId(user.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                HealthIndex healthIndex = task.getResult().toObject(HealthIndex.class);
                List<Entry> entries = new ArrayList<>();
                List<BarEntry> barEntries = new ArrayList<>();
                float averageIndex ;long sum = 0; long minSystolic = 1000,maxSystolic = -1; long minDiastolic = 1000, maxDiastolic = -1;
                if (indexHealthSetup == 1){
                    assert healthIndex != null;
                    List<BloodPressure> bloodPressures = healthIndex.getBloodPressures();
                    for (int i = 0 ; i < bloodPressures.size() ; i ++){
                        if (bloodPressures.get(i).getTimestamp() >= startTimestamp && bloodPressures.get(i).getTimestamp() <= endTimestamp){
                            if (minDiastolic > bloodPressures.get(i).getDiastolic()){
                                minDiastolic = bloodPressures.get(i).getDiastolic();
                            }
                            if (minSystolic > bloodPressures.get(i).getSystolic()){
                                minSystolic = bloodPressures.get(i).getSystolic();
                            }
                            if (maxDiastolic < bloodPressures.get(i).getDiastolic()){
                                maxDiastolic = bloodPressures.get(i).getDiastolic();
                            }
                            if (maxSystolic < bloodPressures.get(i).getSystolic()){
                                maxSystolic = bloodPressures.get(i).getSystolic();
                            }
                            barEntries.add(new BarEntry(i, new float[]{bloodPressures.get(i).getSystolic(), bloodPressures.get(i).getDiastolic()}));
                        }
                    }
                    setupTextMinMax(minSystolic,maxSystolic,minDiastolic,maxDiastolic);
                    setupBloodPressureChart(barEntries);
                }
                else {
                    if (indexHealthSetup == 0){
                        assert healthIndex != null;
                        List<HeartRate> heartRates = healthIndex.getHeartRates(); int k = 0;
                        for (int i = 0 ; i < heartRates.size() ; i ++){
                            if (heartRates.get(i).getTimestamp() >= startTimestamp && heartRates.get(i).getTimestamp() <= endTimestamp){
                                sum += heartRates.get(i).getQuantity();
                                k++;
                                entries.add(new Entry(i,heartRates.get(i).getQuantity()));
                            }
                        }
                        averageIndex = (float) sum / k;
                        setupTextHeartAverage(averageIndex);
                    }
                    else if (indexHealthSetup == 2){
                        assert healthIndex != null;
                        List<BloodSugar> bloodSugars = healthIndex.getBloodSugars();int k = 0;
                        for (int i = 0 ; i < bloodSugars.size() ; i ++){
                            if (bloodSugars.get(i).getTimestamp() >= startTimestamp && bloodSugars.get(i).getTimestamp() <= endTimestamp){
                                sum += bloodSugars.get(i).getQuantity();
                                k++;
                                entries.add(new Entry(i,bloodSugars.get(i).getQuantity()));
                            }
                        }
                        averageIndex = (float) sum / k;
                        setupTextAverageSugar(averageIndex);
                    }
                    else if (indexHealthSetup == 3){
                        assert healthIndex != null;
                        List<Sleep> sleeps = healthIndex.getSleeps(); int k = 0;
                        for (int i = 0 ; i < sleeps.size() ; i ++){
                            if (sleeps.get(i).getTimestamp() >= startTimestamp && sleeps.get(i).getTimestamp() <= endTimestamp){
                                sum += sleeps.get(i).getTotalTime();
                                k ++;
                                entries.add(new Entry(i,sleeps.get(i).getTotalTime()));
                            }
                        }
                        averageIndex = (float) sum / k;
                        setupTextAverageSleep(averageIndex);
                    }
                    else {
                        assert healthIndex != null;
                        List<Calor> calorList = healthIndex.getCalories();
                        Map<Long, Float> dailyCaloriesMap = new HashMap<>();
                        for (int i = 0 ; i < calorList.size() ; i ++){
                            long timestamp = calorList.get(i).getTimestamp();
                            if (timestamp >= startTimestamp && timestamp <= endTimestamp){
                                long currentDay = TimeUnit.MILLISECONDS.toDays(timestamp);
                                float calories = calorList.get(i).getCalories();
                                dailyCaloriesMap.put(currentDay, dailyCaloriesMap.getOrDefault(currentDay, 0f) + calories);
                            }
                        }
                        int i = 0;
                        for (Map.Entry<Long, Float> entry : dailyCaloriesMap.entrySet()) {
                            sum += entry.getValue();
                            entries.add(new Entry(i,entry.getValue()));
                            i++;
                        }
                        averageIndex = (float) sum / i;
                        setupTextAverageCalories(averageIndex);
                    }
                    setupLineChart(lineChart,entries);
                }
            }
        });
    }

    private void setupTextAverageCalories(float averageIndex) {
        if (Float.isNaN(averageIndex)){
            textStatistical.setText("");
            return;
        }
        String formattedNumber = String.format("%.1f", averageIndex);
        textStatistical.setText(formattedNumber+" calo (trung bình)");
    }

    private void setupTextAverageSleep(float averageIndex) {
        if (Float.isNaN(averageIndex)){
            textStatistical.setText("");
            return;
        }
        String formattedNumber = String.format("%.1f", averageIndex);
        textStatistical.setText("trung bình "+formattedNumber+" giờ");
    }

    private void setupTextAverageSugar(float averageIndex) {
        if (Float.isNaN(averageIndex)){
            textStatistical.setText("");
            return;
        }
        String formattedNumber = String.format("%.1f", averageIndex);
        textStatistical.setText(formattedNumber + "mmol/L (trung bình)");
    }

    private void setupTextHeartAverage(float averageIndex) {
        if (Float.isNaN(averageIndex)){
            textStatistical.setText("");
            return;
        }
        long rounded = Math.round(averageIndex);
        textStatistical.setText(rounded + " nhịp/phút (trung bình)");
    }

    private void setupTextMinMax(long minSystolic, long maxSystolic, long minDiastolic, long maxDiastolic) {
        if (minDiastolic == 1000 || maxDiastolic == -1){
            textStatistical.setText("");
            return;
        }
        textStatistical.setText("tâm thu "+minSystolic+" - "+maxSystolic+" / "+"tâm trương "+ minDiastolic+" - "+maxDiastolic);
    }

    private void setupBloodPressureChart(List<BarEntry> barEntries) {
        loadData.setVisibility(View.GONE);
        if (barEntries != null && barEntries.isEmpty()){
            textNotice.setVisibility(View.VISIBLE);
            return;
        }
        barChart.setVisibility(View.VISIBLE);

        List<BarEntry> systolicEntries = new ArrayList<>();
        List<BarEntry> diastolicEntries = new ArrayList<>();

        for (int i = 0; i < barEntries.size(); i++) {
            BarEntry entry = barEntries.get(i);
            systolicEntries.add(new BarEntry(i * 2, entry.getYVals()[0]));
            diastolicEntries.add(new BarEntry(i * 2 + 1, entry.getYVals()[1]));
        }

        BarDataSet systolicDataSet = new BarDataSet(systolicEntries, "Tâm thu");
        systolicDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.mainColor));

        BarDataSet diastolicDataSet = new BarDataSet(diastolicEntries, "Tâm trương");
        diastolicDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));

        BarData barData = new BarData(systolicDataSet, diastolicDataSet);
        barChart.setData(barData);

        float barWidth = 0.5f;
        barChart.getBarData().setBarWidth(barWidth);

        float groupSpace = 0.7f;
        barChart.groupBars(0f, groupSpace, 0f);

        systolicDataSet.setValueTextSize(10f);
        diastolicDataSet.setValueTextSize(10f);
        systolicDataSet.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        diastolicDataSet.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        ValueFormatter integerFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        };

        systolicDataSet.setValueFormatter(integerFormatter);
        diastolicDataSet.setValueFormatter(integerFormatter);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(13f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(13f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);

        barChart.getLegend().setEnabled(true);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
    private void resetViewChart() {
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.GONE);
        loadData.setVisibility(View.VISIBLE);
        textNotice.setVisibility(View.GONE);
    }


    private void setupLineChart(LineChart lineChart, List<Entry> entries) {
        loadData.setVisibility(View.GONE);
        if (entries != null && entries.isEmpty()){
            textNotice.setVisibility(View.VISIBLE);
            return;
        }
        lineChart.setVisibility(View.VISIBLE);

        LineDataSet lineDataSet = new LineDataSet(entries, "Data Set 1");
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setLineWidth(0.7f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
        lineDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(13f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
        leftAxis.setValueFormatter(new LargeValueFormatter());

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));

        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineDataSet.setFillColor(ContextCompat.getColor(getApplicationContext(), R.color.light_blue));
        lineDataSet.setDrawFilled(true);

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setupFormatNumber(lineChart,lineDataSet);
        lineChart.invalidate();
    }

    private void setupFormatNumber(LineChart lineChart,LineDataSet dataSet){
        YAxis yAxis = lineChart.getAxisLeft();
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == (int) value) {
                    return String.format("%d", (int) value);
                } else {
                    return String.format("%.1f", value);
                }
            }
        });
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == (int) value) {
                    return String.format("%d", (int) value);
                } else {
                    return String.format("%.1f", value);
                }
            }
        });
    }
    private void setupViewByIndexSelected(int indexSelected){
        if (indexSelected != indexHealthSetup){
            viewBtn.get(indexSelected).setBackgroundResource(R.drawable.custom_menu_selected);
            viewTexts.get(indexSelected).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            viewBtn.get(indexHealthSetup).setBackgroundResource(R.drawable.custom_menu_not_selected);
            viewTexts.get(indexHealthSetup).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
        indexHealthSetup = indexSelected;
    }

    private void setVideoCall(String userId) {
        try {
            videoCallBtn.setIsVideoCall(true);
            videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(userId)));
        } catch (Exception e) {
            CustomToast.showToast(this,"Lỗi zego kết nối",1000);
            e.printStackTrace();
        }
    }

    private void sendMessageToPatient(String message) {
        if (chatRoom == null){
            getOrCreateChatroomModel(chatroomId);
        }
        else {
            chatRoom.setLastMessageTimestamp(Timestamp.now());
            chatRoom.setLastMessageSenderId(FirebaseUtil.currentUserId());
            chatRoom.setLastMessage(message);
            FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);
        }
        ChatMessage chatMessageModel = new ChatMessage(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "onComplete");
                        }
                    }
                });
    }

    private void getFirstPatientInList() {
        Query query = FirebaseUtil.allOnlineAppointment().where(Filter.and(
                        Filter.equalTo("appointmentDate", AndroidUtil.getCurrentDate()),
                        Filter.equalTo("doctor.doctorId",FirebaseUtil.currentUserId()),
                        Filter.equalTo("stateAppointment",0)
                ))
                .orderBy("time", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();

                if (!snapshot.isEmpty()) {
                    DocumentSnapshot firstDocument = snapshot.getDocuments().get(0);
                    OnlineAppointment firstAppointment = firstDocument.toObject(OnlineAppointment.class);
                    assert firstAppointment != null;
                    firstAppointment.setEvent(1);
                    saveStateAppointment(firstAppointment);
                } else {
                    CustomToast.showToast(this,"Không tìm thấy bệnh nhân",1000);
                }
            } else {
                Log.e("Query Error", "Error getting documents: ", task.getException());
            }
        });
    }

    private void saveStateAppointment(OnlineAppointment firstAppointment) {
        FirebaseUtil.onlineAppointment(firstAppointment.getId()).set(firstAppointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listenAccept(firstAppointment);
            }
        });
    }

    private void listenAccept(OnlineAppointment firstAppointment) {
        listenerRegistration = FirebaseUtil.onlineAppointment(firstAppointment.getId()).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                OnlineAppointment appointment = snapshot.toObject(OnlineAppointment.class);
                assert appointment != null;
                String fullNamePatient = appointment.getUser().getFullName();
                int eventInt = appointment.getEvent();
                int stateInt = appointment.getStateAppointment();
                if (eventInt == -1  && stateInt == 2){
                    currentAppointmentId = appointment.getId();
                    namePatient.setText(fullNamePatient);
                    user = appointment.getUser();
                    chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), appointment.getUser().getUserId());
                    getOrCreateChatroomModel(chatroomId);
                    setupChatRecyclerView();
                }
                else if (stateInt == 0 || stateInt == 1 || eventInt == 0){
                    user = null;
                    currentAppointmentId = "";
                    recyclerViewChat.setAdapter(null);
                    namePatient.setText("...");
                }
            } else {
                Log.d("Firestore", "No document found with ID" );
            }
        });
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerViewChat.setLayoutManager(manager);
        recyclerViewChat.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewChat.smoothScrollToPosition(0);
            }
        });
    }

    private void getOrCreateChatroomModel(String chatroomId) {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatRoom = task.getResult().toObject(ChatRoom.class);
                if (chatRoom == null) {
                    chatRoom = new ChatRoom(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), user.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);
                }
            }
        });
    }
    void startService(String userId, String fullName){
        try {
            ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
            ZegoUIKitPrebuiltCallService.init(getApplication(), Contants.appid, Contants.appSign, userId, fullName,callInvitationConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        if (user != null){
            AndroidUtil.removeSharedPreferences(getApplicationContext(),"patientId");
        }
        try {
            ZegoUIKitPrebuiltCallService.unInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
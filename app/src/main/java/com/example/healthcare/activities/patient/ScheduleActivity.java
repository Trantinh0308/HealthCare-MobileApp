package com.example.healthcare.activities.patient;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DateAdapter;
import com.example.healthcare.adapters.ScheduleDetailDrankAdapter;
import com.example.healthcare.adapters.ScheduleDetailDrinkAdapter;
import com.example.healthcare.adapters.ScheduleEnableAdapter;
import com.example.healthcare.adapters.ScheduleSkipAdapter;
import com.example.healthcare.models.CheckSchedule;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.Repeat;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView, recyclerViewDrink, recyclerViewDrank,
            recyclerViewEnable, recyclerViewSkip;
    DateAdapter dateAdapter;
    ScheduleDetailDrinkAdapter scheduleDetailDrinkAdapter;
    ScheduleDetailDrankAdapter scheduleDetailDrankAdapter;
    ScheduleEnableAdapter scheduleEnableAdapter;
    ScheduleSkipAdapter scheduleSkipAdapter;
    private Calendar currentCalendar;
    RelativeLayout btnPrev, btnNext,blockData;
    RelativeLayout btnAdd;
    ImageView btnBack;
    boolean checkFirstLoad = true;
    ProgressBar progressBarLoadData;
    List<Schedule> scheduleList, scheduleDrinkList,scheduleDrankList, scheduleEnableList, scheduleSkipList;
    List<CheckSchedule> checkScheduleList;
    SimpleDateFormat dateFormat;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initView();
        setOnClick();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        currentCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateSelected = dateFormat.format(new Date());
        getScheduleByUser(dateSelected);
        updateWeek();
    }

    private void getScheduleByUser(String dateSelected) {
        Query query = FirebaseUtil.scheduleAllCollection().whereEqualTo("userId",FirebaseUtil.currentUserId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    scheduleList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Schedule schedule = document.toObject(Schedule.class);
                        if (schedule != null && AndroidUtil.isDate2GreaterOrEqual(schedule.getStartDate(),dateSelected)) {
                            if (schedule.getEndDate().equals("") || AndroidUtil.isDate2GreaterOrEqual(dateSelected,schedule.getEndDate())){
                                scheduleList.add(schedule);
                            }
                        }
                    }
                    getCheckScheduleList(dateSelected);
                } else {
                    System.err.println("Error: " + task.getException());
                }
            }
        });
    }
    private void getCheckScheduleList(String dateSelected) {
        Query query =  FirebaseUtil.checkScheduleCollection().whereEqualTo("date",dateSelected);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    checkScheduleList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        CheckSchedule checkSchedule = document.toObject(CheckSchedule.class);
                        if (checkSchedule != null) {
                            checkScheduleList.add(checkSchedule);
                        }
                    }
                    checkDrankSchedule(dateSelected);
                } else {
                    System.err.println("Error: " + task.getException());
                }
            }
        });
    }

    private void checkDrankSchedule(String dateSelected) {
        scheduleDrinkList = new ArrayList<>();
        scheduleDrankList = new ArrayList<>();
        scheduleEnableList = new ArrayList<>();
        scheduleSkipList = new ArrayList<>();

        for (Schedule schedule : scheduleList){
            if (checkDrank(checkScheduleList,schedule) == 1)scheduleDrankList.add(schedule);
            else if (checkDrank(checkScheduleList,schedule) == 2){
                if (!AndroidUtil.isEarlier(AndroidUtil.getCurrentTimeDate(),schedule.getTime() + "/" + dateSelected)
                && dateSelected.contains(AndroidUtil.getCurrentDate())){
                    scheduleDrinkList.add(schedule);
                }
                else scheduleEnableList.add(schedule);
            }
            else scheduleSkipList.add(schedule);
        }
        showData();
    }

    private void showData() {
        if (checkFirstLoad){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarLoadData.setVisibility(View.GONE);
                    blockData.setVisibility(View.VISIBLE);
                    showListDrink(scheduleDrinkList);
                    showListEnable(scheduleEnableList);
                    showListDrank(scheduleDrankList);
                    showListSkip(scheduleSkipList);
                }
            }, 500);
        }
        else {
            progressBarLoadData.setVisibility(View.VISIBLE);
            blockData.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarLoadData.setVisibility(View.GONE);
                    blockData.setVisibility(View.VISIBLE);
                    showListDrink(scheduleDrinkList);
                    showListEnable(scheduleEnableList);
                    showListDrank(scheduleDrankList);
                    showListSkip(scheduleSkipList);
                }
            }, 700);
        }
    }

    private void showListSkip(List<Schedule> scheduleSkipList) {
        if (scheduleSkipList == null){
            scheduleSkipList = new ArrayList<>();
        }
        scheduleSkipAdapter = new ScheduleSkipAdapter(scheduleSkipList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewSkip.setLayoutManager(linearLayoutManager);
        recyclerViewSkip.setAdapter(scheduleSkipAdapter);
    }

    private void showListEnable(List<Schedule> scheduleEnableList) {
        if (scheduleEnableList == null){
            scheduleEnableList = new ArrayList<>();
        }
        List<Schedule> finalScheduleEnableList = scheduleEnableList;
        scheduleEnableAdapter = new ScheduleEnableAdapter(scheduleEnableList, new ScheduleEnableAdapter.IScheduleEnableItemViewHolder() {
            @Override
            public void onClickItem(int position) {
                Schedule schedule = finalScheduleEnableList.get(position);
                showDialogOperation(schedule,position, false);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewEnable.setLayoutManager(linearLayoutManager);
        recyclerViewEnable.setAdapter(scheduleEnableAdapter);
    }

    private void showListDrank(List<Schedule> scheduleDrankList) {
        if (scheduleDrankList == null){
            scheduleDrankList = new ArrayList<>();
        }
        scheduleDetailDrankAdapter = new ScheduleDetailDrankAdapter(scheduleDrankList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewDrank.setLayoutManager(linearLayoutManager);
        recyclerViewDrank.setAdapter(scheduleDetailDrankAdapter);
    }

    private void updateStatusSchedule(Schedule schedule,int position,boolean isDrink, int status) {
        CheckSchedule checkSchedule = new CheckSchedule();
        checkSchedule.setScheduleId(schedule.getSid());
        checkSchedule.setDate(dateFormat.format(new Date()));
        checkSchedule.setStatus(status);
        FirebaseUtil.checkScheduleCollection().add(checkSchedule).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                CustomToast.showToast(ScheduleActivity.this,"Cập nhật lịch hẹn thành công",1000);
                new Handler().postDelayed(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        if (status == 3){
                            if (isDrink){
                                updateListDrink(position);
                                updateListSkip(schedule);
                            }
                            else {
                                updateListEnable(position);
                                updateListSkip(schedule);
                            }
                        }
                        else if (status == 1){
                            updateListDrink(position);
                            updateListDrank(schedule);
                        }
                    }
                }, 2000);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateListEnable(int position) {
        scheduleEnableList.remove(position);
        scheduleEnableAdapter.notifyDataSetChanged();
    }

    private void showListDrink(List<Schedule> scheduleDrinkList) {
        if (scheduleDrinkList == null){
            scheduleDrinkList = new ArrayList<>();
        }
        List<Schedule> finalScheduleDrinkList = scheduleDrinkList;
        scheduleDetailDrinkAdapter = new ScheduleDetailDrinkAdapter(scheduleDrinkList, new ScheduleDetailDrinkAdapter.IScheduleItemViewHolder() {
            @Override
            public void onClickItem(int position) {
                Schedule schedule = finalScheduleDrinkList.get(position);
                showDialogOperation(schedule,position, true);
            }
            @Override
            public void onClickEnter(int position) {
                Schedule schedule = finalScheduleDrinkList.get(position);
                updateStatusSchedule(schedule,position,true,1);
            }

            @Override
            public void onClickRemind(int position) {
                Schedule schedule = finalScheduleDrinkList.get(position);
                saveRepeat(schedule);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewDrink.setLayoutManager(linearLayoutManager);
        recyclerViewDrink.setAdapter(scheduleDetailDrinkAdapter);
    }

    private void showDialogOperation(Schedule schedule,int position, boolean isDrink) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_operation_item);
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
        RelativeLayout btnSkip, btnEdit, btnDelete;
        ImageView btnClose;
        btnSkip = dialog.findViewById(R.id.btn_skip);
        btnEdit = dialog.findViewById(R.id.btn_edit);
        btnDelete = dialog.findViewById(R.id.btn_delete);
        btnClose = dialog.findViewById(R.id.icon_close);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatusSchedule(schedule,position,isDrink,3);
                dialog.dismiss();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, ScheduleEdit.class);
                AndroidUtil.passScheduleAsIntent(intent,schedule);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSchedule(schedule,position,isDrink);
                dialog.dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateListDrink(int position) {
        scheduleDrinkList.remove(position);
        scheduleDetailDrinkAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateListSkip(Schedule schedule) {
        scheduleSkipList.add(schedule);
        scheduleSkipAdapter.notifyDataSetChanged();
    }

    private void deleteSchedule(Schedule schedule, int position, boolean isDrink) {
        FirebaseUtil.scheduleCollectionById(schedule.getSid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                CustomToast.showToast(ScheduleActivity.this,"Xóa lịch hẹn thành công",1000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isDrink){
                            scheduleDrinkList.remove(position);
                            scheduleDetailDrinkAdapter.notifyDataSetChanged();
                        }
                        else {
                            scheduleEnableList.remove(position);
                            scheduleEnableAdapter.notifyDataSetChanged();
                        }
                    }
                }, 2000);
            }
        });
    }

    private void saveRepeat(Schedule schedule) {
        Repeat repeat = new Repeat();
        String [] arrStr = AndroidUtil.addOneHour(AndroidUtil.getCurrentTimeDate());
        assert arrStr != null;
        repeat.setTime(arrStr[0]);
        repeat.setDate(arrStr[1]);
        FirebaseUtil.repeatCollectionById(schedule.getSid()).set(repeat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag(TAG).d("onComplete: successful");
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateListDrank(Schedule schedule) {
        scheduleDrankList.add(schedule);
        scheduleDetailDrankAdapter.notifyDataSetChanged();
    }

    private int checkDrank(List<CheckSchedule> checkScheduleList, Schedule schedule){
        if (checkScheduleList == null || checkScheduleList.size() == 0) return 2;

        for (CheckSchedule checkSchedule : checkScheduleList) {
            if (checkSchedule.getScheduleId().equals(schedule.getSid())) {
                return checkSchedule.getStatus();
            }
        }
        return 2;
    }

    private void updateWeek() {
        List<String> weekDays = getWeekDays(currentCalendar);
        setDataByWeekContainCurrentDate(weekDays);
        String dateSelected = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(new Date());
        int dateSelectedPosition = weekDays.indexOf(dateSelected);

        dateAdapter = new DateAdapter(weekDays, new DateAdapter.IDateViewHolder() {
            @Override
            public void onClickItem(int positon) {
                checkFirstLoad = false;
                String dateSelected = weekDays.get(positon);
                String formattedDate = AndroidUtil.formatDate(dateSelected);
                getScheduleByUser(formattedDate);
            }
        });
        dateAdapter.selectedPosition = dateSelectedPosition;
        recyclerView.setAdapter(dateAdapter);
    }

    private void setDataByWeekContainCurrentDate(List<String> weekDays) {
        for (String weekDay : weekDays){
            if (AndroidUtil.formatDate(weekDay).equals(AndroidUtil.getCurrentDate())){
                getScheduleByUser(AndroidUtil.getCurrentDate());
                break;
            }
        }
    }

    public static List<String> getWeekDays(Calendar calendar) {
        List<String> days = new ArrayList<>();

        Calendar tempCal = (Calendar) calendar.clone();
        int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int offsetToMonday = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
        tempCal.add(Calendar.DAY_OF_MONTH, offsetToMonday);

        for (int i = 0; i < 7; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
            String dayFormatted = sdf.format(tempCal.getTime());
            days.add(dayFormatted);
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    private void setOnClick() {
        btnAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initView() {
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.list_date);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        recyclerViewDrink = findViewById(R.id.list_drink);
        recyclerViewDrank = findViewById(R.id.list_drank);
        progressBarLoadData = findViewById(R.id.progress_bar);
        blockData = findViewById(R.id.block_data);
        recyclerViewEnable = findViewById(R.id.list_enable);
        recyclerViewSkip = findViewById(R.id.list_skip);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            Intent intent = new Intent(ScheduleActivity.this, Main.class);
            startActivity(intent);
            finish();
        }
        if (v.getId() == R.id.btnAdd) {
            Intent intent = new Intent(ScheduleActivity.this, DetailSchedule.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.btnPrev) {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeek();
        }
        if (v.getId() == R.id.btnNext) {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeek();
        }
    }
}
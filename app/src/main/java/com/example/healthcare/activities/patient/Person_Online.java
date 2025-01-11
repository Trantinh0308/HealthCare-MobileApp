package com.example.healthcare.activities.patient;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.RelativeAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Person_Online extends AppCompatActivity implements View.OnClickListener {
    ImageView iconOne,iconTwo,iconThree,iconFour,iconFive;
    TextView room,specialist,doctorName,symptom,time,price,patientName;
    ImageButton btnBack;
    View lineOne,lineTwo,lineThree,lineFour;
    RelativeLayout btnNext, formCurrentUser, tick,blockIconOne,
            blockIconTwo, blockIconThree,blockIconFour,blockIconFive;
    OnlineDoctor doctor;
    User userChoose;
    String currentUserName;
    private int indexPersonSelected = -1,color,colorDefault;
    OnlineAppointment appointment;
    RelativeAdapter relativeAdapter;
    RecyclerView recyclerViewPersons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_online);
        color = ContextCompat.getColor(this, R.color.white);
        colorDefault = ContextCompat.getColor(this, R.color.icon_notselect);
        if (getIntent()!= null){
            doctor = AndroidUtil.getOnlineDoctorFromIntent(getIntent());
            appointment = AndroidUtil.getOnlineAppointmentFromIntent(getIntent());
            currentUserName = getIntent().getStringExtra("userName");
        }
        initView();
        setOnclick();
        setUpViewData();
        setUpViewIcon();
        getRelativeList();
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

        setBackgroundItem(formCurrentUser);
    }

    private void getRelativeList() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                assert user != null;
                userChoose = user;
                List<User> users = user.getRelativeList();
                setUpViewRelativeList(users);
            }
        });
    }

    private void setUpViewRelativeList(List<User> relativeIdList) {
        if (relativeIdList == null){
            relativeIdList = new ArrayList<>();
        }
        List<User> finalRelativeIdList = relativeIdList;
        relativeAdapter = new RelativeAdapter(getApplicationContext(), relativeIdList, new RelativeAdapter.IRelativeViewHolder() {
            @Override
            public void onClickItem(int positon) {
                userChoose = finalRelativeIdList.get(positon);
                setColorBlockFour();
                setBackgroundNotSelectedItem(formCurrentUser);
                tick.setVisibility(View.GONE);
                indexPersonSelected = positon;
            }

            @Override
            public void onDataLoaded(int size) {

            }
        });
        recyclerViewPersons.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        recyclerViewPersons.setAdapter(relativeAdapter);
    }

    private void setBackgroundNotSelectedItem(RelativeLayout formCurrentUser) {
        Drawable selectedBackground = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_form_person_notselected);
        formCurrentUser.setBackground(selectedBackground);
    }

    private void setColorBlockFour() {

    }

    private void setUpViewData() {
        room.setText("Phòng "+doctor.getRoom());
        specialist.setText(doctor.getSpecialist());
        doctorName.setText(doctor.getFullName());
        patientName.setText(currentUserName);
        time.setText(appointment.getTime()+" - "+appointment.getAppointmentDate());
        price.setText(AndroidUtil.formatPrice(doctor.getPrice()));
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        formCurrentUser.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initView() {
        room = findViewById(R.id.room);
        specialist = findViewById(R.id.specialist);
        doctorName = findViewById(R.id.doctor);
        patientName = findViewById(R.id.name);
        time = findViewById(R.id.time);
        tick = findViewById(R.id.tick);
        btnBack = findViewById(R.id.back_btn);
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
        symptom = findViewById(R.id.symptom);
        price = findViewById(R.id.price);
        recyclerViewPersons = findViewById(R.id.listPerson);
        btnNext = findViewById(R.id.btn_enter);
        formCurrentUser = findViewById(R.id.form_currentUser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            finish();
        }
        if (v.getId() == R.id.form_currentUser){
            getRelativeList();
            setBackgroundItem(formCurrentUser);
            tick.setVisibility(View.VISIBLE);
            relativeAdapter.clearBackgroundItems();
            indexPersonSelected = -1;
        }
        if (v.getId() == R.id.btn_enter){
            if (symptom.getText().length() == 0){
                CustomToast.showToast(this,"Nhập triệu chứng",1000);
                return;
            }
            appointment.setSymptom(symptom.getText().toString());
            Intent intent = new Intent(this, Payment.class);
            AndroidUtil.passOnlineAppointmentAsIntent(intent,appointment);
            AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
            AndroidUtil.passUserAsIntent(intent,userChoose);
            startActivity(intent);
        }
    }

    private void setBackgroundItem(RelativeLayout formRelative) {
        Drawable selectedBackground = ContextCompat.getDrawable(getApplicationContext(), R.drawable.custom_form_person_selected);
        formRelative.setBackground(selectedBackground);
    }
}

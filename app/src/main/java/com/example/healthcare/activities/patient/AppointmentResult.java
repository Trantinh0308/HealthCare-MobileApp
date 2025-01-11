package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.activities.doctor.AppointmentDetailResult;
import com.example.healthcare.adapters.AppointmentResultAdapter;
import com.example.healthcare.adapters.PatientAppointmentAdapter;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class AppointmentResult extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout btnOnlineAppointment, btnDirectAppointment, btnAddNew;
    TextView textOnlineAppointment, textDirectAppointment;
    ImageButton btnBack;
    RecyclerView recyclerView;
    AppointmentResultAdapter appointmentResultAdapter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_result);
        initView();
        setOnclick();
        setBackgroudButton(true);
        progressBar.setVisibility(View.VISIBLE);
        String appointmentId = AndroidUtil.getSharedPreferences(getApplicationContext(),"appointmentId");
        if (appointmentId != null){
            btnAddNew.setVisibility(View.VISIBLE);
            getUserDetail(appointmentId);
        }
        else setupAppointmentRecyclerView(FirebaseUtil.currentUserId());
    }

    private void getUserDetail(String appointmentId) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                User user = appointment.getUser();
                setupAppointmentRecyclerView(user.getUserId());
            }
        });
    }

    private void setupAppointmentRecyclerView(String userId) {
        progressBar.setVisibility(View.GONE);
        Query query = FirebaseUtil.allOnlineAppointment()
                .whereEqualTo("user.userId",userId)
                .whereEqualTo("stateAppointment",-1);
        FirestoreRecyclerOptions<OnlineAppointment> options = new FirestoreRecyclerOptions.Builder<OnlineAppointment>()
                .setQuery(query, OnlineAppointment.class).build();
        appointmentResultAdapter = new AppointmentResultAdapter(options, getApplicationContext(), new AppointmentResultAdapter.IViewHolder() {
            @Override
            public void onClickItem(int position) {
                String appointmentId = appointmentResultAdapter.getItem(position).getId();
                Intent intent = new Intent(AppointmentResult.this,com.example.healthcare.activities.patient.AppointmentDetailResult.class);
                intent.putExtra("appointmentId",appointmentId);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(appointmentResultAdapter);
        appointmentResultAdapter.startListening();
    }

    private void setBackgroudButton(boolean b) {
        int colorOfButtonSelected = ContextCompat.getColor(getApplicationContext(), R.color.color_background_menu_appointment);
        int colorOfButtonNotSelected = ContextCompat.getColor(getApplicationContext(), R.color.white);
        int colorOfTextSelected = ContextCompat.getColor(getApplicationContext(), R.color.text_Color);
        int colorOfTextNotSelected = ContextCompat.getColor(getApplicationContext(), R.color.light_gray);
        if (b) {
            btnOnlineAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonSelected));
            textOnlineAppointment.setTextColor(colorOfTextSelected);
            btnDirectAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonNotSelected));
            textDirectAppointment.setTextColor(colorOfTextNotSelected);
        } else {
            btnDirectAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonSelected));
            textOnlineAppointment.setTextColor(colorOfTextNotSelected);
            btnOnlineAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonNotSelected));
            textDirectAppointment.setTextColor(colorOfTextSelected);
        }
    }

    private void setOnclick() {
        btnOnlineAppointment.setOnClickListener(this);
        btnDirectAppointment.setOnClickListener(this);
        btnAddNew.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void initView() {
        btnOnlineAppointment = findViewById(R.id.btn_appointment);
        textOnlineAppointment = findViewById(R.id.text_appointment);
        btnDirectAppointment = findViewById(R.id.btn_direct);
        textDirectAppointment = findViewById(R.id.text_direct);
        recyclerView = findViewById(R.id.list_Appointment);
        progressBar = findViewById(R.id.loading);
        btnAddNew = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.back_btn);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAdd){
            Intent intent = new Intent(this, AppointmentDetailResult.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btn_direct){
            if (v.getId() == R.id.btn_direct) {
                setBackgroudButton(false);
            }
        }
        else if (v.getId() == R.id.back_btn){
            finish();
        }
    }
}
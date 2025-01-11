package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class MedicalRecords extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    RelativeLayout btnResult;
    TextView textViewName, textViewBirth, textViewGender, textViewPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);
        initView();
        setOnclick();
        String appointmentId = AndroidUtil.getSharedPreferences(getApplicationContext(),"appointmentId");
        if (appointmentId != null){
            getUserDetailByDoctor(appointmentId);
        }
        else getUserDetailByUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidUtil.removeSharedPreferences(getApplicationContext(),"appointmentId");
    }

    private void getUserDetailByDoctor(String appointmentId) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                User user = appointment.getUser();
                setupUserUI(user);
            }
        });
    }

    private void setupUserUI(User user) {
        textViewName.setText(user.getFullName());
        textViewGender.setText(user.getGender());
        textViewBirth.setText(user.getBirth());
        textViewPhone.setText(user.getPhoneNumber());
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnResult.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        textViewName = findViewById(R.id.fullName_user);
        textViewBirth = findViewById(R.id.birth_user);
        textViewGender = findViewById(R.id.gender_user);
        textViewPhone = findViewById(R.id.phone_user);
        btnResult = findViewById(R.id.block_appointment_result);
    }
    private void getUserDetailByUser() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                assert user != null;
                setupUserUI(user);
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn)finish();
        else if (v.getId() == R.id.block_appointment_result){
            Intent intent = new Intent(this, AppointmentResult.class);
            startActivity(intent);
        }
    }
}
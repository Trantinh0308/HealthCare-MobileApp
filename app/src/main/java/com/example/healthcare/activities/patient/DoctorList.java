package com.example.healthcare.activities.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DoctorAdapter2;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class DoctorList extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    RelativeLayout btnSearch, blockNotice;
    EditText editTextSearch;
    RecyclerView recyclerViewDoctors;
    ProgressBar progressBarLoading;
    DoctorAdapter2 doctorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        initView();
        setOnclick();
        if (getIntent() != null){
            String specialist = getIntent().getStringExtra("specialist");
            if (specialist != null){
                editTextSearch.setText(AndroidUtil.normalizeString(specialist));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDoctorsBySearch(AndroidUtil.normalizeString(specialist));
                    }
                }, 1000);
            }
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAllDoctors();
                    }
                }, 1000);
            }
        }
    }
    private void getAllDoctors() {
        progressBarLoading.setVisibility(View.GONE);
        Query query = FirebaseUtil.allOnlineDoctorCollection();
        FirestoreRecyclerOptions<OnlineDoctor> options = new FirestoreRecyclerOptions.Builder<OnlineDoctor>()
                .setQuery(query, OnlineDoctor.class).build();
        doctorAdapter = new DoctorAdapter2(options, getApplicationContext(), new DoctorAdapter2.IDoctorViewHolder() {
            @Override
            public void onClickView(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(DoctorList.this, DoctorDetail.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onClickBook(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(DoctorList.this, Calendar_Online.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onDataLoaded(int size) {
                if (size == 0){
                    blockNotice.setVisibility(View.VISIBLE);
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

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        editTextSearch = findViewById(R.id.search);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerViewDoctors = findViewById(R.id.doctorList);
        progressBarLoading = findViewById(R.id.loading);
        blockNotice = findViewById(R.id.notice);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn)finish();
        else if (v.getId() == R.id.btnSearch){
            String strSearch = editTextSearch.getText().toString().trim();
            if (strSearch.isEmpty()){
                CustomToast.showToast(this,"Nhập chuyên khoa",1000);
                return;
            }
            resetView();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDoctorsBySearch(AndroidUtil.normalizeString(strSearch));
                }
            }, 1000);
        }
    }

    private void getDoctorsBySearch(String strSearch) {
        progressBarLoading.setVisibility(View.GONE);
        Query query = FirebaseUtil.allOnlineDoctorCollection()
                .whereGreaterThanOrEqualTo("specialist", strSearch)
                .whereLessThan("specialist", strSearch + "\uf8ff");
        FirestoreRecyclerOptions<OnlineDoctor> options = new FirestoreRecyclerOptions.Builder<OnlineDoctor>()
                .setQuery(query, OnlineDoctor.class).build();
        doctorAdapter = new DoctorAdapter2(options, getApplicationContext(), new DoctorAdapter2.IDoctorViewHolder() {
            @Override
            public void onClickView(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(DoctorList.this, DoctorDetail.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onClickBook(int position) {
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(DoctorList.this, Calendar_Online.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onDataLoaded(int size) {
                if (size == 0){
                    blockNotice.setVisibility(View.VISIBLE);
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
    private void resetView(){
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerViewDoctors.setVisibility(View.GONE);
        blockNotice.setVisibility(View.GONE);
    }
}
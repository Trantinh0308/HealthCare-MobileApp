package com.example.healthcare.activities.patient;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.PatientAdapter;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class WaitingRoom extends AppCompatActivity {
    PatientAdapter patientAdapterChecking, patientAdapterWaiting;
    RelativeLayout blockEmpty;
    RecyclerView recyclerViewChecking, recyclerViewWaiting;
    ImageView btnBack;
    String appointmentId, doctorId, doctorName;
    ListenerRegistration listenerRegistration, listenerRegistrationChecking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        blockEmpty = findViewById(R.id.notice_checking_empty);
        btnBack = findViewById(R.id.back_btn);
        recyclerViewWaiting = findViewById(R.id.list_waiting);
        recyclerViewChecking = findViewById(R.id.list_checking);
        if(getIntent() != null){
            appointmentId = getIntent().getStringExtra("appointmentId");
            doctorId = getIntent().getStringExtra("doctorId");
            doctorName = getIntent().getStringExtra("doctorName");
        }

        setupDataWaitingList();
        setupDataCheckingList();
        listenEvent(appointmentId);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppointment(appointmentId);
            }
        });
    }

    private void listenEvent(String appointmentId) {
        listenerRegistration = FirebaseUtil.onlineAppointment(appointmentId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                OnlineAppointment appointment = snapshot.toObject(OnlineAppointment.class);
                assert appointment != null;
                int eventInt = appointment.getEvent();
                int stateInt = appointment.getStateAppointment();
                if (eventInt == 1  && stateInt == 0){
                    runOnUiThread(() -> showDialogInvitation(Gravity.CENTER));
                }
            } else {
                Log.d("Firestore", "No document found with ID: " + appointmentId);
            }
        });
    }

    private void showDialogInvitation(int center) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invitation);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = center;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == center) {
            dialog.setCancelable(false);
        } else {
            dialog.setCancelable(true);
        }
        RelativeLayout btnCancel, btnAccept;
        TextView textTitle, textCancel, textTopTitle;

        textTopTitle = dialog.findViewById(R.id.title_top);
        textTitle = dialog.findViewById(R.id.title_below);
        textCancel = dialog.findViewById(R.id.text_cancel);
        textTopTitle.setText("Mời khám");
        textTitle.setText("Bác sĩ mời bạn vào khám");
        textCancel.setText("Từ chối");

        btnCancel = dialog.findViewById(R.id.cancel);
        btnAccept = dialog.findViewById(R.id.agree);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnlineAppointment(appointmentId,2);
                dialog.dismiss();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnlineAppointment(appointmentId,1);
                Intent intent = new Intent(WaitingRoom.this, PatientChatDoctor.class);
                intent.putExtra("doctorId",doctorId);
                intent.putExtra("doctorName",doctorName);
                intent.putExtra("appointmentId",appointmentId);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getOnlineAppointment(String appointmentId, int action) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                if (action == 1){
                    appointment.setStateAppointment(2);
                }
                appointment.setEvent(-1);
                saveOnlineAppointment(appointment);
            }
        });
    }

    private void saveOnlineAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointmentId).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "onComplete: ");
            }
        });
    }

    private void setupDataCheckingList() {
        Query query = FirebaseUtil.allOnlineAppointment().where(Filter.and(
                        Filter.equalTo("doctor.doctorId", doctorId),
                        Filter.equalTo("stateAppointment", 2)
                ))
                .orderBy("time", Query.Direction.ASCENDING);

        listenerRegistrationChecking = query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("TAG", "Listen failed.", e);
                return;
            }

            if (snapshot != null) {
                int size = snapshot.size();
                if (size == 0) {
                    blockEmpty.setVisibility(View.VISIBLE);
                    recyclerViewChecking.setVisibility(View.GONE);
                } else {
                    blockEmpty.setVisibility(View.GONE);
                    recyclerViewChecking.setVisibility(View.VISIBLE);
                }
            }
        });

        FirestoreRecyclerOptions<OnlineAppointment> options = new FirestoreRecyclerOptions.Builder<OnlineAppointment>()
                .setQuery(query, OnlineAppointment.class).build();

        patientAdapterChecking = new PatientAdapter(options, getApplicationContext(), new PatientAdapter.IPatientViewHolder() {
            @Override
            public void onClickItem(int position) {

            }
        });

        recyclerViewChecking.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewChecking.setAdapter(patientAdapterChecking);
        patientAdapterChecking.startListening();
    }


    private void setupDataWaitingList() {
        Query query = FirebaseUtil.allOnlineAppointment().where(Filter.and(
                        Filter.equalTo("doctor.doctorId",doctorId),
                        Filter.equalTo("stateAppointment",0)
                ))
                .orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<OnlineAppointment> options = new FirestoreRecyclerOptions.Builder<OnlineAppointment>()
                .setQuery(query, OnlineAppointment.class).build();
        patientAdapterWaiting = new PatientAdapter(options,getApplicationContext(), new PatientAdapter.IPatientViewHolder() {
            @Override
            public void onClickItem(int positon) {

            }
        });
        recyclerViewWaiting.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewWaiting.setAdapter(patientAdapterWaiting);
        patientAdapterWaiting.startListening();
    }

    private void getAppointment(String appointmentId) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                if (appointment.getStateAppointment() != -1){
                    appointment.setStateAppointment(1);
                    saveAppointment(appointment);
                }
                else finish();
            }
        });
    }

    private void saveAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointment.getId()).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getAppointment(appointmentId);
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        if (listenerRegistrationChecking != null){
            listenerRegistrationChecking.remove();
        }
    }
}
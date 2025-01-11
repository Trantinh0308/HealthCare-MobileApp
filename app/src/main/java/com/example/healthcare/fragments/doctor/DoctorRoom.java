package com.example.healthcare.fragments.doctor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.activities.doctor.OnlineAppointmentRoom;
import com.example.healthcare.activities.patient.ScheduleActivity;
import com.example.healthcare.activities.patient.ScheduleEdit;
import com.example.healthcare.adapters.PatientAdapter;
import com.example.healthcare.adapters.PatientAppointmentAdapter;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;

import java.util.Timer;

import timber.log.Timber;

public class DoctorRoom extends Fragment {
    PatientAdapter patientAdapter;
    RelativeLayout btnRoom;
    RecyclerView recyclerViewPatients;
    public DoctorRoom() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_doctor_room, container, false);

        initView(itemView);
        setupData(FirebaseUtil.currentUserId());
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OnlineAppointmentRoom.class);
                startActivity(intent);
            }
        });
        return itemView;
    }

    private void setupData(String doctorId) {
        Query query = FirebaseUtil.allOnlineAppointment().where(Filter.and(
                Filter.equalTo("appointmentDate",AndroidUtil.getCurrentDate()),
                Filter.equalTo("doctor.doctorId",doctorId),
                Filter.equalTo("stateAppointment",0)
            ))
                .orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<OnlineAppointment> options = new FirestoreRecyclerOptions.Builder<OnlineAppointment>()
                .setQuery(query, OnlineAppointment.class).build();
        patientAdapter = new PatientAdapter(options, getContext(), new PatientAdapter.IPatientViewHolder() {
            @Override
            public void onClickItem(int positon) {
                OnlineAppointment appointment = patientAdapter.getItem(positon);
                showDialog(appointment);
            }

        });
        recyclerViewPatients.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPatients.setAdapter(patientAdapter);
        patientAdapter.startListening();
    }

    private void showDialog(OnlineAppointment appointment) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_doctorroom_item);
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
                sendInvitationPatient(appointment);
                createAppointmentRoom(appointment);
                dialog.dismiss();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void sendInvitationPatient(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointment.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment onlineAppointment = task.getResult().toObject(OnlineAppointment.class);
                onlineAppointment.setEvent(1);
                saveStateAppointment(onlineAppointment);
            }
        });
    }

    private void saveStateAppointment(OnlineAppointment onlineAppointment) {
        FirebaseUtil.onlineAppointment(onlineAppointment.getId()).set(onlineAppointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag("Send successful");
            }
        });
    }

    private void createAppointmentRoom(OnlineAppointment appointment) {
        //
    }

    private void initView(View itemView) {
        btnRoom = itemView.findViewById(R.id.block_room);
        recyclerViewPatients = itemView.findViewById(R.id.list_waiting);
    }
}
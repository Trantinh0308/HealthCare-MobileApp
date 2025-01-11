package com.example.healthcare.fragments.patient;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.activities.patient.AppointmentCalendar;
import com.example.healthcare.activities.patient.DoctorList;
import com.example.healthcare.activities.patient.WaitingRoom;
import com.example.healthcare.adapters.PatientAppointmentAdapter;
import com.example.healthcare.adapters.ReExaminationAdapter;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.ReExamination;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class Appointment extends Fragment implements View.OnClickListener {

    RelativeLayout btnOnlineAppointment, btnExaminedAppointment;
    TextView textOnlineAppointment, textExaminedAppointment;
    RecyclerView recyclerView;
    PatientAppointmentAdapter onlineAppointmentAdapter;
    ReExaminationAdapter reExaminationAdapter;
    ProgressBar progressBar;

    public Appointment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_appointment, container, false);
        initView(itemView);
        setOnclick();
        setBackgroundButton(true);
        getCurrentUserDetail(Arrays.asList(0, 1),1);
        return itemView;
    }

    private void getCurrentUserDetail(List<Integer> listStatus,int category) {
        FirebaseUtil.getUserDetailsById(FirebaseUtil.currentUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                assert user != null;
                List<User> relativeList = user.getRelativeList();
                if (relativeList == null){
                    relativeList = new ArrayList<>();
                }
                List<String>relativeId = new ArrayList<>();
                for (User relative : relativeList){
                    relativeId.add(relative.getUserId());
                }
                if (category == 1){
                    setupAppointmentRecyclerView(relativeId,FirebaseUtil.currentUserId(),listStatus);
                }
                else setupReAppointmentRecyclerView(relativeId,FirebaseUtil.currentUserId(),listStatus);
            }
        });
    }

    private void setupReAppointmentRecyclerView(List<String> relativeId, String currentId, List<Integer> listStatus) {
        Query query = null;
        if (!relativeId.isEmpty()){
            query = FirebaseUtil.reAppointmentCollection()
                    .where(Filter.and(
                            Filter.inArray("stateAppointment", listStatus),
                            Filter.or(
                                    Filter.equalTo("user.userId",currentId),
                                    Filter.inArray("user.userId",relativeId)
                            )
                    ));
        }
        else {
            query = FirebaseUtil.reAppointmentCollection()
                    .where(Filter.and(
                            Filter.inArray("stateAppointment", listStatus),
                            Filter.equalTo("user.userId",currentId)
                    ));
        }
        FirestoreRecyclerOptions<ReExamination> options = new FirestoreRecyclerOptions.Builder<ReExamination>()
                .setQuery(query, ReExamination.class).build();
        reExaminationAdapter = new ReExaminationAdapter(options, getContext(), new ReExaminationAdapter.IViewHolder() {
            @Override
            public void onClickItem(int position) {
                ReExamination reExamination = reExaminationAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), AppointmentCalendar.class);
                intent.putExtra("doctorId",reExamination.getDoctor().getDoctorId());
                startActivity(intent);
            }
        });
        setUpGUI();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reExaminationAdapter);
        reExaminationAdapter.startListening();
    }

    private void initView(View itemView) {
        btnOnlineAppointment = itemView.findViewById(R.id.btn_appointment);
        textOnlineAppointment = itemView.findViewById(R.id.text_appointment);
        btnExaminedAppointment = itemView.findViewById(R.id.btn_examined);
        textExaminedAppointment = itemView.findViewById(R.id.text_examined);
        recyclerView = itemView.findViewById(R.id.list_Appointment);
        progressBar = itemView.findViewById(R.id.loading);
    }

    private void setOnclick() {
        btnOnlineAppointment.setOnClickListener(this);
        btnExaminedAppointment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_appointment) {setBackgroundButton(true);
            resetGUI();
            getCurrentUserDetail(Arrays.asList(0, 1),1);
        }
        if (v.getId() == R.id.btn_examined) {
            setBackgroundButton(false);
            resetGUI();
            getCurrentUserDetail(Collections.singletonList(1),2);
        }
    }

    private void setupAppointmentRecyclerView(List<String> relativeList, String currentId, List<Integer> listStatus) {
        Query query = null;
        if (!relativeList.isEmpty()){
            query = FirebaseUtil.allOnlineAppointment()
                    .where(Filter.and(
                            Filter.inArray("stateAppointment", listStatus),
                            Filter.or(
                                    Filter.equalTo("user.userId",currentId),
                                    Filter.inArray("user.userId",relativeList)
                            )
                    ));
        }
        else {
            query = FirebaseUtil.allOnlineAppointment()
                    .where(Filter.and(
                            Filter.inArray("stateAppointment", listStatus),
                            Filter.equalTo("user.userId",currentId)
                    ));
        }
        FirestoreRecyclerOptions<OnlineAppointment> options = new FirestoreRecyclerOptions.Builder<OnlineAppointment>()
                .setQuery(query, OnlineAppointment.class).build();
        onlineAppointmentAdapter = new PatientAppointmentAdapter(options, getContext(), new PatientAppointmentAdapter.IViewHolder() {
            @Override
            public void onClickItem(int position) {
                OnlineAppointment appointment = onlineAppointmentAdapter.getItem(position);
                appointment.setStateAppointment(0);
                saveAppointment(appointment);
                Intent intent = new Intent(getActivity(), WaitingRoom.class);
                intent.putExtra("appointmentId",appointment.getId());
                intent.putExtra("doctorId",appointment.getDoctor().getDoctorId());
                intent.putExtra("doctorName",appointment.getDoctor().getFullName());
                startActivity(intent);
            }
        });
        setUpGUI();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(onlineAppointmentAdapter);
        onlineAppointmentAdapter.startListening();
    }

    private void saveAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointment.getId()).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag("Successful");
            }
        });
    }

    private void setBackgroundButton(boolean b) {
        int colorOfButtonSelected = ContextCompat.getColor(requireContext(), R.color.color_background_menu_appointment);
        int colorOfButtonNotSelected = ContextCompat.getColor(requireContext(), R.color.white);
        int colorOfTextSelected = ContextCompat.getColor(requireContext(), R.color.text_Color);
        int colorOfTextNotSelected = ContextCompat.getColor(requireContext(), R.color.light_gray);
        if (b) {
            btnOnlineAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonSelected));
            textOnlineAppointment.setTextColor(colorOfTextSelected);
            btnExaminedAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonNotSelected));
            textExaminedAppointment.setTextColor(colorOfTextNotSelected);
        } else {
            btnExaminedAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonSelected));
            textOnlineAppointment.setTextColor(colorOfTextNotSelected);
            btnOnlineAppointment.setBackgroundTintList(ColorStateList.valueOf(colorOfButtonNotSelected));
            textExaminedAppointment.setTextColor(colorOfTextSelected);
        }
    }
    private void resetGUI(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
    private void setUpGUI(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
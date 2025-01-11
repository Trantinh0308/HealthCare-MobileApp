package com.example.healthcare.activities.doctor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.activities.patient.DetailSchedule;
import com.example.healthcare.activities.patient.ScheduleActivity;
import com.example.healthcare.adapters.PrescriptionAdapter;
import com.example.healthcare.adapters.ScheduleItemAdapter;
import com.example.healthcare.models.AppointmentResult;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlinePrescription;
import com.example.healthcare.models.PrescriptionDetail;
import com.example.healthcare.models.ReExamination;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class AppointmentDetailResult extends AppCompatActivity implements View.OnClickListener {
    EditText editTextSymptom, editTextDiagnose, editTextRecommend;
    TextView textViewBtn, textDateReExamination;
    ProgressBar progressBarSave;
    RelativeLayout btnAddDrug, btnEnter, blockDate;
    RecyclerView recyclerViewDrug;
    List<OnlinePrescription> prescriptionList;
    PrescriptionAdapter prescriptionAdapter;
    Boolean checkSaved = false;
    ImageButton btnBack;
    SwitchCompat switchCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail_result);
        initView();
        setOnClick();
        initPrescriptionItemAdapter();
        switchCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                blockDate.setVisibility(View.VISIBLE);
            }
            else {
                textDateReExamination.setText("--/--/----");
                blockDate.setVisibility(View.GONE);
            }
        });
    }

    private void initPrescriptionItemAdapter() {
        prescriptionList = new ArrayList<>();
        prescriptionAdapter = new PrescriptionAdapter(prescriptionList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewDrug.setLayoutManager(linearLayoutManager);
        recyclerViewDrug.setAdapter(prescriptionAdapter);
    }

    private void setOnClick() {
        btnAddDrug.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        blockDate.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        editTextSymptom = findViewById(R.id.symptom);
        editTextDiagnose = findViewById(R.id.diagnose);
        editTextRecommend = findViewById(R.id.recommend);
        btnAddDrug = findViewById(R.id.top_block_schedule_drug);
        btnEnter = findViewById(R.id.btn_enter);
        recyclerViewDrug = findViewById(R.id.list_drug);
        textViewBtn = findViewById(R.id.btn_text);
        progressBarSave = findViewById(R.id.progress_save);
        switchCheck = findViewById(R.id.reExamination_Switch);
        blockDate = findViewById(R.id.block_date);
        textDateReExamination = findViewById(R.id.date_reExamination);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        OnlinePrescription onlinePrescription = AndroidUtil.getOnlinePrescriptionFromIntent(intent);
        ArrayList<String> detailPrescriptions = intent.getStringArrayListExtra("detailPrescriptions");
        if (detailPrescriptions == null){
            detailPrescriptions = new ArrayList<>();
        }
        List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();
        for (String detail : detailPrescriptions){
            String [] str = detail.split(";");
            PrescriptionDetail prescriptionDetail = new PrescriptionDetail();
            prescriptionDetail.setTime(str[0]);
            prescriptionDetail.setNote(str[1]);
            prescriptionDetails.add(prescriptionDetail);
        }
        onlinePrescription.setPrescriptionDetailList(prescriptionDetails);
        addPrescriptionList(onlinePrescription);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_block_schedule_drug){
            Intent intent = new Intent(this, Prescription.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btn_enter){
            if (!checkSaved){
                validateAppointmentResult();
            }
            else {
                AndroidUtil.removeSharedPreferences(getApplicationContext(),"appointmentId");
                Intent intent = new Intent(this,OnlineAppointmentRoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }
        else if (v.getId() == R.id.back_btn){
            finish();
        }
        else if (v.getId() == R.id.block_date){
            showDialogCalendar();
        }
    }

    private void showDialogCalendar() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_datepicker);
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
        DatePicker datePicker;
        RelativeLayout btnCancel, btnSelect;
        datePicker = dialog.findViewById(R.id.datePicker);
        btnCancel = dialog.findViewById(R.id.cancel);
        btnSelect = dialog.findViewById(R.id.select);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(datePicker);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getDate(DatePicker datePicker) {
        int dayOfMonth = datePicker.getDayOfMonth();
        int monthOfYear = datePicker.getMonth();
        int year = datePicker.getYear();
        String dayOfMonthStr = String.valueOf(dayOfMonth),
                monthOfYearStr = String.valueOf(monthOfYear+1);
        if (dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonth;
        if (monthOfYear+1 < 10) monthOfYearStr = "0" + (monthOfYear+1);
        String selectedDate = dayOfMonthStr + "/" + monthOfYearStr + "/" + year;
        textDateReExamination.setText(selectedDate);
    }

    private void validateAppointmentResult() {
        String symptom = editTextSymptom.getText().toString();
        String diagnose = editTextDiagnose.getText().toString();
        String recommend = editTextRecommend.getText().toString();
        String dateReExamination = textDateReExamination.getText().toString();
        if (symptom.isEmpty() || diagnose.isEmpty() || recommend.isEmpty()){
            CustomToast.showToast(this,"Điền đủ thông tin",1000);
            return;
        }
        String appointmentId = AndroidUtil.getSharedPreferences(getApplicationContext(),"appointmentId");
        if (appointmentId == null){
            CustomToast.showToast(this,"Lỗi xác định lịch hẹn",1000);
            return;
        }
        if (switchCheck.isChecked() && dateReExamination.equals("--/--/----")){
            CustomToast.showToast(this,"Chọn ngày tái khám",1000);
            return;
        }

        AppointmentResult result = new AppointmentResult();
        result.setAppointmentId(appointmentId);
        result.setSymptom(symptom);
        result.setDiagnose(diagnose);
        result.setRecommend(recommend);
        result.setPrescriptionList(prescriptionList);

        textViewBtn.setText("");
        progressBarSave.setVisibility(View.VISIBLE);
        saveResult(result);
    }

    private void saveResult(AppointmentResult result) {
        FirebaseUtil.onlineAppointmentResult(result.getAppointmentId()).set(result).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (switchCheck.isChecked()){
                    getAppointment();
                }
                else if (!prescriptionList.isEmpty()) addRemind();
            }
        });
    }

    private void getAppointment() {
        String appointmentId = AndroidUtil.getSharedPreferences(getApplicationContext(),"appointmentId");
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                addReAppointment(appointment);
            }
        });
    }

    private void addReAppointment(OnlineAppointment appointment) {
        ReExamination reExamination = new ReExamination();
        reExamination.setUser(appointment.getUser());
        reExamination.setDoctor(appointment.getDoctor());
        reExamination.setAppointmentDate(appointment.getAppointmentDate());
        reExamination.setReAppointmentDate(textDateReExamination.getText().toString());
        reExamination.setStateAppointment(1);
        FirebaseUtil.reAppointmentCollection().add(reExamination).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (!prescriptionList.isEmpty()) addRemind();
            }
        });
    }

    private void addRemind() {
        for (OnlinePrescription prescription : prescriptionList){
            if (!prescription.getRemind()) break;
            List<PrescriptionDetail> prescriptionDetails = prescription.getPrescriptionDetailList();
            for (PrescriptionDetail prescriptionDetail : prescriptionDetails){

                Schedule schedule = new Schedule();
                schedule.setSid(UUID.randomUUID().toString());
                schedule.setUserId(AndroidUtil.getSharedPreferences(getApplicationContext(),"patientId"));
                schedule.setDrugName(prescription.getDrugName());
                schedule.setImage(prescription.getImage());
                schedule.setTime(prescriptionDetail.getTime());
                schedule.setNote(prescriptionDetail.getNote());
                schedule.setStartDate(prescription.getStartDate());
                schedule.setEndDate(prescription.getEndDate());
                schedule.setFrequency(prescription.getFrequency());

                saveDataBase(schedule);
            }
        }
        setAppointmentState(AndroidUtil.getSharedPreferences(getApplicationContext(),"appointmentId"));
    }

    private void saveDataBase(Schedule schedule) {
        FirebaseUtil.scheduleCollectionById(schedule.getSid()).set(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag(TAG).d("onComplete: successful");
            }
        });
    }

    private void setAppointmentState(String appointmentIdByCurrentUser) {
        FirebaseUtil.onlineAppointment(appointmentIdByCurrentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                appointment.setStateAppointment(-1);
                saveAppointment(appointment);
            }
        });
    }

    private void saveAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointment.getId()).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                checkSaved = true;
                progressBarSave.setVisibility(View.GONE);
                textViewBtn.setText("Quay lại phòng khám");
                CustomToast.showToast(AppointmentDetailResult.this,"Lưu kết quả thành công",1000);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addPrescriptionList(OnlinePrescription prescription) {
        prescriptionList.add(prescription);
        prescriptionAdapter.notifyDataSetChanged();
    }
}
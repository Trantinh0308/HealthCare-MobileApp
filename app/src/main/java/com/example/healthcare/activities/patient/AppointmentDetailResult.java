package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.PatientPrescriptionAdapter;
import com.example.healthcare.models.AppointmentResult;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.Evaluate;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.OnlinePrescription;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDetailResult extends AppCompatActivity {
    TextView textViewSymptom, textViewDiagnose, textViewRecommend, textViewDoctorName, textViewDoctorGender,
    textViewDoctorDegree, textViewDoctorSpecialty;
    RecyclerView recyclerViewDrugs;
    RelativeLayout progressbar, btnCreateQR, btnEvaluate, btnDetail;
    ScrollView data;
    ImageButton btnBack;
    String appointmentId;
    PatientPrescriptionAdapter prescriptionAdapter;
    List<OnlinePrescription> onlinePrescriptionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reusult);
        initView();
        if (getIntent() != null){
            appointmentId = getIntent().getStringExtra("appointmentId");
            if (appointmentId != null){
                getResultData(appointmentId);
            }
        }
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnCreateQR.setOnClickListener(v -> {
            if (onlinePrescriptionList.isEmpty()){
                CustomToast.showToast(AppointmentDetailResult.this,"Đơn thuốc trống",2000);
            }
            else {
                getPrescriptionQR();
            }
        });
        btnEvaluate.setOnClickListener(v -> {
            showDialogEvaluate();
        });
        btnDetail.setOnClickListener(v -> {
            FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                    assert appointment != null;
                    OnlineDoctor doctor = appointment.getDoctor();
                    Intent intent = new Intent(AppointmentDetailResult.this, DoctorDetail.class);
                    AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                    startActivity(intent);
                }
            });
        });
    }

    private void showDialogEvaluate() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_evaluate);
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
        RatingBar rating;
        EditText editComment;
        RelativeLayout btnEnter, btnText;
        ImageView btnClose;
        ProgressBar progressBarLoading;
        btnClose = dialog.findViewById(R.id.icon_close);
        rating = dialog.findViewById(R.id.ratingBar);
        editComment = dialog.findViewById(R.id.comment);
        btnEnter = dialog.findViewById(R.id.btn_enter);
        btnText = dialog.findViewById(R.id.btnText);
        progressBarLoading = dialog.findViewById(R.id.loading_save);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnEnter.setOnClickListener(v -> {
            int ratingNumber = (int) rating.getRating();
            if (ratingNumber == 0){
                CustomToast.showToast(getApplicationContext(),"Chưa đánh giá",1000);
                return;
            }
            progressBarLoading.setVisibility(View.VISIBLE);
            btnText.setVisibility(View.GONE);
            getAppointment(ratingNumber,editComment.getText().toString(),dialog);
        });
        dialog.show();
    }

    private void getAppointment(int ratingNumber, String comment, Dialog dialog) {
       if (appointmentId != null){
           FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                   OnlineDoctor doctor = appointment.getDoctor();
                   User user = appointment.getUser();
                   Evaluate evaluate = new Evaluate();
                   evaluate.setDoctor(doctor);
                   evaluate.setUser(user);
                   evaluate.setRating(ratingNumber);
                   evaluate.setComment(comment);
                   evaluate.setDate(AndroidUtil.getCurrentDate());
                   saveEvaluate(evaluate,dialog);
               }
           });
       }
    }

    private void saveEvaluate(Evaluate evaluate, Dialog dialog) {
        FirebaseUtil.allEvaluateCollection().add(evaluate).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                dialog.dismiss();
                CustomToast.showToast(getApplicationContext(),"Đánh giá đã được lưu",1000);
            }
        });
    }

    private void getPrescriptionQR() {
        StringBuilder prescriptionStr = new StringBuilder();
        for (OnlinePrescription onlinePrescription : onlinePrescriptionList){
            prescriptionStr.append(createPrescriptionString(onlinePrescription));
            prescriptionStr.append("------------------------------------------").append("\n");
        }
        Bitmap qrCodeBitmap = generateQRCode(prescriptionStr.toString());
        showDialogPrescriptionQR(Gravity.CENTER,qrCodeBitmap);
    }

    private Bitmap generateQRCode(String prescriptionStr) {
        try {
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            String encodedStr = new String(prescriptionStr.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(encodedStr, BarcodeFormat.QR_CODE, 600, 600, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createPrescriptionString(OnlinePrescription onlinePrescription){
        StringBuilder sb = new StringBuilder();
        sb.append("Tên thuốc: ").append(onlinePrescription.getDrugName()).append("\n");
        sb.append("Đơn vị tính: ").append(onlinePrescription.getUnit()).append("\n");
        sb.append("Số lượng: ").append(onlinePrescription.getOrder()).append("\n");
        sb.append("Cách dùng: ").append(onlinePrescription.getNote()).append("\n");

        return sb.toString();
    }

    private void showDialogPrescriptionQR(int center, Bitmap qrCodeBitmap) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_prescription_qr);
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
        ImageView imageViewQR;
        RelativeLayout btnShare, btnClose;
        imageViewQR = dialog.findViewById(R.id.qrImageView);
        btnShare = dialog.findViewById(R.id.share);
        btnClose = dialog.findViewById(R.id.close);

        imageViewQR.setImageBitmap(qrCodeBitmap);

        btnShare.setOnClickListener(v -> {
            shareQRCode(qrCodeBitmap,AppointmentDetailResult.this);
        });

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public static File saveQRCodeToFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "prescription_qr.png");

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    public static void shareQRCode(Bitmap bitmap, android.content.Context context) {
        File qrFile = saveQRCodeToFile(bitmap);
        if (qrFile != null) {
            Uri uri = FileProvider.getUriForFile(context, "com.example.healthcare.fileprovider", qrFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ mã QR"));
        }
    }

    private void getResultData(String appointmentId) {
        FirebaseUtil.onlineAppointmentResult(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                AppointmentResult result = task.getResult().toObject(AppointmentResult.class);
                if (result != null){
                    onlinePrescriptionList = result.getPrescriptionList();
                    if (onlinePrescriptionList == null){
                        onlinePrescriptionList = new ArrayList<>();
                    }
                    getDoctorDetail(appointmentId,result);
                }
            }
        });
    }

    private void getDoctorDetail(String appointmentId, AppointmentResult result) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                OnlineDoctor doctor = appointment.getDoctor();
                setupView(doctor,result);
            }
        });
    }

    private void setupView(OnlineDoctor doctor, AppointmentResult result) {
        progressbar.setVisibility(View.GONE);
        data.setVisibility(View.VISIBLE);

        textViewDoctorName.setText(doctor.getFullName());
        textViewDoctorGender.setText(doctor.getGender());
        textViewDoctorDegree.setText(doctor.getDegree());
        textViewDoctorSpecialty.setText(doctor.getSpecialist());

        textViewSymptom.setText(result.getSymptom());
        textViewDiagnose.setText(result.getDiagnose());
        textViewRecommend.setText(result.getRecommend());
        if (result.getPrescriptionList() == null) return;
        prescriptionAdapter = new PatientPrescriptionAdapter(result.getPrescriptionList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerViewDrugs.setLayoutManager(linearLayoutManager);
        recyclerViewDrugs.setAdapter(prescriptionAdapter);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        textViewSymptom = findViewById(R.id.symptom);
        textViewDiagnose = findViewById(R.id.diagnose);
        textViewRecommend = findViewById(R.id.recommend);
        recyclerViewDrugs = findViewById(R.id.list_drug);
        textViewDoctorName = findViewById(R.id.fullName_doctor);
        textViewDoctorDegree = findViewById(R.id.doctor_degree);
        textViewDoctorGender = findViewById(R.id.gender_doctor);
        textViewDoctorSpecialty = findViewById(R.id.doctor_specialty);
        progressbar = findViewById(R.id.loading_data);
        data = findViewById(R.id.scrollView_body);
        btnCreateQR = findViewById(R.id.btnQR);
        btnEvaluate = findViewById(R.id.btn_evaluate);
        btnDetail = findViewById(R.id.btnDetail);
    }
}
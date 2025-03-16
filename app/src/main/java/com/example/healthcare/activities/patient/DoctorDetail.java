package com.example.healthcare.activities.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.DoctorAdapter2;
import com.example.healthcare.adapters.EvaluateAdapter;
import com.example.healthcare.models.Evaluate;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class DoctorDetail extends AppCompatActivity {
    OnlineDoctor doctor;
    RelativeLayout btnEnter;
    RecyclerView recyclerViewEvaluate;
    EvaluateAdapter evaluateAdapter;
    ImageView imageDoctor;
    ImageButton btnBack;
    TextView fullName, gender, specialist, price,degree,number_appointment,
            experience,achievement,biography, evaluateSum, ratingAverage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        doctor = AndroidUtil.getOnlineDoctorFromIntent(getIntent());
        initView();
        setupDetailDoctor(doctor);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDetail.this, Calendar_Online.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        imageDoctor = findViewById(R.id.block1_col1);
        fullName = findViewById(R.id.fullName_doctor);
        degree = findViewById(R.id.degree);
        gender = findViewById(R.id.gender);
        specialist = findViewById(R.id.specialist);
        price = findViewById(R.id.price);
        number_appointment = findViewById(R.id.number_appointment);
        experience = findViewById(R.id.experience);
        achievement = findViewById(R.id.achievement);
        biography = findViewById(R.id.work);
        btnEnter = findViewById(R.id.btn_enter);
        recyclerViewEvaluate = findViewById(R.id.evaluateList);
        evaluateSum = findViewById(R.id.evaluate_sum);
        ratingAverage = findViewById(R.id.rating_average);
    }
    @SuppressLint("SetTextI18n")
    private void setupDetailDoctor(OnlineDoctor doctor) {
        fullName.setText(doctor.getFullName());
        degree.setText(doctor.getDegree());
        gender.setText(doctor.getGender());
        specialist.setText(doctor.getSpecialist());
        number_appointment.setText(String.valueOf(doctor.getAppointment()));
        price.setText(AndroidUtil.formatPrice(doctor.getPrice()));
        experience.setText(doctor.getExperience());
        deCodeImage(doctor);
        if (!doctor.getAchievement().equals("")){
            String formatAchievement = doctor.getAchievement().replace(". ", ".<br>");
            achievement.setText(Html.fromHtml(formatAchievement));
        }
        else achievement.setText(doctor.getAchievement());

        if (!doctor.getBiography().equals("")){
            String formatWork = doctor.getBiography().replace(". ", ".<br>");
            biography.setText(Html.fromHtml(formatWork));
        }
        else biography.setText(doctor.getBiography());

        setUpEvaluate();
    }

    private void setUpEvaluate() {
        Query query = FirebaseUtil.allEvaluateCollection()
                .whereEqualTo("doctor.doctorId",doctor.getDoctorId());
        FirestoreRecyclerOptions<Evaluate> options = new FirestoreRecyclerOptions.Builder<Evaluate>()
                .setQuery(query, Evaluate.class).build();
        evaluateAdapter = new EvaluateAdapter(options, getApplicationContext(), new EvaluateAdapter.IViewHolder() {
            @Override
            public void onDataLoaded(int size) {
                evaluateSum.setText("("+size+")");
                if (size == 0){
                    ratingAverage.setText("");
                }
                else {
                    int totalRating = 0;
                    for (int i = 0; i < size; i++) {
                        Evaluate evaluate = evaluateAdapter.getItem(i);
                        totalRating += evaluate.getRating();
                    }
                    float average = (float) totalRating /size;
                    if (average == (int) average) {
                        ratingAverage.setText(String.format("%d", (int) average));
                    } else {
                        ratingAverage.setText(String.format("%.1f", average));
                    }
                }
            }
        });
        recyclerViewEvaluate.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvaluate.setAdapter(evaluateAdapter);
        evaluateAdapter.startListening();
    }

    private void deCodeImage(OnlineDoctor doctor) {
        if (doctor.getImageCode() != null && !doctor.getImageCode().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(doctor.getImageCode(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageDoctor.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imageDoctor.setImageResource(R.drawable.drugs);
            }
        } else {
            imageDoctor.setImageResource(R.drawable.drugs);
        }
    }
}
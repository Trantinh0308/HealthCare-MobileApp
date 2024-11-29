package com.example.healthcare.fragments.patient;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.activities.patient.DoctorDetail;
import com.example.healthcare.activities.patient.ScheduleActivity;
import com.example.healthcare.adapters.DoctorAdapter;
import com.example.healthcare.adapters.SlidePagerAdapter;
import com.example.healthcare.models.Doctor;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home extends Fragment implements View.OnClickListener {
    private SlidePagerAdapter slidePagerAdapter;
    ViewPager2 formSlide;
    private ImageButton circleOne, circleTwo, circleThree, circleFour, circleFive, circleSix;
    private List<ImageButton> listCircles = new ArrayList<>();
    private TextView textTemp;
    RelativeLayout schedule,bookAppointment,result,health;
    Timer timer;
    DoctorAdapter doctorAdapter;
    RecyclerView recyclerViewDoctor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView =  inflater.inflate(R.layout.fragment_home, container, false);
        initView (itemView);
        setOnClick();
        callApiWeather();
        createSlide();
        getDataListDoctor();
        return  itemView;
    }

    private void getDataListDoctor() {
        Query query = FirebaseUtil.allDoctorCollection();
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>()
                .setQuery(query,Doctor.class).build();

        doctorAdapter = new DoctorAdapter(options, getContext(), new DoctorAdapter.IDoctorViewHolder() {
            @Override
            public void onClickItem(int positon) {
                Doctor doctor = doctorAdapter.getItem(positon);
                Intent intent = new Intent(getContext(), DoctorDetail.class);
                AndroidUtil.passDoctorAsIntent(intent,doctor);
                startActivity(intent);
            }

            @Override
            public void onDataLoaded(int size) {

            }
        });

        recyclerViewDoctor.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        recyclerViewDoctor.setAdapter(doctorAdapter);
        doctorAdapter.startListening();
    }

    private void setOnClick() {
        schedule.setOnClickListener(this);
        bookAppointment.setOnClickListener(this);
        result.setOnClickListener(this);
        health.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        callApiWeather();
    }

    private void initView(View itemView) {
        textTemp = itemView.findViewById(R.id.temp);
        schedule = itemView.findViewById(R.id.block_schedule);
        bookAppointment = itemView.findViewById(R.id.block_booking);
        result = itemView.findViewById(R.id.block_medical_record);
        health = itemView.findViewById(R.id.block_health);
        formSlide = itemView.findViewById(R.id.slide);
        circleOne = itemView.findViewById(R.id.circle_one);
        listCircles.add(circleOne);
        circleTwo = itemView.findViewById(R.id.circle_two);
        listCircles.add(circleTwo);
        circleThree = itemView.findViewById(R.id.circle_three);
        listCircles.add(circleThree);
        circleFour = itemView.findViewById(R.id.circle_four);
        listCircles.add(circleFour);
        circleFive = itemView.findViewById(R.id.circle_five);
        listCircles.add(circleFive);
        circleSix = itemView.findViewById(R.id.circle_six);
        listCircles.add(circleSix);
        recyclerViewDoctor = itemView.findViewById(R.id.list_doctor);
    }

    private void createSlide() {
        slidePagerAdapter = new SlidePagerAdapter(getChildFragmentManager(), getLifecycle());
        formSlide.setAdapter(slidePagerAdapter);

        formSlide.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setBackgroundCircle(position);
            }
        });

        final boolean[] isForward = {true};
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int nextSlide;
                        if (isForward[0]) {
                            nextSlide = formSlide.getCurrentItem() + 1;
                            if (nextSlide >= slidePagerAdapter.getItemCount()) {
                                isForward[0] = false;
                                nextSlide = formSlide.getCurrentItem() - 1;
                            }
                        } else {
                            nextSlide = formSlide.getCurrentItem() - 1;
                            if (nextSlide < 0) {
                                isForward[0] = true;
                                nextSlide = formSlide.getCurrentItem() + 1;
                            }
                        }
                        setBackgroundCircle(nextSlide);
                        // Di chuyển tới slide tiếp theo
                        formSlide.setCurrentItem(nextSlide, true);
                    }
                });
            }
        }, 4000, 4000);
    }

    private void setBackgroundCircle(int position) {
        if (isAdded()) {
            int color = ContextCompat.getColor(requireContext(), R.color.colorCircleSelected);
            int colorDefault = ContextCompat.getColor(requireContext(), R.color.mainColor);
            for (int i = 0; i < listCircles.size(); i++) {
                if (i == position) {
                    listCircles.get(i).setBackgroundTintList(ColorStateList.valueOf(color));
                } else {
                    // Thiết lập màu mặc định cho các circle không được chọn
                    listCircles.get(i).setBackgroundTintList(ColorStateList.valueOf(colorDefault));
                }
            }
        }
    }

    private void callApiWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "https://api.openweathermap.org/data/2.5/weather?q=Hanoi&appid=2c7dd62facdd0cb9881ca1fc1d17e974";

                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");
                        double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");

                        double tempMinCelsius = tempMin - 273.15;
                        double tempMaxCelsius = tempMax - 273.15;

                        double tempAverage = (double) (tempMinCelsius + tempMaxCelsius) / 2.0;
                        int tempAverage_round = (int) Math.round(tempAverage);
                        String tempAStr = String.valueOf(tempAverage_round) + "°C";
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textTemp.setText(tempAStr);
                                }
                            });
                        }
                    } else {
                        Log.e("Weather", "Lỗi: " + response.code() + " - " + response.message());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v.getId() == R.id.block_schedule){
            intent = new Intent(getActivity(), ScheduleActivity.class);
        }
        if (intent != null){
            startActivity(intent);
        }
    }
}
package com.example.healthcare.fragments.patient;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.activities.Login;
import com.example.healthcare.activities.patient.DoctorDetail;
import com.example.healthcare.activities.patient.DoctorList;
import com.example.healthcare.activities.patient.FaceRecognition;
import com.example.healthcare.activities.patient.HealthQuestion;
import com.example.healthcare.activities.patient.Main;
import com.example.healthcare.activities.patient.MedicalRecords;
import com.example.healthcare.activities.patient.Register;
import com.example.healthcare.activities.patient.ScheduleActivity;
import com.example.healthcare.activities.patient.Specialist;
import com.example.healthcare.adapters.DoctorAdapter;
import com.example.healthcare.adapters.SlidePagerAdapter;
import com.example.healthcare.models.BloodPressure;
import com.example.healthcare.models.BloodSugar;
import com.example.healthcare.models.Calor;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.HealthIndex;
import com.example.healthcare.models.HeartRate;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.Role;
import com.example.healthcare.models.Sleep;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class Home extends Fragment implements View.OnClickListener {
    private SlidePagerAdapter slidePagerAdapter;
    ViewPager2 formSlide;
    private ImageButton circleOne, circleTwo, circleThree, circleFour, circleFive, circleSix;
    private List<ImageButton> listCircles = new ArrayList<>();
    private TextView textTemp;
    RelativeLayout schedule,bookAppointment,result,health, btnDoctors;
    Timer timer;
    DoctorAdapter doctorAdapter;
    RecyclerView recyclerViewDoctor;
    private FitnessOptions fitnessOptions;
    private static final String TAG = "GoogleFit";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1001;

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
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account == null) {
            Log.e(TAG, "No account found.");
        } else {
            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this,
                        REQUEST_OAUTH_REQUEST_CODE,
                        account,
                        fitnessOptions);
            } else {
                getDataFromGoogleFit(account);
            }
        }

        return  itemView;
    }

    private void getDataFromGoogleFit(GoogleSignInAccount account) {
        HealthIndex healthIndex = new HealthIndex();

        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        long startTime = threeMonthsAgo.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long endTime = today.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Fitness.getHistoryClient(getActivity(), account)
                .readData(new com.google.android.gms.fitness.request.DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build()).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<HeartRate> heartRates = new ArrayList<>();
                        List<BloodPressure> bloodPressures = new ArrayList<>();
                        List<BloodSugar> bloodSugars = new ArrayList<>();
                        List<Sleep> sleeps = new ArrayList<>();
                        long checkTimeStamp = -1;
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                float value = dataPoint.getValue(Field.FIELD_BPM).asFloat();
                                long timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS);
                                HeartRate heartRate = new HeartRate();
                                heartRate.setQuantity(value);
                                heartRate.setTimestamp(timestamp);
                                heartRates.add(heartRate);

                                long systolic = AndroidUtil.getNumberInRange(80,120);
                                long diastolic = AndroidUtil.getNumberInRange(60,80);
                                BloodPressure bloodPressure = new BloodPressure();
                                bloodPressure.setSystolic(systolic);
                                bloodPressure.setDiastolic(diastolic);
                                bloodPressure.setTimestamp(timestamp);
                                bloodPressures.add(bloodPressure);

                                float quantity = AndroidUtil.getFloatRange((float) 3.9F, (float) 5.5F);
                                BloodSugar bloodSugar = new BloodSugar();
                                bloodSugar.setQuantity(quantity);
                                bloodSugar.setTimestamp(timestamp);
                                bloodSugars.add(bloodSugar);

                                long currentDay = TimeUnit.MILLISECONDS.toDays(timestamp);

                                if (currentDay != checkTimeStamp) {
                                    float total = AndroidUtil.getFloatRange(5.0F, 10.0F);
                                    Sleep sleep = new Sleep();
                                    sleep.setTotalTime(total);
                                    sleep.setTimestamp(timestamp);
                                    sleeps.add(sleep);

                                    checkTimeStamp = currentDay;
                                }
                            }
                        }
                        healthIndex.setHeartRates(heartRates);
                        healthIndex.setBloodPressures(bloodPressures);
                        healthIndex.setBloodSugars(bloodSugars);
                        healthIndex.setSleeps(sleeps);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to read heart rate data", e);
                    }
                });
        Fitness.getHistoryClient(getActivity(), account)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_CALORIES_EXPENDED)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build()).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<Calor> calories = new ArrayList<>();
                        for (DataSet dataSet : dataReadResponse.getDataSets()) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                float value = dataPoint.getValue(Field.FIELD_CALORIES).asFloat();  // Lấy giá trị calo tiêu thụ
                                long timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS);
                                Calor calor = new Calor();
                                calor.setCalories(value);
                                calor.setTimestamp(timestamp);
                                calories.add(calor);
                            }
                        }
                        healthIndex.setCalories(calories);
                        saveHealthIndex(healthIndex);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to read calories data", e);
                    }
                });
    }

    private void saveHealthIndex(HealthIndex healthIndex) {
        FirebaseUtil.heartIndexCollectionByUserId(FirebaseUtil.currentUserId()).set(healthIndex).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag(TAG).d("onComplete: saveHealthIndex");
            }
        });
    }

    private void getDataListDoctor() {
        Query query = FirebaseUtil.allOnlineDoctorCollection();
        FirestoreRecyclerOptions<OnlineDoctor> options = new FirestoreRecyclerOptions.Builder<OnlineDoctor>()
                .setQuery(query, OnlineDoctor.class).build();

        doctorAdapter = new DoctorAdapter(options, getContext(), new DoctorAdapter.IDoctorViewHolder() {
            @Override
            public void onClickItem(int position) {
                if (FirebaseUtil.currentUserId() == null) {
                    showDialogLogin(Gravity.CENTER);
                    return;
                }
                OnlineDoctor doctor = doctorAdapter.getItem(position);
                Intent intent = new Intent(getContext(), DoctorDetail.class);
                AndroidUtil.passOnlineDoctorAsIntent(intent,doctor);
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
        btnDoctors.setOnClickListener(this);
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
        btnDoctors = itemView.findViewById(R.id.btnDoctorList);
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
        if (FirebaseUtil.currentUserId() == null) {
            showDialogLogin(Gravity.CENTER);
            return;
        }
        Intent intent = null;
        if (v.getId() == R.id.block_schedule){
            intent = new Intent(getActivity(), ScheduleActivity.class);
        }
        else if (v.getId() == R.id.block_medical_record){
            intent = new Intent(getActivity(), MedicalRecords.class);
        }
        else if (v.getId() == R.id.block_health){
            intent = new Intent(getActivity(), HealthQuestion.class);
        }
        else if (v.getId() == R.id.btnDoctorList){
            intent = new Intent(getActivity(), DoctorList.class);
        }
        else if (v.getId() == R.id.block_booking){
            intent = new Intent(getActivity(), Specialist.class);
        }
        startActivity(intent);
    }

    private void showDialogLogin(int center) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notice_center);
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
        RelativeLayout btnCancel, btnLogin;
        btnCancel = dialog.findViewById(R.id.cancel);
        btnLogin = dialog.findViewById(R.id.agree);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDialog(int center) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login_biometrics);
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
        RelativeLayout btnFace, btnFingerprint;

        btnFace = dialog.findViewById(R.id.login_face);
        btnFingerprint = dialog.findViewById(R.id.login_fingerprint);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkHasFace();
            }
        });
        btnFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkFingerprint();
            }
        });
        dialog.show();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkFingerprint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Xác thực sinh trắc học")
                    .setSubtitle("Vui lòng xác thực bằng vân tay")
                    .setNegativeButtonText("Hủy")
                    .build();

            Executor executor = ContextCompat.getMainExecutor(getContext());
            BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Intent intent = new Intent(getActivity(), MedicalRecords.class);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    CustomToast.showToast(getActivity(), "Lỗi", 1000);
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }
            });

            biometricPrompt.authenticate(promptInfo);
        } else {
            CustomToast.showToast(getActivity(), "Thiết bị không hỗ trợ sinh trắc học hoặc chưa cài đặt.", 1000);
        }
    }

    private void checkHasFace() {
        FirebaseUtil.getRoleCollectionByUserId(FirebaseUtil.currentUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Role role = task.getResult().toObject(Role.class);
                assert role != null;
                if (!role.isCheckFace()){
                    showDialogGetFace(Gravity.CENTER);
                }
                else checkFace();
            }
        });
    }

    private void checkFace() {
        Intent intent = new Intent(getActivity(), FaceRecognition.class);
        startActivity(intent);
    }

    private void showDialogGetFace(int center) {
        final Dialog dialog = new Dialog(getActivity());
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
        TextView textTitle, textCancel, textTitleTop;

        textTitleTop = dialog.findViewById(R.id.title_top);
        textTitle = dialog.findViewById(R.id.title_below);
        textCancel = dialog.findViewById(R.id.text_cancel);
        textTitleTop.setText("Chưa có dữ liệu");
        textTitle.setText("Vui lòng cung cấp dữ liệu khuông mặt");
        textCancel.setText("Từ chối");

        btnCancel = dialog.findViewById(R.id.cancel);
        btnAccept = dialog.findViewById(R.id.agree);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Register.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
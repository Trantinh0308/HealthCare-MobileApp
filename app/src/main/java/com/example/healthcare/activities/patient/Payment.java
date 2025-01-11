package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.notification.AlarmReceiver;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.example.healthcare.zalopayService.Api.CreateOrder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class Payment extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack;
    ImageView iconOne,iconTwo,iconThree,iconFour,iconFive;
    View lineOne,lineTwo,lineThree,lineFour;
    TextView textViewPrice, textViewTotalPrice, textPrice, textMethodPayment,remindDate,remindTime;;
    RelativeLayout btnPayment,blockPayment, blockIconOne, blockIconTwo,
            blockIconThree,blockIconFour,blockIconFive;
    OnlineDoctor doctor;
    User userChoose;
    SwitchCompat btnRemind;
    OnlineAppointment appointment;
    private int color,colorDefault;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        color = ContextCompat.getColor(this, R.color.white);
        colorDefault = ContextCompat.getColor(this, R.color.icon_notselect);
        if (getIntent() != null)getDataFromIntent();
        initView();
        setOnclick();
        setViewIcon();
        setupData();
    }

    private void setupData() {
        textViewPrice.setText(AndroidUtil.formatPrice(doctor.getPrice()));
        textViewTotalPrice.setText(AndroidUtil.formatPrice(doctor.getPrice()));
        textPrice.setText(AndroidUtil.formatPrice(doctor.getPrice()));
    }

    private void getDataFromIntent() {
        doctor = AndroidUtil.getOnlineDoctorFromIntent(getIntent());
        userChoose = AndroidUtil.getUserFromIntent(getIntent());
        appointment = AndroidUtil.getOnlineAppointmentFromIntent(getIntent());
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnPayment.setOnClickListener(this);
        blockPayment.setOnClickListener(this);
    }

    private void initView() {
        btnPayment = findViewById(R.id.btn_enter);
        btnBack = findViewById(R.id.back_btn);
        iconOne = findViewById(R.id.icon_one);
        iconTwo = findViewById(R.id.icon_two);
        iconThree = findViewById(R.id.icon_three);
        iconFour = findViewById(R.id.icon_four);
        iconFive= findViewById(R.id.icon_five);
        blockPayment = findViewById(R.id.block_payment);
        blockIconOne = findViewById(R.id.block_icon_one);
        blockIconTwo = findViewById(R.id.block_icon_two);
        blockIconThree= findViewById(R.id.block_icon_three);
        blockIconFour = findViewById(R.id.block_icon_four);
        blockIconFive = findViewById(R.id.block_icon_five);
        lineOne = findViewById(R.id.line_one);
        lineTwo = findViewById(R.id.line_two);
        lineThree = findViewById(R.id.line_three);
        lineFour = findViewById(R.id.line_four);
        textViewPrice = findViewById(R.id.appointment_price);
        textViewTotalPrice = findViewById(R.id.total_price);
        textPrice = findViewById(R.id.price);
        textMethodPayment = findViewById(R.id.method);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            finish();
        }
        if (v.getId() == R.id.block_payment){
            showMethodDialog();
        }
        if (v.getId() == R.id.btn_enter){
            if (textMethodPayment.getText().toString().length() == 0){
                CustomToast.showToast(this,"Chọn phương thức thanh toán",1000);
                return;
            }
            payment();
        }
    }

    private void payment() {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(2553, Environment.SANDBOX);
        String totalStr = String.format("%.0f", (double)doctor.getPrice());
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(totalStr);
            String code = data.getString("return_code");
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(Payment.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        getDataUser();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        CustomToast.showToast(getApplicationContext(),"Đã hủy thanh toán",1000);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        CustomToast.showToast(getApplicationContext(),"Thanh toán không thành công",1000);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataUser() {
        appointment.setDoctor(doctor);
        appointment.setStateAppointment(1);
        FirebaseUtil.getUserDetailsById(userChoose.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                appointment.setUser(user);
                appointment.setId(UUID.randomUUID().toString());
                saveOnlineAppointment(appointment);
            }
        });
    }

    private void saveOnlineAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointment.getId()).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showDialogNotice();
            }
        });
    }

    private void showDialogNotice() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_book_successful);
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
        RelativeLayout btnHome, btnDetail, change;

        change = dialog.findViewById(R.id.change);
        remindDate = dialog.findViewById(R.id.reminder_date);
        remindTime = dialog.findViewById(R.id.reminder_time);
        btnRemind = dialog.findViewById(R.id.notificationSwitch);
        btnHome = dialog.findViewById(R.id.btn_main);
        btnDetail = dialog.findViewById(R.id.btn_detail);

        remindDate.setText(appointment.getAppointmentDate());
        remindTime.setText("07:00");

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnRemind.isChecked()) {
                    try {
                        setupRemind(remindTime,remindDate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                dialog.dismiss();
                Intent intent = new Intent(Payment.this, Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //
            }
        });

        dialog.show();
    }

    private void setupRemind(TextView remindTime, TextView remindDate) throws ParseException {
        String timeStr = remindTime.getText().toString();
        String dateStr = remindDate.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        Date remindDateTime = dateFormat.parse(timeStr + " " + dateStr);

        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent(Payment.this, AlarmReceiver.class);
        intent.setAction("remind");
        intent.putExtra("appointmentTime", appointment.getTime());
        intent.putExtra("dateRemind",dateStr);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(Payment.this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        if (AndroidUtil.getCurrentDate().equals(dateStr)){
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
        else {
            assert remindDateTime != null;
            calendar.setTime(remindDateTime);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    private void showMethodDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_method);
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
        RelativeLayout btnZaloPay, btnMomo;
        ImageView btnClose;
        TextView textViewMethod;
        btnZaloPay = dialog.findViewById(R.id.btn_zalopay);
        btnMomo = dialog.findViewById(R.id.btn_momoPay);
        textViewMethod = dialog.findViewById(R.id.text_title);
        btnClose = dialog.findViewById(R.id.icon_close);

        btnZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textMethodPayment.setVisibility(View.VISIBLE);
                textMethodPayment.setText(textViewMethod.getText());
                dialog.dismiss();
            }
        });

        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CustomToast.showToast(getApplicationContext(),"Phương thức đang phát triển",1000);
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
    private void setViewIcon() {
        iconOne.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconOne.setBackgroundResource(R.drawable.bg_icon_selected);
        lineOne.setBackgroundTintList(ColorStateList.valueOf(color));

        iconTwo.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconTwo.setBackgroundResource(R.drawable.bg_icon_selected);
        lineTwo.setBackgroundTintList(ColorStateList.valueOf(color));

        iconThree.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconThree.setBackgroundResource(R.drawable.bg_icon_selected);
        lineThree.setBackgroundTintList(ColorStateList.valueOf(color));

        iconFour.setBackgroundTintList(ColorStateList.valueOf(color));
        blockIconFour.setBackgroundResource(R.drawable.bg_icon_selected);
        lineFour.setBackgroundTintList(ColorStateList.valueOf(colorDefault));

        iconFive.setBackgroundTintList(ColorStateList.valueOf(colorDefault));
        blockIconFive.setBackgroundResource(R.drawable.bg_icon_notselect);
    }
}
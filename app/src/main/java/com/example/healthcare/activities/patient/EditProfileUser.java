package com.example.healthcare.activities.patient;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.healthcare.R;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditProfileUser extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    ImageButton btnBack, btnEdit;
    ImageView imageAccount;
    EditText fullName, phoneNumber, address, height, weight;
    RadioButton male, female;
    TextView birth;
    CardView bottomLayout;
    RelativeLayout btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);
        initView();
        setOnlick();
        setupUserDetail();
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        btnEdit = findViewById(R.id.btn_edit);
        imageAccount = findViewById(R.id.image_account);
        fullName = findViewById(R.id.fullName_user);
        phoneNumber = findViewById(R.id.phoneNumber_user);
        address = findViewById(R.id.address_user);
        birth = findViewById(R.id.birth_user);
        height = findViewById(R.id.height_user);
        weight = findViewById(R.id.weight_user);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        bottomLayout = findViewById(R.id.bottom_Layout);
        btnSave = findViewById(R.id.enterBook);
    }

    private void setOnlick() {
        btnBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        birth.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    private void setupUserDetail() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User userModel = task.getResult().toObject(User.class);
                if (userModel != null) {
                    setupViewDetail(userModel);
                }
            } else {
                Log.e("ERROR", "Lỗi kết nối");
            }
        });
    }

    private void setupViewDetail(User userModel) {
        fullName.setText(userModel.getFullName());
        phoneNumber.setText(userModel.getPhoneNumber());
        address.setText(userModel.getAddress());
        birth.setText(userModel.getBirth());
        height.setText(String.valueOf(userModel.getHeight()));
        weight.setText(String.valueOf(userModel.getWeight()));
        if (userModel.getGender().equals("Nam")) {
            male.setChecked(true);
        }
        if (userModel.getGender().equals("Nữ")) {
            female.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn) {
            finish();
        }
        if (v.getId() == R.id.btn_edit) {
            setupView(true);
        }
        if (v.getId() == R.id.enterBook) {
            saveUserDetail();
            setupView(false);
        }
        if (v.getId() == R.id.birth_user) {
            showDialogCalendar(Gravity.CENTER);
        }
    }

    private void showDialogCalendar(int center) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_datepicker);
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
                getBirthDay(datePicker);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getBirthDay(DatePicker datePicker) {
        int dayOfMonth = datePicker.getDayOfMonth();
        int monthOfYear = datePicker.getMonth();
        int year = datePicker.getYear();
        String dayOfMonthStr = String.valueOf(dayOfMonth),
                monthOfYearStr = String.valueOf(monthOfYear + 1);
        if (dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonth;
        if (monthOfYear + 1 < 10) monthOfYearStr = "0" + (monthOfYear + 1);
        String selectedDate = dayOfMonthStr + "/" + monthOfYearStr + "/" + year;
        birth.setText(selectedDate);
    }

    private void setupView(boolean b) {
        fullName.setEnabled(b);
        phoneNumber.setEnabled(b);
        address.setEnabled(b);
        birth.setEnabled(b);
        height.setEnabled(b);
        weight.setEnabled(b);
        male.setEnabled(b);
        female.setEnabled(b);
        if (b) {
            bottomLayout.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        } else {
            bottomLayout.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    private void saveUserDetail() {
        String name = fullName.getText().toString();
        String gender = "";
        if (male.isChecked()) {
            gender = "Nam";
        } else {
            gender = "Nữ";
        }
        String phone = phoneNumber.getText().toString();
        String addressUser = address.getText().toString();
        String birthUser = birth.getText().toString();
        String heightStr = height.getText().toString();
        String weightStr = weight.getText().toString();

        if (name.isEmpty() || gender.isEmpty() || phone.isEmpty() ||
                addressUser.isEmpty() || birthUser.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            CustomToast.showToast(getApplicationContext(), "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT);
            return;
        }

        int userHeight = Integer.parseInt(heightStr);
        int userWeight = Integer.parseInt(weightStr);

        if (userHeight <= 0) {
            CustomToast.showToast(getApplicationContext(), "Chiều cao không hợp lệ", Toast.LENGTH_SHORT);
            return;
        }

        if (userWeight <= 0) {
            CustomToast.showToast(getApplicationContext(), "Cân nặng không hợp lệ", Toast.LENGTH_SHORT);
            return;
        }
        // Lưu dữ liệu
        String finalGender = gender;
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                if (user != null){
                    List<User> relativeList = user.getRelativeList();
                    editUserNew(name, finalGender, phone, addressUser, birthUser, userHeight,
                            userWeight,user,relativeList);
                }
                else {
                    User userNew = new User(name, finalGender, phone, addressUser, birthUser, userHeight,
                            userWeight,"",System.currentTimeMillis(),new ArrayList<>(),"Chủ tài khoản",FirebaseUtil.currentUserId());
                    FirebaseUtil.getUserDetailsById(FirebaseUtil.currentUserId()).set(userNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CustomToast.showToast(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT);
                                }
                            }, 1000);
                            finish();
                        }
                    });
                }
            }
        });

    }

    private void editUserNew(String name, String finalGender, String phone, String addressUser, String birthUser,
                             int userHeight, int userWeight,User user, List<User> relativeList) {
        User userNew = new User(name, finalGender, phone, addressUser, birthUser, userHeight,
                userWeight,"",System.currentTimeMillis(),relativeList,user.getRelative(),user.getUserId());

        FirebaseUtil.currentUserDetailsById().set(userNew).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.showToast(getApplicationContext(), "Lưu thành công", Toast.LENGTH_SHORT);
                    }
                }, 1000);
                finish();
            }
        });
    }
}
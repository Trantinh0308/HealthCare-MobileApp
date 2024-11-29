package com.example.healthcare.activities.patient;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.adapters.ScheduleItemAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.models.ScheduleDetail;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class DetailSchedule extends AppCompatActivity implements View.OnClickListener {
    private static final int MAX_IMAGE_SIZE = 65000;
    private static final int IMAGE_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    RecyclerView recyclerView;
    ProgressBar loadingSaveData;
    List<ScheduleDetail> scheduleDetailList;
    ScheduleItemAdapter scheduleItemAdapter;
    EditText editTextDrugName;
    TextView textStartDate, textEndDate,textViewFrequency,textDateSelected;
    ImageView drugImage, checkedOne, checkedTwo, checkedThree;
    RelativeLayout btnAddTime,blockStartDate,
            blockEndDate,blockFrequency,btnEnter, blockSelectDate,btnEnterDialog;
    Spinner spinnerDay;
    DatePicker datePickerFreQuency;
    ImageView btnBack;
    String frequency = "",imageCode = "",scheduleId;
    boolean checkSizeImage = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);
        initView();
        scheduleId = UUID.randomUUID().toString();
        frequency = textViewFrequency.getText().toString().trim();
        setCurrentDate();
        initScheduleItemAdapter();
        setOnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnEnter.setVisibility(View.VISIBLE);
        loadingSaveData.setVisibility(View.GONE);
    }

    private void setCurrentDate() {
        final Calendar calendar = Calendar.getInstance();
        int year, month, day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthStr = String.valueOf(month + 1),dayStr = String.valueOf(day);

        if (month+1 < 10) monthStr = "0" + monthStr;
        if (day < 10) dayStr = "0" + dayStr;
        String result = dayStr+"/"+monthStr+"/"+year;
        textStartDate.setText(result);
    }

    private void initScheduleItemAdapter() {
        scheduleDetailList = new ArrayList<>();
        scheduleItemAdapter = new ScheduleItemAdapter(scheduleDetailList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(scheduleItemAdapter);
    }

    private void setOnClick() {
        btnBack.setOnClickListener(this);
        drugImage.setOnClickListener(this);
        btnAddTime.setOnClickListener(this);
        blockStartDate.setOnClickListener(this);
        blockEndDate.setOnClickListener(this);
        blockFrequency.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        editTextDrugName = findViewById(R.id.name_drug);
        drugImage = findViewById(R.id.drug_image);
        btnAddTime = findViewById(R.id.top_block_time);
        blockStartDate = findViewById(R.id.form_startDate);
        blockEndDate = findViewById(R.id.form_endDate);
        blockFrequency = findViewById(R.id.block_frequency);
        btnEnter = findViewById(R.id.btn_enter);
        recyclerView = findViewById(R.id.list_time);
        textStartDate = findViewById(R.id.text_start_date);
        textEndDate = findViewById(R.id.text_end_date);
        textViewFrequency = findViewById(R.id.frequency);
        textDateSelected = findViewById(R.id.date_selected);
        loadingSaveData = findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            finish();
        }
        if (v.getId() == R.id.top_block_time){
            editTextDrugName.clearFocus();
            addScheduleItem();
        }
        if (v.getId() == R.id.form_startDate){
            showDialogDatePicker(textStartDate);
        }
        if (v.getId() == R.id.form_endDate){
            showDialogDatePicker(textEndDate);
        }
        if (v.getId() == R.id.block_frequency){
            showDialogFrequency(Gravity.BOTTOM);
        }
        if (v.getId() == R.id.drug_image){
            editTextDrugName.clearFocus();
            showDialogSelectMethod();
        }
        if (v.getId() == R.id.btn_enter){
            validScheduleDetail();
        }
    }

    private void validScheduleDetail() {
        String drugName = editTextDrugName.getText().toString().trim();
        String startDate = textStartDate.getText().toString();
        String endDate = textEndDate.getText().toString();
        String frequency = textViewFrequency.getText().toString();
        if (drugName.length() == 0){
            CustomToast.showToast(DetailSchedule.this,"Nhập tên thuốc",Toast.LENGTH_SHORT);
            return;
        }
        if (!checkSizeImage){
            CustomToast.showToast(DetailSchedule.this,"Dung lượng ảnh quá lớn, vui lòng chọn ảnh khác",2000);
            return;
        }
        if (scheduleDetailList == null || scheduleDetailList.size() == 0){
            CustomToast.showToast(DetailSchedule.this,"Thêm giờ hẹn",Toast.LENGTH_SHORT);
            return;
        }
        if (endDate.equals("--/--/--"))endDate = "";

        btnEnter.setVisibility(View.GONE);
        loadingSaveData.setVisibility(View.VISIBLE);
        saveListSchedule(scheduleDetailList,drugName,startDate,endDate,frequency);
    }

    private void saveListSchedule(List<ScheduleDetail> scheduleDetailList, String drugName, String startDate, String endDate, String frequency) {
        for(ScheduleDetail scheduleDetail : scheduleDetailList){
            saveScheduleItem(scheduleDetail,drugName,startDate,endDate,frequency);
        }
    }

    private void saveScheduleItem(ScheduleDetail scheduleDetail, String drugName, String startDate, String endDate, String frequency) {
        Schedule schedule = new Schedule();
        schedule.setSid(UUID.randomUUID().toString());
        schedule.setUserId(FirebaseUtil.currentUserId());
        schedule.setDrugName(drugName);
        schedule.setImage(imageCode);
        schedule.setTime(scheduleDetail.getTime());
        schedule.setNote(scheduleDetail.getNote());
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        if (textDateSelected.getText().toString().length() != 0){
            schedule.setFrequency(frequency+" "+ textDateSelected.getText().toString());
        }
        else schedule.setFrequency(frequency);
        saveDataBase(schedule);
    }

    private void saveDataBase(Schedule schedule) {
        FirebaseUtil.scheduleCollectionById(schedule.getSid()).set(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Timber.tag(TAG).d("onComplete: successful");
                Intent intent = new Intent(DetailSchedule.this, ScheduleActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showDialogSelectMethod() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_add_image);
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
        RelativeLayout btnLibrary, btnCamera, btnClose;
        btnLibrary = dialog.findViewById(R.id.btn_library);
        btnCamera = dialog.findViewById(R.id.btn_camera);
        btnClose = dialog.findViewById(R.id.btn_close);

        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkLibraryPermission();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkCameraPermission();
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

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void checkLibraryPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, IMAGE_PERMISSION_CODE);
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
        else{
            Toast.makeText(DetailSchedule.this,"Permission Denind",Toast.LENGTH_SHORT).show();
        }
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission Denied to Access Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if (requestCode == IMAGE_PERMISSION_CODE && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            if (uri != null) {
                drugImage.setImageURI(uri);
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            drugImage.setImageBitmap(bitmap);
        }
        if(bitmap != null){
            int imageSize = getImageSize(bitmap);
            if (imageSize > MAX_IMAGE_SIZE) {
                checkSizeImage = false;
                CustomToast.showToast(DetailSchedule.this,"Dung lượng ảnh quá lớn, vui lòng chọn ảnh khác",2000);
            } else {
                checkSizeImage = true;
                encodeImageByBase64(bitmap);
            }
        }
    }

    private int getImageSize(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageInBytes = baos.toByteArray();
        return imageInBytes.length;
    }

    private void encodeImageByBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageCode = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void showDialogFrequency(int bottom) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_frequency);
        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = bottom;
        window.setAttributes(windowAttributes);
        if (windowAttributes.gravity == bottom) {
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        } else {
            dialog.setCancelable(false);
        }
        ImageView btnRemove;
        RelativeLayout btnDaily, btnDateOfWeek, btnPermanent;
        btnRemove = dialog.findViewById(R.id.btn_remove);
        btnEnterDialog = dialog.findViewById(R.id.btn_enter);
        btnDaily = dialog.findViewById(R.id.block_one);
        btnDateOfWeek = dialog.findViewById(R.id.block_two);
        btnPermanent = dialog.findViewById(R.id.block_three);
        checkedOne = dialog.findViewById(R.id.check_one);
        checkedTwo = dialog.findViewById(R.id.checked_two);
        checkedThree = dialog.findViewById(R.id.check_three);
        blockSelectDate = dialog.findViewById(R.id.block_select_time);
        spinnerDay = dialog.findViewById(R.id.day);
        datePickerFreQuency = dialog.findViewById(R.id.datePicker_frequency);

        setChecked(textViewFrequency.getText().toString());
        setBackgroundDialogFrequency(textViewFrequency.getText().toString());
        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency = "Hằng ngày";
                setChecked(frequency);
                setBackgroundDialogFrequency(frequency);
                btnEnterDialog.setBackgroundResource(R.drawable.custom_button);
                btnEnterDialog.setEnabled(true);
            }
        });

        btnDateOfWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency = "Ngày cố định trong tuần";
                setChecked(frequency);
                setBackgroundDialogFrequency(frequency);
                setbtnEnter();
            }
        });

        btnPermanent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequency = "Ngày cố định";
                setChecked(frequency);
                setBackgroundDialogFrequency(frequency);
                btnEnterDialog.setBackgroundResource(R.drawable.custom_button);
                btnEnterDialog.setEnabled(true);
            }
        });

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Lựa chọn")){
                    btnEnterDialog.setBackgroundResource(R.drawable.custom_button_enable);
                    btnEnterDialog.setEnabled(false);
                }
                else {
                    btnEnterDialog.setBackgroundResource(R.drawable.custom_button);
                    btnEnterDialog.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnEnterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewFrequency.setText(frequency);
                getDetailFrequency(datePickerFreQuency);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setbtnEnter() {
        if (spinnerDay.getSelectedItem().toString().equals("Lựa chọn")){
            btnEnterDialog.setBackgroundResource(R.drawable.custom_button_enable);
            btnEnterDialog.setEnabled(false);
        }
        else {
            btnEnterDialog.setBackgroundResource(R.drawable.custom_button);
            btnEnterDialog.setEnabled(true);
        }
    }

    private void getDetailFrequency(DatePicker datePickerFreQuency) {
        if (frequency.equals("Hằng ngày")){
            textDateSelected.setText("");
        }
        else {
            if (frequency.equals("Ngày cố định trong tuần")){
                textDateSelected.setText(spinnerDay.getSelectedItem().toString());
            }
            else if (frequency.equals("Ngày cố định")){
                getDate(datePickerFreQuency,textDateSelected);
            }
        }
    }

    private void showDialogDatePicker(TextView textDate) {
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

        setMinDate(datePicker);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(datePicker,textDate);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setMinDate(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        long today = calendar.getTimeInMillis();
        datePicker.setMinDate(today);
    }

    private void getDate(DatePicker datePicker, TextView textDate) {
        int dayOfMonth = datePicker.getDayOfMonth();
        int monthOfYear = datePicker.getMonth();
        int year = datePicker.getYear();
        String dayOfMonthStr = String.valueOf(dayOfMonth),
                monthOfYearStr = String.valueOf(monthOfYear+1);
        if (dayOfMonth < 10) dayOfMonthStr = "0" + dayOfMonth;
        if (monthOfYear+1 < 10) monthOfYearStr = "0" + (monthOfYear+1);
        String selectedDate = dayOfMonthStr + "/" + monthOfYearStr + "/" + year;
        textDate.setText(selectedDate);
    }

    private void addScheduleItem() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_detail);
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
        ImageView btnRemove;
        RelativeLayout btnEnter;
        NumberPicker numberPickerHour,numberPickerMinute;
        EditText editTextNote;
        btnRemove = dialog.findViewById(R.id.btn_remove);
        btnEnter = dialog.findViewById(R.id.btn_enter);
        numberPickerHour = dialog.findViewById(R.id.hours);
        numberPickerMinute = dialog.findViewById(R.id.minute);
        editTextNote = dialog.findViewById(R.id.note);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(23);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);

        numberPickerHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        numberPickerMinute.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d",value);
            }
        });

        setCurrentTime(numberPickerHour,numberPickerMinute);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetail(numberPickerHour,numberPickerMinute,editTextNote);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setCurrentTime(NumberPicker hours, NumberPicker minute) {
        Calendar myCalender = Calendar.getInstance();
        int currentHour = myCalender.get(Calendar.HOUR_OF_DAY);
        int currentMinute = myCalender.get(Calendar.MINUTE);
        hours.setValue(currentHour);
        minute.setValue(currentMinute);
    }

    private void getDetail(NumberPicker numberPickerHour, NumberPicker numberPickerMinute,
                           EditText editTextNote) {
        @SuppressLint("DefaultLocale") String hours = String.format("%02d",numberPickerHour.getValue());
        @SuppressLint("DefaultLocale") String minute = String.format("%02d",numberPickerMinute.getValue());
        String time = hours + ":" + minute;
        String note = editTextNote.getText().toString();
        addListDetail(time,note);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addListDetail(String time, String note) {
        ScheduleDetail scheduleDetail = new ScheduleDetail();
        scheduleDetail.setTime(time);
        scheduleDetail.setNote(note);
        scheduleDetailList.add(scheduleDetail);
        scheduleItemAdapter.notifyDataSetChanged();
    }

    private void setChecked (String frequency){
        if (frequency.equals("Hằng ngày")){
            checkedOne.setVisibility(View.VISIBLE);
            checkedTwo.setVisibility(View.GONE);
            checkedThree.setVisibility(View.GONE);
        }
        else if (frequency.equals("Ngày cố định trong tuần")){
            checkedOne.setVisibility(View.GONE);
            checkedTwo.setVisibility(View.VISIBLE);
            checkedThree.setVisibility(View.GONE);
        }
        else {
            checkedOne.setVisibility(View.GONE);
            checkedTwo.setVisibility(View.GONE);
            checkedThree.setVisibility(View.VISIBLE);
        }
    }

    private void setBackgroundDialogFrequency(String frequency){
        if (frequency.equals("Hằng ngày")){
            blockSelectDate.setVisibility(View.GONE);
        }
        else if (frequency.equals("Ngày cố định trong tuần")){
            blockSelectDate.setVisibility(View.VISIBLE);
            spinnerDay.setVisibility(View.VISIBLE);
            datePickerFreQuency.setVisibility(View.GONE);
        }
        else {
            blockSelectDate.setVisibility(View.VISIBLE);
            spinnerDay.setVisibility(View.GONE);
            datePickerFreQuency.setVisibility(View.VISIBLE);
        }
    }
}
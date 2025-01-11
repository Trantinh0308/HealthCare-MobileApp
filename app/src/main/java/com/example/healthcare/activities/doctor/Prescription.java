package com.example.healthcare.activities.doctor;

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
import com.example.healthcare.activities.patient.DetailSchedule;
import com.example.healthcare.activities.patient.ScheduleActivity;
import com.example.healthcare.adapters.DateFrequencyAdapter;
import com.example.healthcare.adapters.ScheduleItemAdapter;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.OnlinePrescription;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.models.ScheduleDetail;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class Prescription extends AppCompatActivity implements View.OnClickListener {

    private static final int MAX_IMAGE_SIZE = 65000;
    private static final int IMAGE_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    RecyclerView recyclerView,recyclerViewDate;
    List<ScheduleDetail> scheduleDetailList;
    List<String> detailPrescriptions;
    List<Boolean> checkList;
    ScheduleItemAdapter scheduleItemAdapter;
    DateFrequencyAdapter dateFrequencyAdapter;
    EditText editTextDrugName, editTextNote, editTextOrder;
    TextView textStartDate, textEndDate,textViewFrequency,textDateSelected;
    ImageView drugImage, checkedOne, checkedTwo, checkedThree;
    RelativeLayout btnAddTime,blockStartDate, blockRemind,textAddRemind, textDeletedRemind,
            blockEndDate,blockFrequency,btnEnter, blockSelectDate,btnEnterDialog,btnRemind;
    DatePicker datePickerFreQuency;
    ImageView btnBack;
    Spinner spinnerTypeDrug;
    String frequency = "",imageCode = "", drugType = "";
    boolean checkSizeImage = true, hasRemind = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        checkList = new ArrayList<>();
        for (int i = 0 ; i < 7 ; i++){
            checkList.add(false);
        }
        initView();
        frequency = textViewFrequency.getText().toString().trim();
        setCurrentDate();
        initScheduleItemAdapter();
        setOnClick();
        getDrugType();
    }

    private void getDrugType() {
        spinnerTypeDrug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                drugType = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
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
        detailPrescriptions = new ArrayList<>();
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
        btnRemind.setOnClickListener(this);
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
        editTextNote = findViewById(R.id.note);
        editTextOrder = findViewById(R.id.order);
        spinnerTypeDrug = findViewById(R.id.type_drug);
        btnRemind = findViewById(R.id.btnRemind);
        blockRemind = findViewById(R.id.block_remind);
        textAddRemind = findViewById(R.id.text_add);
        textDeletedRemind = findViewById(R.id.text_deleted);
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
            frequency = textViewFrequency.getText().toString().trim();
            showDialogFrequency(Gravity.BOTTOM);
        }
        if (v.getId() == R.id.drug_image){
            editTextDrugName.clearFocus();
            showDialogSelectMethod();
        }
        if (v.getId() == R.id.btn_enter){
            validScheduleDetail();
        }
        if (v.getId() == R.id.btnRemind){
            if (!hasRemind){
                hasRemind = true;
            }
            else hasRemind = false;
            setUpViewRemind(hasRemind);
        }
    }

    private void validScheduleDetail() {
        String drugName = editTextDrugName.getText().toString().trim();
        String startDate = textStartDate.getText().toString();
        String endDate = textEndDate.getText().toString();
        String frequency = textViewFrequency.getText().toString();
        String note = editTextNote.getText().toString();
        int order;
        if (drugName.length() == 0){
            CustomToast.showToast(Prescription.this,"Nhập tên thuốc", Toast.LENGTH_SHORT);
            return;
        }
        if (!checkSizeImage){
            CustomToast.showToast(Prescription.this,"Dung lượng ảnh quá lớn, vui lòng chọn ảnh khác",2000);
            return;
        }
        if (drugType.equals("Lựa chọn")){
            CustomToast.showToast(Prescription.this,"Chọn đơn vị tính",2000);
            return;
        }
        try {
            order = Integer.parseInt(editTextOrder.getText().toString());
        }catch (NumberFormatException ignored){
            CustomToast.showToast(Prescription.this,"Số lượng không hợp lệ",2000);
            return;
        }
        if (note.isEmpty()){
            CustomToast.showToast(Prescription.this,"Thêm cách dùng",2000);
            return;
        }
        if (hasRemind){
            if (detailPrescriptions.isEmpty()){
                CustomToast.showToast(Prescription.this,"Thêm giờ uống",2000);
                return;
            }
        }

        if (endDate.equals("--/--/--"))endDate = "";

        if (textDateSelected.getText().toString().length() != 0){
            frequency = frequency+" "+ textDateSelected.getText().toString();
        }

        Intent intent = new Intent(this, AppointmentDetailResult.class);
        OnlinePrescription onlinePrescription = new OnlinePrescription(drugName,imageCode,drugType,order,startDate,endDate,frequency,note,hasRemind);
        AndroidUtil.passOnlinePrescriptionAsIntent(intent,onlinePrescription);
        if (hasRemind){
            intent.putStringArrayListExtra("detailPrescriptions", (ArrayList<String>) detailPrescriptions);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
            Toast.makeText(Prescription.this,"Permission Denind",Toast.LENGTH_SHORT).show();
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
                CustomToast.showToast(Prescription.this,"Dung lượng ảnh quá lớn, vui lòng chọn ảnh khác",2000);
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
        recyclerViewDate = dialog.findViewById(R.id.day);
        datePickerFreQuency = dialog.findViewById(R.id.datePicker_frequency);

        setupDateFrequency();
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
                setBtnEnter();
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

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnEnterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!frequency.equals("Ngày cố định trong tuần"))resetCheckList();
                textViewFrequency.setText(frequency);
                getDetailFrequency(datePickerFreQuency);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void resetCheckList() {
        for (int i = 0 ; i < 7 ; i++){
            checkList.set(i,false);
        }
    }

    private void setupDateFrequency() {
        dateFrequencyAdapter  = new DateFrequencyAdapter(checkList,new DateFrequencyAdapter.IDateViewHolder() {
            @Override
            public void onClickItem(int position) {
                if (dateFrequencyAdapter.checkChoose()){
                    btnEnterDialog.setBackgroundResource(R.drawable.custom_button);
                    btnEnterDialog.setEnabled(true);
                }
                else {
                    btnEnterDialog.setBackgroundResource(R.drawable.custom_button_enable);
                    btnEnterDialog.setEnabled(false);
                }
            }
        });
        for (int i = 0 ; i < checkList.size() ; i ++){
            if (checkList.get(i))dateFrequencyAdapter.notifyItemChanged(i);
        }
        recyclerViewDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewDate.setAdapter(dateFrequencyAdapter);
    }

    private void setBtnEnter() {
        if (!dateFrequencyAdapter.checkChoose()){
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
                textDateSelected.setText(dateFrequencyAdapter.getDateChoose());
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
        detailPrescriptions.add(time+";"+note);
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
            recyclerViewDate.setVisibility(View.VISIBLE);
            datePickerFreQuency.setVisibility(View.GONE);
        }
        else {
            blockSelectDate.setVisibility(View.VISIBLE);
            recyclerViewDate.setVisibility(View.GONE);
            datePickerFreQuency.setVisibility(View.VISIBLE);
        }
    }
    private void setUpViewRemind(Boolean remind){
        if (remind){
            textAddRemind.setVisibility(View.GONE);
            textDeletedRemind.setVisibility(View.VISIBLE);
            blockRemind.setVisibility(View.VISIBLE);
        }
        else {
            textAddRemind.setVisibility(View.VISIBLE);
            textDeletedRemind.setVisibility(View.GONE);
            blockRemind.setVisibility(View.GONE);
        }
    }
}
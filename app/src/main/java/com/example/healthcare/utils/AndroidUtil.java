package com.example.healthcare.utils;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.example.healthcare.models.Doctor;
import com.example.healthcare.models.Schedule;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidUtil {
    public static void passScheduleAsIntent(Intent intent, Schedule schedule){
        intent.putExtra("sid",schedule.getSid());
        intent.putExtra("drugName",schedule.getDrugName());
        intent.putExtra("image",schedule.getImage());
        intent.putExtra("startDate",schedule.getStartDate());
        intent.putExtra("endDate",schedule.getEndDate());
        intent.putExtra("frequency",schedule.getFrequency());
        intent.putExtra("time",schedule.getTime());
        intent.putExtra("note",schedule.getNote());
        intent.putExtra("userId",schedule.getUserId());
    }

    public static Schedule getScheduleFromIntent(Intent intent){
        Schedule schedule = new Schedule();
        schedule.setSid(intent.getStringExtra("sid"));
        schedule.setDrugName(intent.getStringExtra("drugName"));
        schedule.setImage(intent.getStringExtra("image"));
        schedule.setStartDate(intent.getStringExtra("startDate"));
        schedule.setEndDate(intent.getStringExtra("endDate"));
        schedule.setFrequency(intent.getStringExtra("frequency"));
        schedule.setTime(intent.getStringExtra("time"));
        schedule.setNote(intent.getStringExtra("note"));
        schedule.setUserId(intent.getStringExtra("userId"));

        return schedule;
    }

    public static void passDoctorAsIntent(Intent intent, Doctor model){
        intent.putExtra("doctorId",model.getDoctorId());
        intent.putExtra("fullName",model.getFullName());
        intent.putExtra("gender",model.getGender());
        intent.putExtra("phoneNumber",model.getPhoneNumber());
        intent.putExtra("degree",model.getDegree());
        intent.putExtra("specialist",model.getSpecialist());
        intent.putExtra("experience",model.getExperience());
        intent.putExtra("achievement",model.getAchievement());
        intent.putExtra("workPlace",model.getWorkPlace());
        intent.putExtra("biography",model.getBiography());
        intent.putExtra("price",model.getPrice());
        intent.putExtra("appointment",model.getAppointment());
        intent.putExtra("image",model.getImageCode());
    }

    public static Doctor getDoctorFromIntent(Intent intent){
        Doctor doctor = new Doctor();
        doctor.setDoctorId(intent.getStringExtra("doctorId"));
        doctor.setFullName(intent.getStringExtra("fullName"));
        doctor.setGender(intent.getStringExtra("gender"));
        doctor.setPhoneNumber(intent.getStringExtra("phoneNumber"));
        doctor.setDegree(intent.getStringExtra("degree"));
        doctor.setSpecialist(intent.getStringExtra("specialist"));
        doctor.setExperience(intent.getStringExtra("experience"));
        doctor.setAchievement(intent.getStringExtra("achievement"));
        doctor.setWorkPlace(intent.getStringExtra("workPlace"));
        doctor.setBiography(intent.getStringExtra("biography"));
        doctor.setPrice(intent.getIntExtra("price",100000));
        doctor.setImageCode(intent.getStringExtra("image"));
        doctor.setAppointment(intent.getIntExtra("appointment",0));
        return doctor;
    }

    public static String formatPrice(int price){
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(price);
    }
    public static String currentTime (){
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        String hourStr = String.valueOf(currentHour);
        String minuteStr = String.valueOf(currentMinute);
        if (currentHour < 10)hourStr = "0"+currentHour;
        if (currentMinute < 10)minuteStr = "0"+currentMinute;
        return hourStr+":"+minuteStr;
    }
    public static boolean isDate2GreaterOrEqual(String date1, String date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parsedDate1 = dateFormat.parse(date1);
            Date parsedDate2 = dateFormat.parse(date2);

            assert parsedDate2 != null;
            return !parsedDate2.before(parsedDate1);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String formatDate(String input) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> numbersList = new ArrayList<>();
        while (matcher.find()) {
            numbersList.add(matcher.group());
        }

        String day = null, month = null;
        if (numbersList.size() == 2) {
            day = numbersList.get(0);
            month = numbersList.get(1);
        } else if (numbersList.size() == 3) {
            day = numbersList.get(1);
            month = numbersList.get(2);
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return day + "/" + month + "/" + currentYear;
    }

    public static boolean isEarlier(String time1, String time2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
        try {
            Date date1 = format.parse(time1);
            Date date2 = format.parse(time2);

            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getCurrentTimeDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
        Date currentTime = new Date();
        return format.format(currentTime);
    }
    public static String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return format.format(currentDate);
    }
    public static String[] addOneHour(String dateTimeInput) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
        try {
            Date dateTime = format.parse(dateTimeInput);

            Calendar calendar = Calendar.getInstance();
            assert dateTime != null;
            calendar.setTime(dateTime);
            calendar.add(Calendar.HOUR_OF_DAY, 1);

            @SuppressLint("SimpleDateFormat") String newTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());
            @SuppressLint("SimpleDateFormat") String newDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());

            return new String[]{newTime, newDate};
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean isDate2Greater(String date1, String date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parsedDate1 = dateFormat.parse(date1);
            Date parsedDate2 = dateFormat.parse(date2);

            // Kiểm tra nếu parsedDate2 lớn hơn parsedDate1
            return parsedDate2.after(parsedDate1);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isTime2Greater(String time1, String time2) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        try {
            Date parsedTime1 = timeFormat.parse(time1);
            Date parsedTime2 = timeFormat.parse(time2);

            return parsedTime2.after(parsedTime1);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}

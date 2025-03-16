package com.example.healthcare.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlinePrescription;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.models.User;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
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

    public static void passOnlineDoctorAsIntent(Intent intent, OnlineDoctor model){
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
        intent.putExtra("room",model.getRoom());
        intent.putExtra("appointment",model.getAppointment());
        intent.putExtra("image",model.getImageCode());
    }

    public static OnlineDoctor getOnlineDoctorFromIntent(Intent intent){
        OnlineDoctor doctor = new OnlineDoctor();
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
        doctor.setRoom(intent.getStringExtra("room"));
        doctor.setImageCode(intent.getStringExtra("image"));
        doctor.setAppointment(intent.getIntExtra("appointment",0));
        return doctor;
    }

    public static void passUserAsIntent(Intent intent, User model){
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fullNameUser",model.getFullName());
        intent.putExtra("genderUser",model.getGender());
        intent.putExtra("height",model.getHeight());
        intent.putExtra("weight",model.getWeight());
        intent.putExtra("phoneNumberUser",model.getPhoneNumber());
        intent.putExtra("addressUser",model.getAddress());
        intent.putExtra("birthUser",model.getBirth());
        intent.putExtra("relative",model.getRelative());
    }
    public static User getUserFromIntent(Intent intent){
        User user = new User();
        user.setUserId(intent.getStringExtra("userId"));
        user.setFullName(intent.getStringExtra("fullNameUser"));
        user.setHeight(intent.getIntExtra("height",165));
        user.setWeight(intent.getIntExtra("weight",165));
        user.setGender(intent.getStringExtra("genderUser"));
        user.setPhoneNumber(intent.getStringExtra("phoneNumberUser"));
        user.setAddress(intent.getStringExtra("addressUser"));
        user.setBirth(intent.getStringExtra("birthUser"));
        user.setRelative(intent.getStringExtra("relative"));
        return user;
    }
    public static void passOnlineAppointmentAsIntent(Intent intent, OnlineAppointment model){
        intent.putExtra("bookDate",model.getBookDate());
        intent.putExtra("appointmentDate",model.getAppointmentDate());
        intent.putExtra("time",model.getTime());
        intent.putExtra("symptom",model.getSymptom());
        intent.putExtra("stateAppointment",model.getStateAppointment());
    }
    public static OnlineAppointment getOnlineAppointmentFromIntent(Intent intent){
        OnlineAppointment onlineAppointment = new OnlineAppointment();
        onlineAppointment.setBookDate(intent.getStringExtra("bookDate"));
        onlineAppointment.setAppointmentDate(intent.getStringExtra("appointmentDate"));
        onlineAppointment.setTime(intent.getStringExtra("time"));
        onlineAppointment.setSymptom(intent.getStringExtra("symptom"));
        onlineAppointment.setStateAppointment(intent.getIntExtra("stateAppointment",0));
        return onlineAppointment;
    }
    public static void passOnlinePrescriptionAsIntent(Intent intent, OnlinePrescription model){
        intent.putExtra("drugName",model.getDrugName());
        intent.putExtra("image",model.getImage());
        intent.putExtra("unit",model.getUnit());
        intent.putExtra("order",model.getOrder());
        intent.putExtra("startDate",model.getStartDate());
        intent.putExtra("endDate",model.getEndDate());
        intent.putExtra("frequency",model.getFrequency());
        intent.putExtra("note",model.getNote());
        intent.putExtra("remind",model.getRemind());
    }
    public static OnlinePrescription getOnlinePrescriptionFromIntent(Intent intent){
        OnlinePrescription onlinePrescription = new OnlinePrescription();
        onlinePrescription.setDrugName(intent.getStringExtra("drugName"));
        onlinePrescription.setImage(intent.getStringExtra("image"));
        onlinePrescription.setUnit(intent.getStringExtra("unit"));
        onlinePrescription.setOrder(intent.getIntExtra("order",1));
        onlinePrescription.setStartDate(intent.getStringExtra("startDate"));
        onlinePrescription.setEndDate(intent.getStringExtra("endDate"));
        onlinePrescription.setFrequency(intent.getStringExtra("frequency"));
        onlinePrescription.setNote(intent.getStringExtra("note"));
        onlinePrescription.setRemind(intent.getBooleanExtra("remind",false));
        return onlinePrescription;
    }
    public static String formatPrice(int price){
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(price)+" đ";
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
    public static int getNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
    public static float getFloatRange(float min, float max) {
        Random random = new Random();
        float randomValue = min + (max - min) * random.nextFloat();
        return (float) (Math.round(randomValue * 10.0) / 10.0);
    }
    public static long[] getDayBoundaries() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfDayMillis = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endOfDayMillis = calendar.getTimeInMillis();

        return new long[] { startOfDayMillis, endOfDayMillis };
    }

    public static long[] getWeekBoundaries() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfWeekMillis = calendar.getTimeInMillis();

        calendar.add(Calendar.DATE, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endOfWeekMillis = calendar.getTimeInMillis();

        return new long[] { startOfWeekMillis, endOfWeekMillis };
    }

    public static long[] getMonthBoundaries() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfMonthMillis = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endOfMonthMillis = calendar.getTimeInMillis();

        return new long[] { startOfMonthMillis, endOfMonthMillis };
    }

    public static long[] getYearBoundaries() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startOfYearMillis = calendar.getTimeInMillis();

        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endOfYearMillis = calendar.getTimeInMillis();

        return new long[] { startOfYearMillis, endOfYearMillis };
    }
    public static long[] getTimestampListByPosition(int position){
        if (position == 0){
            return AndroidUtil.getDayBoundaries();
        }
        else if (position == 1){
            return AndroidUtil.getWeekBoundaries();
        }
        else if (position == 2){
            return AndroidUtil.getMonthBoundaries();
        }
        else return AndroidUtil.getYearBoundaries();
    }

    public static long calculateTimestampByCurrentPosition(long currentTimestamp,int position, int number){
        long timestamp24h = 86399999;
        if (position == 0){
            if (number == -1) return (currentTimestamp - timestamp24h);
            else return (currentTimestamp + timestamp24h);
        }
        else if (position == 1){
            if (number == -1) return (currentTimestamp - timestamp24h*7);
            else return (currentTimestamp + timestamp24h*7);
        }
        else if (position == 2){
            if (number == -1) return (currentTimestamp - timestamp24h*30);
            else return (currentTimestamp + timestamp24h*30);
        }
        else {
            if (number == -1) return (currentTimestamp - timestamp24h*365);
            else return (currentTimestamp + timestamp24h*365);
        }
    }

    public static String getTimeFrame(int position) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE, dd MMMM");
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM/yyyy");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        switch (position) {
            case 0:
                return dayFormat.format(today);

            case 1:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                Date startOfWeek = calendar.getTime();
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                Date endOfWeek = calendar.getTime();

                SimpleDateFormat weekFormat = new SimpleDateFormat("dd/MM");
                return "Ngày " + weekFormat.format(startOfWeek) + " - Ngày " + weekFormat.format(endOfWeek);

            case 2:
                String monthYearResult = monthYearFormat.format(today);
                return monthYearResult.substring(0, 1).toUpperCase() + monthYearResult.substring(1);

            case 3:
                return "Năm " + yearFormat.format(today);

            default:
                return "";
        }
    }
    public static void sharedPreferences (Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void removeSharedPreferences (Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
    public static String getSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static String normalizeString(String input) {
        String[] parts = input.split("\\s+");
        StringBuilder normalizedString = new StringBuilder();

        for (String word : parts) {
            if (!word.matches(".*[0-9%].*")) {
                String normalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                normalizedString.append(normalizedWord).append(" ");
            }
        }
        return normalizedString.toString().trim();
    }

    public static List<String> getDaysAndWeekdays(int month, int year) {
        List<String> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            if (date.isBefore(today)) {
                continue;
            }

            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayName = getDayName(dayOfWeek);

            String formattedDay = (day < 10) ? "0" + day : String.valueOf(day);
            if (!dayName.contains("CN")){
                result.add("Thứ " + dayName + "," + formattedDay );
            }
            else result.add("CN" + "," + formattedDay);
        }

        return result;
    }

    private static String getDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "2";
            case TUESDAY:
                return "3";
            case WEDNESDAY:
                return "4";
            case THURSDAY:
                return "5";
            case FRIDAY:
                return "6";
            case SATURDAY:
                return "7";
            case SUNDAY:
                return "CN";
            default:
                return "";
        }
    }
}

package com.example.healthcare.adapters;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.Repeat;
import com.example.healthcare.models.Schedule;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleDetailDrinkAdapter extends RecyclerView.Adapter<ScheduleDetailDrinkAdapter.ScheduleDetailDrinkViewHolder>{
    List<Schedule> scheduleList;
    IScheduleItemViewHolder iScheduleItemViewHolder;

    public ScheduleDetailDrinkAdapter(List<Schedule> scheduleList, IScheduleItemViewHolder iScheduleItemViewHolder) {
        this.scheduleList = scheduleList;
        this.iScheduleItemViewHolder = iScheduleItemViewHolder;
    }

    public void setiScheduleItemViewHolder(IScheduleItemViewHolder iScheduleItemViewHolder) {
        this.iScheduleItemViewHolder = iScheduleItemViewHolder;
    }

    @NonNull
    @Override
    public ScheduleDetailDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_detail_item, parent, false);
        return new ScheduleDetailDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleDetailDrinkViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Schedule schedule = scheduleList.get(position);
        setVisibility(holder,schedule);
        holder.textTime.setText(schedule.getTime());
        holder.textName.setText(schedule.getDrugName());
        holder.textNote.setText(schedule.getNote());
        String imageCode = schedule.getImage();
        deCodeImage(holder,imageCode);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iScheduleItemViewHolder.onClickItem(position);
            }
        });
        holder.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iScheduleItemViewHolder.onClickEnter(position);
            }
        });

        holder.btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(holder);
                iScheduleItemViewHolder.onClickRemind(position);
            }
        });
    }

    private void setView(ScheduleDetailDrinkViewHolder holder) {
        holder.btnRemind.setVisibility(View.GONE);
        holder.titleRemind.setVisibility(View.VISIBLE);
        String [] arrStr = addOneHour(getCurrentTimeDate());
        holder.titleRemind.setText("Nhắc lại vào "+arrStr[0]);
    }

    private void setVisibility(ScheduleDetailDrinkViewHolder holder, Schedule schedule) {
        FirebaseUtil.repeatCollectionById(schedule.getSid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Repeat repeat = task.getResult().toObject(Repeat.class);
                    if (repeat != null && isEarlier(getCurrentTimeDate(),repeat.getTime()+"/"+repeat.getDate())
                            && getCurrentDate().equals(repeat.getDate())){
                        holder.titleRemind.setText("Nhắc lại vào "+repeat.getTime());
                        holder.btnRemind.setVisibility(View.GONE);
                    }
                    else {
                        holder.titleRemind.setVisibility(View.GONE);
                        holder.btnRemind.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d("TAG", "ERROR");
                }
            }
        });
    }

    private void deCodeImage(ScheduleDetailDrinkViewHolder holder, String imagecode) {
        if (imagecode != null && !imagecode.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imagecode, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.image.setImageResource(R.drawable.drugs);
            }
        } else {
            holder.image.setImageResource(R.drawable.drugs);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleDetailDrinkViewHolder extends RecyclerView.ViewHolder {
        TextView textTime,textName, textNote, titleRemind;
        ImageView image;
        RelativeLayout btnRemind, btnEnter;
        public ScheduleDetailDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.time);
            textName = itemView.findViewById(R.id.drug_name);
            textNote = itemView.findViewById(R.id.note);
            btnRemind = itemView.findViewById(R.id.btn_remind);
            btnEnter = itemView.findViewById(R.id.btnEnter);
            image = itemView.findViewById(R.id.image);
            titleRemind = itemView.findViewById(R.id.title_remind);
        }
    }
    public interface IScheduleItemViewHolder {
        void onClickItem(int position);
        void onClickEnter(int position);
        void onClickRemind(int position);
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
    private String getCurrentTimeDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm/dd/MM/yyyy");
        Date currentTime = new Date();
        return format.format(currentTime);
    }
    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        return format.format(currentDate);
    }
    private String[] addOneHour(String dateTimeInput) {
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
}

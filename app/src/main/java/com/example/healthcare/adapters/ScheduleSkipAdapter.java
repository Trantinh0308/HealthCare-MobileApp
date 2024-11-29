package com.example.healthcare.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.Schedule;

import java.util.List;

public class ScheduleSkipAdapter extends RecyclerView.Adapter<ScheduleSkipAdapter.ScheduleSkipViewHolder>{
    List<Schedule> scheduleSkipList;

    public ScheduleSkipAdapter(List<Schedule> scheduleSkipList) {
        this.scheduleSkipList = scheduleSkipList;
    }

    @NonNull
    @Override
    public ScheduleSkipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_skip, parent, false);
        return new ScheduleSkipAdapter.ScheduleSkipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleSkipViewHolder holder, int position) {
        Schedule schedule = scheduleSkipList.get(position);
        holder.textTime.setText(schedule.getTime());
        holder.textName.setText(schedule.getDrugName());
        holder.textNote.setText(schedule.getNote());
        String imageCode = schedule.getImage();
        deCodeImage(holder,imageCode);
    }

    private void deCodeImage(ScheduleSkipAdapter.ScheduleSkipViewHolder holder, String imageCode) {
        if (imageCode != null && !imageCode.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageCode, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageDrug.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.imageDrug.setImageResource(R.drawable.drugs);
            }
        } else {
            holder.imageDrug.setImageResource(R.drawable.drugs);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleSkipList.size();
    }

    public static class ScheduleSkipViewHolder extends RecyclerView.ViewHolder {
        TextView textTime,textName, textNote;
        ImageView imageDrug, iconSkip;

        public ScheduleSkipViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.time);
            textName = itemView.findViewById(R.id.drug_name);
            textNote = itemView.findViewById(R.id.note);
            imageDrug = itemView.findViewById(R.id.drug_image);
            iconSkip = itemView.findViewById(R.id.icon_skip);
        }
    }
}

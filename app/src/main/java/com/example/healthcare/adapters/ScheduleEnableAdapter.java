package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
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

public class ScheduleEnableAdapter extends RecyclerView.Adapter<ScheduleEnableAdapter.ScheduleEnableViewHolder>{
    List<Schedule> scheduleList;
    IScheduleEnableItemViewHolder iScheduleItemViewHolder;
    public ScheduleEnableAdapter(List<Schedule> scheduleList, IScheduleEnableItemViewHolder iScheduleItemViewHolder) {
        this.scheduleList = scheduleList;
        this.iScheduleItemViewHolder = iScheduleItemViewHolder;
    }

    public void setiScheduleItemViewHolder(IScheduleEnableItemViewHolder iScheduleItemViewHolder) {
        this.iScheduleItemViewHolder = iScheduleItemViewHolder;
    }

    @NonNull
    @Override
    public ScheduleEnableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_enable_item, parent, false);
        return new ScheduleEnableAdapter.ScheduleEnableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleEnableViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Schedule schedule = scheduleList.get(position);
        holder.textTime.setText(schedule.getTime());
        holder.textName.setText(schedule.getDrugName());
        holder.textNote.setText(schedule.getNote());
        String imagecode = schedule.getImage();
        deCodeImage(holder,imagecode);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iScheduleItemViewHolder.onClickItem(position);
            }
        });
    }
    private void deCodeImage(ScheduleEnableViewHolder holder, String imagecode) {
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

    public static class ScheduleEnableViewHolder extends RecyclerView.ViewHolder{
        TextView textTime,textName, textNote, textRemind;
        ImageView image;
        public ScheduleEnableViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.time);
            textName = itemView.findViewById(R.id.drug_name);
            textNote = itemView.findViewById(R.id.note);
            textRemind = itemView.findViewById(R.id.text_remind);
            image = itemView.findViewById(R.id.image);
        }
    }
    public interface IScheduleEnableItemViewHolder {
        void onClickItem(int position);
    }
}

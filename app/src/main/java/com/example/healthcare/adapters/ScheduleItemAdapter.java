package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.ScheduleDetail;

import java.util.List;

public class ScheduleItemAdapter extends RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder>{
    private List<ScheduleDetail> scheduleDetailList;

    public ScheduleItemAdapter(List<ScheduleDetail> scheduleDetailList) {
        this.scheduleDetailList = scheduleDetailList;
    }

    @NonNull
    @Override
    public ScheduleItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item,parent,false);
        return new ScheduleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ScheduleDetail detail = scheduleDetailList.get(position);
        holder.time.setText(detail.getTime());
        holder.note.setText(detail.getNote());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleDetailList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, scheduleDetailList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleDetailList.size();
    }

    public static class ScheduleItemViewHolder extends RecyclerView.ViewHolder{
        TextView time,note;
        RelativeLayout btnRemove;
        public ScheduleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            note = itemView.findViewById(R.id.note);
            btnRemove = itemView.findViewById(R.id.btn_delete);
        }
    }
}

package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.List;

public class TimeOnlineAdapter extends RecyclerView.Adapter<TimeOnlineAdapter.TimeViewHolder>{
    private List<String> timeList;
    Context context;
    public int selectedPosition = -1;
    private ITimeViewHolder iTimeViewHolder;

    public TimeOnlineAdapter(Context context, List<String> timeList, ITimeViewHolder iDateHolder) {
        this.timeList = timeList;
        this.iTimeViewHolder = iDateHolder;
        this.context = context;
    }

    public void setiTimeViewHolder(ITimeViewHolder iTimeViewHolder) {
        this.iTimeViewHolder = iTimeViewHolder;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new TimeOnlineAdapter.TimeViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String time = timeList.get(position);
        holder.textTime.setText(time);
        holder.bind(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            iTimeViewHolder.onClickItem(position);
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder{
        Context context;
        TextView textTime;
        public TimeViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            textTime = itemView.findViewById(R.id.time);
        }

        public void bind(boolean isSelected) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.custom_calendar_choosed);
                textTime.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.custom_calendar_default);
                textTime.setTextColor(ContextCompat.getColor(context, R.color.text_Color));
            }
        }
    }
    public interface ITimeViewHolder {
        void onClickItem(int position);

    }
}

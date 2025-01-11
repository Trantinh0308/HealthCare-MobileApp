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
import com.example.healthcare.utils.AndroidUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateOnlineAdapter extends RecyclerView.Adapter<DateOnlineAdapter.DateItemViewHolder>{
    private List<String> weekDays;
    public int selectedPosition = -1;
    private IDateViewHolder iDateHolder;

    public DateOnlineAdapter(List<String> weekDays, IDateViewHolder iDateHolder) {
        this.weekDays = weekDays;
        this.iDateHolder = iDateHolder;
    }

    @NonNull
    @Override
    public DateOnlineAdapter.DateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_top_item, parent, false);
        return new DateOnlineAdapter.DateItemViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull DateOnlineAdapter.DateItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = weekDays.get(position);
        holder.bind(date, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            iDateHolder.onClickItem(position);
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return weekDays.size();
    }

    public static class DateItemViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView textDate, textDateMonth;

        public DateItemViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            textDate = itemView.findViewById(R.id.date);
            textDateMonth = itemView.findViewById(R.id.dateWeek);
        }

        public void bind(String date, boolean isSelected) {
            String[] parts = date.split(",");
            if (parts.length == 2) {
                textDate.setText(parts[0].trim());
                textDateMonth.setText(parts[1].trim());
            }
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.custom_calendar_choosed);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.white));
                textDateMonth.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.custom_calendar_default);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.text_Color));
                textDateMonth.setTextColor(ContextCompat.getColor(context, R.color.text_Color));
            }
        }
    }
    public interface IDateViewHolder {
        void onClickItem(int position);
    }
}

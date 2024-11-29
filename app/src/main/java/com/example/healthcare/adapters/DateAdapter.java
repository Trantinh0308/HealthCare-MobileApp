package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateItemViewHolder> {
    private List<String> weekDays;
    public int selectedPosition = -1;
    private IDateViewHolder iDateHolder;

    public DateAdapter(List<String> weekDays, IDateViewHolder iDateHolder) {
        this.weekDays = weekDays;
        this.iDateHolder = iDateHolder;
    }

    @NonNull
    @Override
    public DateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateItemViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull DateItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
            textDate = itemView.findViewById(R.id.day);
            textDateMonth = itemView.findViewById(R.id.day_month);
        }

        public void bind(String date, boolean isSelected) {
            String[] parts = date.split(",");
            if (parts.length == 2) {
                String dateStr = parts[1].trim();

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                try {
                    Date parsedDate = inputFormat.parse(dateStr);
                    String formattedDate = outputFormat.format(parsedDate);
                    textDateMonth.setText(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textDate.setText(parts[0].trim());
            }
            // Đặt nền cho item
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.custom_form_date);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.white));
                textDateMonth.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.custom_date_not_selected);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.gray));
                textDateMonth.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        }
    }
    public interface IDateViewHolder {
        void onClickItem(int position);
    }
}

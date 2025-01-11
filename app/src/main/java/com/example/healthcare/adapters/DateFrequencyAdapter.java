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

import java.util.ArrayList;
import java.util.List;

public class DateFrequencyAdapter extends RecyclerView.Adapter<DateFrequencyAdapter.DateItemViewHolder>{
    private List<String> numberStr;
    private List<Boolean> chooseList;
    public int selectedPosition = -1;
    private IDateViewHolder iDateHolder;

    public DateFrequencyAdapter(List<Boolean> chooseList, IDateViewHolder iDateHolder) {
        List<String> numberStr = new ArrayList<>();
        for (int i = 2 ; i < 8 ; i++){
            numberStr.add(String.valueOf(i));
        }
        numberStr.add("CN");

        this.numberStr = numberStr;
        this.chooseList = chooseList;
        this.iDateHolder = iDateHolder;
    }

    @NonNull
    @Override
    public DateFrequencyAdapter.DateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_frequency_item, parent, false);
        return new DateFrequencyAdapter.DateItemViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull DateFrequencyAdapter.DateItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = numberStr.get(position);
        holder.textNumber.setText(date);
        holder.bind(chooseList,position);

        holder.itemView.setOnClickListener(v -> {
            if (chooseList.get(position))chooseList.set(position,false);
            else chooseList.set(position,true);

            iDateHolder.onClickItem(position);
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return numberStr.size();
    }

    public String getDateChoose() {
        StringBuilder selectedItems = new StringBuilder();

        for (int i = 0; i < chooseList.size(); i++) {
            if (chooseList.get(i)) {
                if (selectedItems.length() > 0) {
                    selectedItems.append(",");
                }
                selectedItems.append("thứ "+numberStr.get(i));
            }
        }

        return selectedItems.toString();
    }

    public Boolean checkChoose(){
        return chooseList.contains(true);
    }

    public static class DateItemViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView textNumber, textDate;

        public DateItemViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            textDate = itemView.findViewById(R.id.date);
            textNumber = itemView.findViewById(R.id.number);
        }

        public void bind(List<Boolean> chooseList, int position) {
            if (chooseList.get(position)) {
                itemView.setBackgroundResource(R.drawable.custom_calendar_choosed);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.white));
                textNumber.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.custom_calendar_default);
                textDate.setTextColor(ContextCompat.getColor(context, R.color.text_Color));
                textNumber.setTextColor(ContextCompat.getColor(context, R.color.text_Color));
            }
        }
    }
    public interface IDateViewHolder {
        void onClickItem(int position);
    }
}

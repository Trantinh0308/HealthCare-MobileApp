package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.List;

public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder>{
    private List<String> specialists;
    private ISpecialistViewHolder iSpecialistViewHolder;

    public SpecialistAdapter(List<String> specialists, ISpecialistViewHolder iSpecialistViewHolder) {
        this.specialists = specialists;
        this.iSpecialistViewHolder = iSpecialistViewHolder;
    }

    public void setiSpecialistViewHolder(ISpecialistViewHolder iSpecialistViewHolder) {
        this.iSpecialistViewHolder = iSpecialistViewHolder;
    }

    @NonNull
    @Override
    public SpecialistAdapter.SpecialistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.specialist_item, parent, false);
        return new SpecialistAdapter.SpecialistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialistAdapter.SpecialistViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = specialists.get(position);
        if (position != 0){
            holder.textName.setText("Khoa "+name);
            holder.imageViewIcon.setImageResource(R.drawable.medical2);
        }
        else {
            holder.textName.setText(name);
            holder.imageViewIcon.setImageResource(R.drawable.technology);
        }
        holder.itemView.setOnClickListener(v -> {
            iSpecialistViewHolder.onClickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return specialists.size();
    }

    public static class SpecialistViewHolder extends RecyclerView.ViewHolder{
        TextView textName;
        ImageView imageViewIcon;
        public SpecialistViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            imageViewIcon = itemView.findViewById(R.id.icon);
        }
    }
    public interface ISpecialistViewHolder {
        void onClickItem(int position);
    }
}

package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.User;

import java.util.List;

public class RelativeAdapter extends RecyclerView.Adapter<RelativeAdapter.RelativeViewHolder> {
    Context context;
    List<User> userList;
    private RelativeAdapter.IRelativeViewHolder iViewHolder;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public RelativeAdapter(Context context, List<User> userList, IRelativeViewHolder iViewHolder) {
        this.context = context;
        this.userList = userList;
        this.iViewHolder = iViewHolder;
    }

    private void setBackgroundNotSelectedItem(RelativeLayout formRelative) {
        Drawable selectedBackground = ContextCompat.getDrawable(context, R.drawable.custom_form_person_notselected);
        formRelative.setBackground(selectedBackground);
    }

    private void setBackgroundItem(RelativeLayout formRelative) {
        Drawable selectedBackground = ContextCompat.getDrawable(context, R.drawable.custom_form_person_selected);
        formRelative.setBackground(selectedBackground);
    }

    @NonNull
    @Override
    public RelativeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new RelativeAdapter.RelativeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RelativeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userList.get(position);
        holder.name.setText(getNameFromFullName(user.getFullName()));
        holder.detail.setText(user.getRelative());
        if (position == selectedPosition) {
            setBackgroundItem(holder.formRelative);
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            setBackgroundNotSelectedItem(holder.formRelative);
            holder.tick.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousPosition = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                iViewHolder.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class RelativeViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout formRelative, tick;
        TextView name, detail;

        public RelativeViewHolder(@NonNull View itemView) {
            super(itemView);
            formRelative = itemView.findViewById(R.id.form_relative);
            name = itemView.findViewById(R.id.name);
            detail = itemView.findViewById(R.id.btnDetail);
            tick = itemView.findViewById(R.id.tick);
        }
    }

    private String getNameFromFullName(String fullName) {
        String[] str = fullName.split(" ");
        return str[str.length - 1];
    }

    public interface IRelativeViewHolder {
        void onClickItem(int positon);

        void onDataLoaded(int size);
    }

    public void clearBackgroundItems() {
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }
}


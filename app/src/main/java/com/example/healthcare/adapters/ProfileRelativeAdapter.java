package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ProfileRelativeAdapter extends RecyclerView.Adapter<ProfileRelativeAdapter.RelativeViewHolder> {
    List<User> relativeList;
    private IRelativeViewHolder iViewHolder;

    public ProfileRelativeAdapter(List<User> relativeList, IRelativeViewHolder iRelativeViewHolder) {
        this.relativeList = relativeList;
        this.iViewHolder = iRelativeViewHolder;
    }

    public void setiViewHolder(IRelativeViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }



    @NonNull
    @Override
    public RelativeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_relative_item, parent, false);
        return new ProfileRelativeAdapter.RelativeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RelativeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User model = relativeList.get(position);
        holder.accountFullname.setText(model.getFullName());
        holder.accountBirth.setText("Ngày sinh: " + model.getBirth());
        holder.relationship.setText(model.getRelative());
        holder.accountPhoneNumber.setText("Điện thoại: " + model.getPhoneNumber());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return relativeList.size();
    }

    public static class RelativeViewHolder extends RecyclerView.ViewHolder {
        TextView accountFullname, accountBirth, accountPhoneNumber, relationship;

        public RelativeViewHolder(@NonNull View itemView) {
            super(itemView);
            accountFullname = itemView.findViewById(R.id.fullName);
            accountBirth = itemView.findViewById(R.id.birth);
            accountPhoneNumber = itemView.findViewById(R.id.phoneNumber);
            relationship = itemView.findViewById(R.id.relationship);
        }
    }

    public interface IRelativeViewHolder {
        void onClickItem(int positon);

        void onDataLoaded(int size);
    }
}

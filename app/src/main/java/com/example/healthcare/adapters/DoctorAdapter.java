package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.Doctor;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DoctorAdapter extends FirestoreRecyclerAdapter<Doctor, DoctorAdapter.DoctorViewHolder> {
    Context context;
    private IDoctorViewHolder iViewHolder;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public DoctorAdapter(@NonNull FirestoreRecyclerOptions<Doctor> options, Context context, IDoctorViewHolder iDoctorViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iDoctorViewHolder;
    }

    public void setiViewHolder(IDoctorViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull DoctorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Doctor model) {
        holder.textfullName.setText(model.getFullName());
        holder.textNumber.setText(String.valueOf(model.getAppointment()));
        holder.textLocation.setText(model.getWorkPlace());
        holder.textSpecialist.setText(model.getSpecialist());
        String imageCode = model.getImageCode();
        deCodeImage(holder,imageCode);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickItem(position);
            }
        });
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickItem(position);
            }
        });
    }

    private void deCodeImage(DoctorViewHolder holder, String imageCode) {
        if (imageCode != null && !imageCode.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageCode, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageDoctor.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.imageDoctor.setImageResource(R.drawable.drugs);
            }
        } else {
            holder.imageDoctor.setImageResource(R.drawable.drugs);
        }
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (iViewHolder != null) {
            iViewHolder.onDataLoaded(getItemCount());
        }
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_detail_item, parent, false);
        return new DoctorViewHolder(itemView);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView textfullName, textNumber, textLocation, textSpecialist;
        ImageView imageDoctor;
        RelativeLayout btnSelect;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            textfullName = itemView.findViewById(R.id.name);
            imageDoctor = itemView.findViewById(R.id.image);
            textNumber = itemView.findViewById(R.id.appointment);
            textSpecialist = itemView.findViewById(R.id.name_specialist);
            textLocation = itemView.findViewById(R.id.location_work);
            btnSelect = itemView.findViewById(R.id.block_select);
        }
    }

    public interface IDoctorViewHolder {
        void onClickItem(int positon);

        void onDataLoaded(int size);
    }
}


package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.OnlinePrescription;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.PrescriptionViewHolder>{
    private List<OnlinePrescription> prescriptions;

    public PrescriptionAdapter(List<OnlinePrescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public PrescriptionAdapter.PrescriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prescription_item,parent,false);
        return new PrescriptionAdapter.PrescriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionAdapter.PrescriptionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OnlinePrescription prescription = prescriptions.get(position);
        holder.textViewDrugName.setText(prescription.getDrugName());
        holder.textViewNote.setText(prescription.getNote());
        deCodeImage(holder.drugImage,prescription.getImage());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prescriptions.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, prescriptions.size());
            }
        });
    }

    private void deCodeImage(ImageView drugImage, String imageCode) {
        if (imageCode != null && !imageCode.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageCode, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                drugImage.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                drugImage.setImageResource(R.drawable.drugs);
            }
        } else {
            drugImage.setImageResource(R.drawable.drugs);
        }
    }

    @Override
    public int getItemCount() {
        return prescriptions.size();
    }

    public static class PrescriptionViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDrugName, textViewNote;
        ImageView drugImage;
        RelativeLayout btnRemove;
        public PrescriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDrugName = itemView.findViewById(R.id.drug_name);
            drugImage = itemView.findViewById(R.id.drug_image);
            btnRemove = itemView.findViewById(R.id.btn_delete);
            textViewNote = itemView.findViewById(R.id.note);
        }
    }
}

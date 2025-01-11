package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class DoctorAdapter2 extends FirestoreRecyclerAdapter<OnlineDoctor, DoctorAdapter2.DoctorViewHolder> {
    Context context;
    private IDoctorViewHolder iViewHolder;

    public DoctorAdapter2(@NonNull FirestoreRecyclerOptions<OnlineDoctor> options, Context context, IDoctorViewHolder iDoctorViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iDoctorViewHolder;
    }

    public void setiViewHolder(IDoctorViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull DoctorAdapter2.DoctorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull OnlineDoctor model) {
        holder.textFullName.setText(model.getFullName());
        holder.textNumber.setText("Lượt khám: "+String.valueOf(model.getAppointment()));
        holder.textPrice.setText("Giá khám: "+AndroidUtil.formatPrice(model.getPrice()));
        holder.textSpecialist.setText("Chuyên khoa "+model.getSpecialist());
        String imageCode = model.getImageCode();
        deCodeImage(holder,imageCode);

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickView(position);
            }
        });
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickBook(position);
            }
        });
    }

    private void deCodeImage(DoctorAdapter2.DoctorViewHolder holder, String imageCode) {
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
    public DoctorAdapter2.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item2, parent, false);
        return new DoctorAdapter2.DoctorViewHolder(itemView);
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView textFullName, textSpecialist, textPrice, textNumber;
        ImageView imageDoctor;
        RelativeLayout btnSelect, btnView;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullName = itemView.findViewById(R.id.name_doctor);
            textSpecialist = itemView.findViewById(R.id.specialist);
            textPrice = itemView.findViewById(R.id.price);
            textNumber = itemView.findViewById(R.id.number);
            imageDoctor = itemView.findViewById(R.id.image_doctor);
            btnSelect = itemView.findViewById(R.id.btnBook);
            btnView = itemView.findViewById(R.id.btn_detail_doctor);
        }
    }

    public interface IDoctorViewHolder {
        void onClickView(int position);
        void onClickBook(int position);
        void onDataLoaded(int size);
    }
}

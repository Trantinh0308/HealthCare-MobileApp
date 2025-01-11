package com.example.healthcare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AppointmentResultAdapter extends FirestoreRecyclerAdapter<OnlineAppointment, AppointmentResultAdapter.AppointmentViewHolder> {
    Context context;
    private IViewHolder iViewHolder;

    public AppointmentResultAdapter(@NonNull FirestoreRecyclerOptions<OnlineAppointment> options, Context context, IViewHolder iViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iViewHolder;
    }

    public void setiViewHolder(IViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentResultAdapter.AppointmentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull OnlineAppointment model) {
        User user = model.getUser();
        holder.userName.setText(user.getFullName());
        setColorStateAppointment(holder);

        String calendarStr = model.getTime() + " - " + model.getAppointmentDate();
        holder.calendar.setText(calendarStr);
        OnlineDoctor doctor = model.getDoctor();
        if (doctor != null) {
            holder.doctorName.setText("BS." + doctor.getFullName());
        } else {
            holder.doctorName.setText("");
        }
        holder.blockStateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iViewHolder.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public AppointmentResultAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_item, parent, false);
        return new AppointmentResultAdapter.AppointmentViewHolder(itemView);
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, status, calendar, doctorName;
        RelativeLayout blockStateAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.fullName_user);
            status = itemView.findViewById(R.id.status_appointment);
            calendar = itemView.findViewById(R.id.calendar_appointment);
            doctorName = itemView.findViewById(R.id.fullName_doctor_appoitment);
            blockStateAppointment = itemView.findViewById(R.id.block_state_appointment);
        }
    }

    private void setColorStateAppointment(AppointmentResultAdapter.AppointmentViewHolder holder) {
        int bgColorState1 = ContextCompat.getColor(context, R.color.colorStateApproved);
        int textColorState1 = ContextCompat.getColor(context, R.color.chat_color_receiver);
        holder.blockStateAppointment.setBackgroundTintList(ColorStateList.valueOf(bgColorState1));
        holder.status.setTextColor(ColorStateList.valueOf(textColorState1));
        holder.status.setText("Xem kết quả");
    }
    public interface IViewHolder {
        void onClickItem(int position);
    }
}

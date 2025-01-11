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
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.ReExamination;
import com.example.healthcare.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ReExaminationAdapter extends FirestoreRecyclerAdapter<ReExamination, ReExaminationAdapter.AppointmentViewHolder> {
    Context context;
    private IViewHolder iViewHolder;

    public ReExaminationAdapter(@NonNull FirestoreRecyclerOptions<ReExamination> options, Context context, IViewHolder iViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iViewHolder;
    }

    public void setiViewHolder(IViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ReExaminationAdapter.AppointmentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReExamination model) {
        User user = model.getUser();
        holder.userName.setText(user.getFullName());
        setColorStateAppointment(model, holder);
        holder.calendarAppointment.setText("Ngày khám :"+model.getAppointmentDate());
        holder.calendarReAppointment.setText("Ngày tái khám :"+model.getReAppointmentDate());
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
    public ReExaminationAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_re_examination_item, parent, false);
        return new ReExaminationAdapter.AppointmentViewHolder(itemView);
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, status, calendarAppointment, calendarReAppointment, doctorName;
        RelativeLayout blockStateAppointment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.fullName_user);
            status = itemView.findViewById(R.id.status_appointment);
            calendarAppointment = itemView.findViewById(R.id.calendar_appointment);
            calendarReAppointment = itemView.findViewById(R.id.calendar_reExamination);
            doctorName = itemView.findViewById(R.id.fullName_doctor_appoitment);
            blockStateAppointment = itemView.findViewById(R.id.block_state_appointment);
        }
    }

    private void setColorStateAppointment(ReExamination appointment, ReExaminationAdapter.AppointmentViewHolder holder) {
        int bgColorState1 = ContextCompat.getColor(context, R.color.colorStateApproved);
        int textColorState1 = ContextCompat.getColor(context, R.color.chat_color_receiver);
        if (appointment.getStateAppointment() == 1) {
            holder.blockStateAppointment.setBackgroundTintList(ColorStateList.valueOf(bgColorState1));
            holder.status.setTextColor(ColorStateList.valueOf(textColorState1));
            holder.status.setText("Đặt lịch");
        }
    }
    public interface IViewHolder {
        void onClickItem(int position);
    }
}

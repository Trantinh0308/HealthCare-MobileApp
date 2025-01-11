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
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PatientAdapter extends FirestoreRecyclerAdapter<OnlineAppointment, PatientAdapter.AppointmentViewHolder> {
    Context context;
    private PatientAdapter.IPatientViewHolder iViewHolder;
    public PatientAdapter(@NonNull FirestoreRecyclerOptions<OnlineAppointment> options, Context context, IPatientViewHolder iViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iViewHolder;
    }

    public void setiViewHolder(IPatientViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull PatientAdapter.AppointmentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull OnlineAppointment model) {
        User user = model.getUser();
        holder.userName.setText(user.getFullName());
        String timeStr = model.getTime();
        holder.time.setText(timeStr);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
    public PatientAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientAdapter.AppointmentViewHolder(itemView);
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, time;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.fullName_user);
            time = itemView.findViewById(R.id.calendar_appointment);
        }
    }
    public interface IPatientViewHolder {
        void onClickItem(int position);
    }
}

package com.example.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import java.util.List;

public class PredictAdapter extends RecyclerView.Adapter<PredictAdapter.PredictViewHolder>{
    List<String> predicts;
    PredictAdapter.IViewHolder iViewHolder;
    public PredictAdapter(List<String> predicts, PredictAdapter.IViewHolder iViewHolder) {
        this.predicts = predicts;
        this.iViewHolder = iViewHolder;
    }

    @NonNull
    @Override
    public PredictAdapter.PredictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.predict_item, parent, false);
        return new PredictAdapter.PredictViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictAdapter.PredictViewHolder holder, int position) {
        holder.textViewPredict.setText(predicts.get(position));
        holder.btnDoctor.setOnClickListener(v -> {
            iViewHolder.OnclickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return predicts.size();
    }

    public static class PredictViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout btnDoctor;
        TextView textViewPredict;
        public PredictViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDoctor = itemView.findViewById(R.id.doctor);
            textViewPredict = itemView.findViewById(R.id.text_predict);
        }
    }
    public interface IViewHolder{
        void OnclickItem(int position);
    }
}

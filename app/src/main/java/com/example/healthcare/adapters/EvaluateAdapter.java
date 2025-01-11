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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;
import com.example.healthcare.models.Evaluate;
import com.example.healthcare.models.OnlineDoctor;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class EvaluateAdapter extends FirestoreRecyclerAdapter<Evaluate, EvaluateAdapter.ItemViewHolder> {
    Context context;
    private IViewHolder iViewHolder;
    public EvaluateAdapter(@NonNull FirestoreRecyclerOptions<Evaluate> options, Context context, IViewHolder iViewHolder) {
        super(options);
        this.context = context;
        this.iViewHolder = iViewHolder;
    }

    public void setiViewHolder(IViewHolder iViewHolder) {
        this.iViewHolder = iViewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull EvaluateAdapter.ItemViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Evaluate model) {
        holder.textFullName.setText(model.getUser().getFullName());
        holder.ratingBar.setRating(model.getRating());
        holder.comment.setText(model.getComment());
        holder.date.setText(model.getDate());
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
    public EvaluateAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluate_item, parent, false);
        return new EvaluateAdapter.ItemViewHolder(itemView);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textFullName,comment,date;
        RatingBar ratingBar;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullName = itemView.findViewById(R.id.userName);
            ratingBar = itemView.findViewById(R.id.rating);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
        }
    }

    public interface IViewHolder {
        void onDataLoaded(int size);
    }
}

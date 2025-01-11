package com.example.healthcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>{
    List<String> questions;
    List<Boolean> radioButtonStates;
    IViewHolder iViewHolder;
    public QuestionAdapter(List<String> questions, IViewHolder iViewHolder) {
        this.questions = questions;
        this.iViewHolder = iViewHolder;
        this.radioButtonStates = new ArrayList<>(questions.size());
        for (int i = 0; i < questions.size(); i++) {
            radioButtonStates.add(false);
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionAdapter.QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.textViewQuestion.setText(questions.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (holder.rBtn.isChecked()){
                holder.rBtn.setChecked(false);
                radioButtonStates.set(position, false);
            }
            else {
                holder.rBtn.setChecked(true);
                radioButtonStates.set(position, true);
            }
            iViewHolder.OnclickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
    public String getCheckedItems() {
        StringBuilder selectedItems = new StringBuilder();

        for (int i = 0; i < radioButtonStates.size(); i++) {
            if (radioButtonStates.get(i)) {
                if (selectedItems.length() > 0) {
                    selectedItems.append(",");
                }
                selectedItems.append(i);
            }
        }

        return selectedItems.toString();
    }


    public static class QuestionViewHolder extends RecyclerView.ViewHolder{
        RadioButton rBtn;
        TextView textViewQuestion;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            rBtn = itemView.findViewById(R.id.radioButton);
            textViewQuestion = itemView.findViewById(R.id.text_symptom);
        }
    }
    public interface IViewHolder{
        void OnclickItem(int position);
    }
}

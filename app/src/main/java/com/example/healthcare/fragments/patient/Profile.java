package com.example.healthcare.fragments.patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.activities.patient.AddRelative;
import com.example.healthcare.activities.patient.EditProfileRelative;
import com.example.healthcare.activities.patient.EditProfileUser;
import com.example.healthcare.adapters.ProfileRelativeAdapter;
import com.example.healthcare.adapters.RelativeAdapter;
import com.example.healthcare.models.User;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class Profile extends Fragment implements View.OnClickListener {
    ImageView btnAdd, accountImage;
    CardView account;
    TextView accountFullname, accountBirth, accountPhoneNumber,textRelationship;
    RecyclerView listRelative;
    ProfileRelativeAdapter relativeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView =  inflater.inflate(R.layout.activity_list_profile, container, false);
        initView(itemView);
        setOnclick();
        getInForAccount();
        return  itemView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getInForAccount();
    }

    private void initView(View  itemView) {
        btnAdd = itemView.findViewById(R.id.btn_add);
        account = itemView.findViewById(R.id.user);
        accountImage = itemView.findViewById(R.id.image_Account);
        accountFullname = itemView.findViewById(R.id.fullName);
        accountBirth = itemView.findViewById(R.id.birth);
        accountPhoneNumber = itemView.findViewById(R.id.phoneNumber);
        listRelative = itemView.findViewById(R.id.list_profile);
        textRelationship = itemView.findViewById(R.id.relationship);
    }

    private void setOnclick() {
        btnAdd.setOnClickListener(this);
        account.setOnClickListener(this);
    }

    private void getInForAccount() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User userModel = task.getResult().toObject(User.class);
                setupViewAccount(userModel);
                if (userModel != null){
                    List<User> users = userModel.getRelativeList();
                    setUpViewRelativeList(users);
                }
            } else {
                Log.e("ERROR", "Lỗi kết nối");
            }
        });
    }

    private void setUpViewRelativeList(List<User> relativeIdList) {
        if (relativeIdList == null){
            relativeIdList = new ArrayList<>();
        }
        List<User> finalRelativeIdList = relativeIdList;
        relativeAdapter = new ProfileRelativeAdapter(relativeIdList, new ProfileRelativeAdapter.IRelativeViewHolder() {
            @Override
            public void onClickItem(int positon) {
                User relative = finalRelativeIdList.get(positon);
                Intent intent = new Intent(getActivity(), EditProfileRelative.class);
                AndroidUtil.passUserAsIntent(intent,relative);
                startActivity(intent);
            }

            @Override
            public void onDataLoaded(int size) {

            }
        });
        listRelative.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        listRelative.setAdapter(relativeAdapter);
    }

    private void setupViewAccount(User userModel) {
        if (userModel != null){
            accountFullname.setText(userModel.getFullName());
            accountBirth.setText("Ngày sinh: " + userModel.getBirth());
            accountPhoneNumber.setText("Điện thoại: " + userModel.getPhoneNumber());
        }
        else {
            accountFullname.setText("Họ tên : Chưa cập nhật");
            accountBirth.setText("Ngày sinh: Chưa cập nhật");
            accountPhoneNumber.setText("Điện thoại: Chưa cập nhật");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user) {
            Intent intent = new Intent(getActivity(), EditProfileUser.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.btn_add) {
            Intent intent = new Intent(getActivity(), AddRelative.class);
            startActivity(intent);
        }
    }
}
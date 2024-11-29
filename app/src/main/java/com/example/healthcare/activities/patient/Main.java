package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.healthcare.R;
import com.example.healthcare.fragments.patient.Appointment;
import com.example.healthcare.fragments.patient.Home;
import com.example.healthcare.fragments.patient.Menu;
import com.example.healthcare.fragments.patient.Notification;
import com.example.healthcare.fragments.patient.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main extends AppCompatActivity implements View.OnClickListener{
    private BottomNavigationView bottomNavigationView, notificationBG;
    private FrameLayout pageLayout;
    RelativeLayout btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setOnclick();
        setBotomNavigation();
    }

    private void setOnclick() {
        btnHome.setOnClickListener(this);
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.menuBottom);
        notificationBG = findViewById(R.id.notification_bg);
        btnHome = findViewById(R.id.btnHome);
        pageLayout = findViewById(R.id.page_home);
    }

    private void setBotomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.item_user) {
//                    if (FirebaseUtil.currentUserId() == null) {
//                        showDialogLogin(Gravity.CENTER);
//                        return false;
//                    }
                    selectedFragment = new Profile();
                }
                if (item.getItemId() == R.id.item_appointment) {
//                    if (FirebaseUtil.currentUserId() == null) {
//                        showDialogLogin(Gravity.CENTER);
//                        return false;
//                    }
                    selectedFragment = new Appointment();
                }
                if (item.getItemId() == R.id.item_home) {
                    selectedFragment = new Home();
                }
                if (item.getItemId() == R.id.item_notification) {
//                    if (FirebaseUtil.currentUserId() == null) {
//                        showDialogLogin(Gravity.CENTER);
//                        return false;
//                    }
                    selectedFragment = new Notification();
                }
                if (item.getItemId() == R.id.item_menu_bar) {
                    selectedFragment = new Menu();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.page_home, selectedFragment).commit();
                }
                return true;
            }
        });

        // Chọn Fragment mặc định khi mở ứng dụng
        bottomNavigationView.setSelectedItemId(R.id.item_home);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHome) {
            Fragment mainFragment = new Home();
            bottomNavigationView.setSelectedItemId(R.id.item_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.page_home, mainFragment).commit();
        }
    }
}
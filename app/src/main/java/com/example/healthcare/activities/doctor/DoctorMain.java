package com.example.healthcare.activities.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.fragments.doctor.DoctorProfile;
import com.example.healthcare.fragments.doctor.DoctorRoom;
import com.example.healthcare.fragments.patient.Appointment;
import com.example.healthcare.fragments.patient.Home;
import com.example.healthcare.fragments.patient.Menu;
import com.example.healthcare.fragments.patient.Notification;
import com.example.healthcare.fragments.patient.Profile;
import com.example.healthcare.models.Role;
import com.example.healthcare.utils.AndroidUtil;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class DoctorMain extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView, notificationBG;
    private FrameLayout pageLayout;
    RelativeLayout btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        saveRole();
        initView();
        setOnclick();
        setBotomNavigation();
    }

    private void saveRole() {
        FirebaseUtil.roleUser().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Role role = task.getResult().toObject(Role.class);
                if (role != null){
                    AndroidUtil.sharedPreferences(getApplicationContext(),"role",String.valueOf(role.getIsRole()));
                }
                else AndroidUtil.removeSharedPreferences(getApplicationContext(),"role");
            }
        });
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
                    selectedFragment = new DoctorProfile();
                }
                if (item.getItemId() == R.id.item_appointment) {
//                    if (FirebaseUtil.currentUserId() == null) {
//                        showDialogLogin(Gravity.CENTER);
//                        return false;
//                    }
                    selectedFragment = new DoctorRoom();
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

        bottomNavigationView.setSelectedItemId(R.id.item_appointment);
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
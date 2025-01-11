package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.activities.Login;
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

public class Main extends AppCompatActivity implements View.OnClickListener{
    private BottomNavigationView bottomNavigationView;
    private FrameLayout pageLayout;
    RelativeLayout btnHome;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseUtil.currentUserId() != null){
            saveRole();
        }
        initView();
        setOnclick();
        setBotomNavigation();
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.page_home);
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
        btnHome = findViewById(R.id.btnHome);
        pageLayout = findViewById(R.id.page_home);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        bottomNavigationView.setSelectedItemId(R.id.item_home);
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
                    if (currentFragment instanceof Appointment){
                        return false;
                    }
                    if (FirebaseUtil.currentUserId() == null) {
                        showDialogLogin(Gravity.CENTER);
                        return false;
                    }
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
                    currentFragment = selectedFragment;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.item_home);
    }

    private void showDialogLogin(int center) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notice_center);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = center;
        window.setAttributes(windowAttributes);
        if (Gravity.BOTTOM == center) {
            dialog.setCancelable(false);
        } else {
            dialog.setCancelable(true);
        }
        RelativeLayout btnCancel, btnLogin;
        btnCancel = dialog.findViewById(R.id.cancel);
        btnLogin = dialog.findViewById(R.id.agree);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Login.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHome) {
            if (currentFragment instanceof Home){
                return;
            }
            Fragment mainFragment = new Home();
            bottomNavigationView.setSelectedItemId(R.id.item_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.page_home, mainFragment).commit();
        }
    }
}
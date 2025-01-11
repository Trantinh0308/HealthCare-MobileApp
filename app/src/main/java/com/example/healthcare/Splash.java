package com.example.healthcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.healthcare.activities.Login;
import com.example.healthcare.activities.doctor.DoctorMain;
import com.example.healthcare.activities.employee.MainEmployee;
import com.example.healthcare.activities.patient.Main;
import com.example.healthcare.models.OnlineDoctor;
import com.example.healthcare.models.Role;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Splash extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null){
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else getRoleUser();
            }
        }, 2000);
    }

    private void getRoleUser() {
        Query query = FirebaseUtil.allRoleCollectionReference().whereEqualTo("userId",FirebaseUtil.currentUserId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Role role = document.toObject(Role.class);
                        if (role != null) {
                            checkRole(role);
                            break;
                        }
                    }
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void checkRole(Role role) {
        Intent intent = null;
        if (role.getIsRole() == 1){
            intent = new Intent(Splash.this, Main.class);
        }
        else if (role.getIsRole() == 2){
            intent = new Intent(Splash.this, DoctorMain.class);
        }
        else if (role.getIsRole() == 3){
            intent = new Intent(Splash.this, MainEmployee.class);
        }
        if (intent == null) return;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
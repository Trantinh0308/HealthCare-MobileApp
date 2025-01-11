package com.example.healthcare.testCallingVideo;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.healthcare.R;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class Calling extends AppCompatActivity {
    EditText userIdEditText;
    Button btnStart,btnNext;
    String  userId;
    ZegoSendCallInvitationButton videoCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        userIdEditText = findViewById(R.id.userId);
        videoCallBtn = findViewById(R.id.btnCallVideo);
        btnNext = findViewById(R.id.btn_Next);
        btnStart = findViewById(R.id.btnStart);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calling.this,LiveActivity.class);
                startActivity(intent);
            }
        });

        startService("userId");
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setVideoCall("long");
            }
        });
    }

    void startService(String userId){
        try {
            String userName = userId;
            ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
            ZegoUIKitPrebuiltCallService.init(getApplication(), Contants.appid, Contants.appSign, userId, userName,callInvitationConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setVideoCall(String targetUserID){
        try {
            videoCallBtn.setIsVideoCall(true);
            videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ZegoUIKitPrebuiltCallService.unInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
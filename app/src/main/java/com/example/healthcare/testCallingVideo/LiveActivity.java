package com.example.healthcare.testCallingVideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.example.healthcare.R;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig;
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class LiveActivity extends AppCompatActivity {
    TextView userIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        userIdEditText = findViewById(R.id.user_id);

        String testId = "long";
        startService(testId);
    }


    void startService(String userId){
        String userName = userId;
        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        ZegoUIKitPrebuiltCallService.init(getApplication(), Contants.appid, Contants.appSign, userId, userName,callInvitationConfig);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallService.unInit();
    }
}
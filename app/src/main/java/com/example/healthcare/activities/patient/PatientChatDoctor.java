package com.example.healthcare.activities.patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.adapters.ChatRecyclerAdapter;
import com.example.healthcare.models.ChatMessage;
import com.example.healthcare.models.ChatRoom;
import com.example.healthcare.models.OnlineAppointment;
import com.example.healthcare.models.User;
import com.example.healthcare.testCallingVideo.Contants;
import com.example.healthcare.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;

import java.util.Arrays;

public class PatientChatDoctor extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnBack,btnSend;
    EditText editTextChat;
    TextView textDoctorName;
    ImageView btnHome;
    RecyclerView recyclerViewChat;
    ChatRoom chatRoom;
    String chatroomId;
    String doctorId, doctorName, appointmentId, userName;
    ChatRecyclerAdapter chatRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat_doctor);
        if (getIntent() != null){
            doctorId = getIntent().getStringExtra("doctorId");
            doctorName = getIntent().getStringExtra("doctorName");
            appointmentId = getIntent().getStringExtra("appointmentId");
            userName = getIntent().getStringExtra("userName");
        }
        initView();
        textDoctorName.setText("Bác sĩ "+doctorName);
        setOnclick();
        getDataUser();
    }

    private void getDataUser() {
        FirebaseUtil.currentUserDetailsById().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                startService(user.getUserId(),user.getFullName());
                chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),doctorId);
                getOrCreateChatroomModel(chatroomId,user);
                setupChatRecyclerView(chatroomId);
            }
        });
    }

    private void getOrCreateChatroomModel(String chatroomId,User user) {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoom = task.getResult().toObject(ChatRoom.class);
                if(chatRoom==null){
                    chatRoom = new ChatRoom(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),user.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);
                }
            }
        });
    }

    private void setupChatRecyclerView(String chatroomId) {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerViewChat.setLayoutManager(manager);
        recyclerViewChat.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewChat.smoothScrollToPosition(0);
            }
        });
    }

    private void initView() {
        btnBack = findViewById(R.id.back_btn);
        btnSend = findViewById(R.id.message_send);
        editTextChat = findViewById(R.id.chat_message_input);
        textDoctorName = findViewById(R.id.fullName_Chat);
        recyclerViewChat = findViewById(R.id.chat_recycler_view);
        btnHome = findViewById(R.id.home);
    }

    private void setOnclick() {
        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn){
            getOnlineAppointment(appointmentId,0);
            finish();
        }
        if (v.getId() == R.id.message_send){
            String message = editTextChat.getText().toString().trim();
            if (message.isEmpty())
                return;
            editTextChat.setText("");
            sendMessageToDoctor(message);
        }
        if (v.getId() == R.id.home){
            Intent intent = new Intent(this, Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void getOnlineAppointment(String appointmentId,int state) {
        FirebaseUtil.onlineAppointment(appointmentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                OnlineAppointment appointment = task.getResult().toObject(OnlineAppointment.class);
                assert appointment != null;
                if (appointment.getStateAppointment() != -1){
                    appointment.setStateAppointment(state);
                }
                else appointment.setEvent(0);
                saveAppointment(appointment);
            }
        });
    }

    private void saveAppointment(OnlineAppointment appointment) {
        FirebaseUtil.onlineAppointment(appointmentId).set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "onComplete");
            }
        });
    }

    private void sendMessageToDoctor(String message) {
        chatRoom.setLastMessageTimestamp(Timestamp.now());
        chatRoom.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoom.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);

        ChatMessage chatMessageModel = new ChatMessage(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            editTextChat.setText("");
                        }
                    }
                });
    }
    void startService(String userId, String fullName){
        try {
            ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
            ZegoUIKitPrebuiltCallService.init(getApplication(), Contants.appid, Contants.appSign, userId, fullName,callInvitationConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getOnlineAppointment(appointmentId,1);
        try {
            ZegoUIKitPrebuiltCallService.unInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
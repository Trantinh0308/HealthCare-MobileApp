package com.example.healthcare.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.activities.doctor.DoctorMain;
import com.example.healthcare.activities.employee.MainEmployee;
import com.example.healthcare.activities.patient.Main;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.models.Role;
import com.example.healthcare.models.Token;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    EditText emailUser, passWord;
    TextView register, loginGoogle;
    RelativeLayout btnLogin;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;
    String tokenId;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        tokenId = task.getResult();
                        Log.e("TOKEN", tokenId);
                    } else {
                        Log.e("EROR", "Khong lay duoc Token");
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new Dialog(this);
        initView();
        initOnclick();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showDialogLoadingLogin(Gravity.CENTER);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fireBaseAuth(account.getIdToken());
                    }
                }, 2000);
            } catch (Exception e) {
                Log.e("ERROR", "ERROR", e);
            }
        } else {
            CustomToast.showToast(getApplicationContext(), "Lỗi kết nối mạng", Toast.LENGTH_LONG);
        }
    }

    private void initView() {
        emailUser = findViewById(R.id.email);
        emailUser.requestFocus();
        passWord = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        loginGoogle = findViewById(R.id.login_google);
        register = findViewById(R.id.register);
    }


    private void initOnclick() {
        btnLogin.setOnClickListener(this);
        loginGoogle.setOnClickListener(this);
    }

    private void loginGoogle() {
        GoogleSignInOptions gos = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gos);
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void getRole(String currentUserid) {
        addTokenId();
        Query query = FirebaseUtil.allRoleCollectionReference().whereEqualTo("userId",currentUserid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int isRole = -1;
                    for (DocumentSnapshot document : task.getResult()) {
                        Role role = document.toObject(Role.class);
                        if (role != null) {
                            isRole = role.getIsRole();
                            break;
                        }
                    }
                    if (isRole != -1){
                        checkRole(isRole);
                    }
                    else {
                        addRole(FirebaseUtil.currentUserId());
                    }
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void addRole(String userId) {
        Role role = new Role();
        role.setUserId(userId);
        role.setIsRole(1);
        role.setCheckFace(false);
        FirebaseUtil.getRoleCollectionByUserId(userId).set(role).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(Login.this,Main.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkRole(int isRole) {
        Intent intent = null;
        if (isRole == 1){
            intent = new Intent(Login.this, Main.class);
        }
        else if (isRole == 2){
            intent = new Intent(Login.this, DoctorMain.class);
        }
        else if (isRole == 3){
            intent = new Intent(Login.this, MainEmployee.class);
        }
        if (intent == null) return;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void addTokenId() {
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null) {
            Query query = FirebaseUtil.getTokenId().whereEqualTo("userId", currentUserId);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        Token token = documentSnapshot.toObject(Token.class);
                        if (token != null) {
                            List<String> tokenList = token.getTokenList();
                            if (!tokenList.contains(tokenId)) {
                                tokenList.add(tokenId);
                                token.setTokenList(tokenList);
                                updateToken(documentSnapshot.getId(), token);
                            }
                        }
                    } else {
                        List<String> tokenList = new ArrayList<>();
                        tokenList.add(tokenId);
                        Token token = new Token(tokenList, currentUserId);
                        addNewToken(token);
                    }
                } else {
                    Log.e("ERROR", "Lỗi kết nối");
                }
            });
        }
    }

    private void addNewToken(Token token) {
        FirebaseUtil.getTokenId().add(token).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SUCCESSFULL", "Thêm mới Token thành công");
            } else Log.e("ERROR", "Lỗi kết nối");
        });
    }

    private void updateToken(String documentId, Token token) {
        FirebaseUtil.getTokenByDocument(documentId).set(token).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SUCCESSFULL", "Thêm tokenId thành công");
            } else Log.e("ERROR", "Lỗi kết nối");
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            String email = emailUser.getText().toString();
            String pass = passWord.getText().toString();
            if (email.equals("") || pass.equals("")) {
                Toast.makeText(this, "Điền đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            onClickLogin(email, pass);
        } else if (v.getId() == R.id.login_google) {
            loginGoogle();
        }
    }

    private void showDialogLoadingLogin(int center) {
        dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_loading);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = center;
        window.setAttributes(windowAttributes);

        TextView textTitle;
        textTitle = dialog.findViewById(R.id.title);
        textTitle.setText("Đang đăng nhập...");

        dialog.show();
    }

    private void onClickLogin(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showDialogLoadingLogin(Gravity.CENTER);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String currentUserId = FirebaseUtil.currentUserId();
                                    getRole(currentUserId);
                                }
                            }, 2000);
                        } else {
                            CustomToast.showToast(getApplicationContext(), "Lỗi kết nối mạng", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    private void fireBaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getRole(FirebaseUtil.currentUserId());
                        } else {
                            CustomToast.showToast(getApplicationContext(), "Lỗi kết nối mạng", Toast.LENGTH_LONG);
                        }
                    }
                });
    }
}
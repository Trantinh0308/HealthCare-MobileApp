package com.example.healthcare.fragments.patient;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthcare.R;
import com.example.healthcare.activities.Login;
import com.example.healthcare.activities.patient.Main;
import com.example.healthcare.models.Token;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class Menu extends Fragment implements View.OnClickListener {
    Dialog dialog;
    TextView userName, userGenderBirth;
    ImageView imageAccount;
    GoogleSignInClient googleSignInClient;
    CardView btnLogout, btnLogin;
    String currentUserId = "";

    public Menu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_menu, container, false);
        currentUserId = FirebaseUtil.currentUserId();
        dialog = new Dialog(getContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        initView(itemView);
        setOnclick();
        setBtnLoginAndLogout();
        return itemView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View itemView) {
        btnLogout = itemView.findViewById(R.id.btn_logout);
        btnLogin = itemView.findViewById(R.id.btn_login);
        userName = itemView.findViewById(R.id.fullName_user);
        userGenderBirth = itemView.findViewById(R.id.gender_birth);
        imageAccount = itemView.findViewById(R.id.image_account);

    }

    private void setBtnLoginAndLogout() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
        else {
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        }
    }

    private void setOnclick() {
        btnLogout.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_logout) {
            showDialogLoadingLogout(Gravity.CENTER);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    sigOut();
                }
            }, 2000);
        }
        if (v.getId() == R.id.btn_login){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }
    }

    private void showDialogLoadingLogout(int center) {
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
        textTitle.setText("Đang đăng xuất...");

        dialog.show();
    }

    private void sigOut() {
        getCurrentUserTokenId(new TokenUserFetchCallback() {
            @Override
            public void onTokenUserFetchComplete() {
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    Intent intent = new Intent(getActivity(), Main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        });
    }

    private void getCurrentUserTokenId(TokenUserFetchCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String tokenId = task.getResult();
                        removeTokenId(FirebaseUtil.currentUserId(), tokenId);
                        callback.onTokenUserFetchComplete();
                    } else {
                        Log.e("EROR", "Khong lay duoc Token");
                    }
                });
    }

    private void removeTokenId(String currentUserId, String tokenId) {
        Query query = FirebaseUtil.getTokenId().whereEqualTo("userId", currentUserId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    Token token = documentSnapshot.toObject(Token.class);
                    if (token != null) {
                        List<String> tokenList = token.getTokenList();
                        for (int i = 0; i < tokenList.size(); i++) {
                            if (tokenList.get(i).equals(tokenId)) {
                                tokenList.remove(i);
                                break;
                            }
                        }
                        updateTonken(documentSnapshot.getId(), tokenList);
                    }
                }
            } else {
                Log.e("ERROR", "Lỗi kết nối");
            }
        });
    }

    private void updateTonken(String documentSnapshot, List<String> tokenList) {
        Token token = new Token(tokenList, currentUserId);
        FirebaseUtil.getTokenByDocument(documentSnapshot).set(token).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SUCCESSFULL", "Update tokenList thành công");
            } else Log.e("ERROR", "Lỗi kết nối");
        });
    }

    public interface TokenUserFetchCallback {
        void onTokenUserFetchComplete();
    }
}
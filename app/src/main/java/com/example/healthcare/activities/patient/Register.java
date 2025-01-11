package com.example.healthcare.activities.patient;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.healthcare.R;
import com.example.healthcare.models.Role;
import com.example.healthcare.utils.ApiClient;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {
    ProgressBar progressBar;
    RelativeLayout blockNotice;
    TextView textViewComplete;
    private static final int CAMERA_PERMISSION_CODE = 1;
    private PreviewView frameCamera;
    private ImageCapture imageCapture;
    private int count = 0;
    private Handler handler = new Handler();
    private Runnable takePictureRunnable;

    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameCamera = findViewById(R.id.viewFinder);
        progressBar = findViewById(R.id.progress_register);
        blockNotice = findViewById(R.id.block_notice);
        textViewComplete = findViewById(R.id.text_complete);
        progressBar.setProgress(0);
        checkCameraPermissionAndStartCamera();
    }

    private void checkCameraPermissionAndStartCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            frameCamera.setVisibility(View.VISIBLE);
            startCameraAndTakePicture();
        }
    }

    private void startCameraAndTakePicture() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(frameCamera.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                handler.postDelayed(this::startTakingPictures, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void startTakingPictures() {
        takePictureRunnable = new Runnable() {
            @Override
            public void run() {
                if (count < 10) {
                    takePicture();
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(this);
                    registerFace(FirebaseUtil.currentUserId(),imageUris);
                }
            }
        };

        handler.post(takePictureRunnable);
    }


    private void takePicture() {
        File photoFile = new File(getFilesDir(), "camera_photo_" + count + ".jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                Uri imageUri = Uri.fromFile(photoFile);
                imageUris.add(imageUri);
                count++;
                int progress = (int) ((count / 10.0) * 90);
                progressBar.setProgress(progress);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Error taking photo: " + exception.getMessage(), exception);
            }
        });
    }

    private void registerFace(String userId, List<Uri> imageUris) {
        ApiClient.registerFace(userId, imageUris, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    progressBar.setProgress(100);
                    blockNotice.setVisibility(View.VISIBLE);
                    textViewComplete.setVisibility(View.VISIBLE);
                    getUserRole(FirebaseUtil.currentUserId());
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(Register.this, "Lỗi", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getUserRole(String userId) {
        FirebaseUtil.getRoleCollectionByUserId(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Role role = task.getResult().toObject(Role.class);
                assert role != null;
                role.setCheckFace(true);
                saveUserRole(role);
            }
        });
    }

    private void saveUserRole(Role role) {
        FirebaseUtil.getRoleCollectionByUserId(FirebaseUtil.currentUserId()).set(role).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDialogToUser(Gravity.CENTER);
                    }
                }, 600);
            }
        });
    }

    private void showDialogToUser(int center) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invitation);
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
        RelativeLayout btnCancel, btnAccept;
        TextView textTitle, textTitleTop;

        textTitleTop = dialog.findViewById(R.id.title_top);
        textTitle = dialog.findViewById(R.id.title_below);
        textTitleTop.setText("Đăng ký thành công");
        textTitle.setText("Bạn có muốn tiếp tục truy cập không ?");

        btnCancel = dialog.findViewById(R.id.cancel);
        btnAccept = dialog.findViewById(R.id.agree);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Register.this, FaceRecognition.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                frameCamera.setVisibility(View.VISIBLE);
                startCameraAndTakePicture();
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền sử dụng camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageCapture != null) {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            cameraProvider.unbindAll();
            frameCamera.setVisibility(View.INVISIBLE);
        }
        if (takePictureRunnable != null) {
            handler.removeCallbacks(takePictureRunnable);
        }
    }
}

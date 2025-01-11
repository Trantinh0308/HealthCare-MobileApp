package com.example.healthcare.activities.patient;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthcare.R;
import com.example.healthcare.models.CustomToast;
import com.example.healthcare.utils.ApiClient;
import com.example.healthcare.utils.FirebaseUtil;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class FaceRecognition extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1;
    private PreviewView frameCamera;
    private ImageCapture imageCapture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        frameCamera = findViewById(R.id.viewFinder);
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

                new Handler().postDelayed(this::takePicture, 3000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        File photoFile = new File(getFilesDir(), "camera_photo.jpg");
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                Uri imageUri = Uri.fromFile(photoFile);
                sendImageToServer(imageUri);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Lỗi khi chụp ảnh: " + exception.getMessage(), exception);
            }
        });
    }

    private void sendImageToServer(Uri imageUri) {
        ApiClient.recognizeFace(imageUri, new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                getResultFromServer();
            }

            @Override
            public void onFailure(String error) {
                Log.e("Face Recognition", "Error: " + error);
            }
        });
    }

    private void getResultFromServer() {
        ApiClient.getRecognitionResult(new ApiClient.ApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    JSONObject jsonResponse = null;String userId;
                    try {
                        jsonResponse = new JSONObject(response);
                        userId = jsonResponse.getString("userId");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (userId.equals(FirebaseUtil.currentUserId())){
                        Intent intent = new Intent(FaceRecognition.this, MedicalRecords.class);
                        startActivity(intent);
                    }
                    else showDialogToUser(Gravity.CENTER);
                });
            }
            @Override
            public void onFailure(String error) {
                Log.e("API Error", error);
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
        textTitleTop.setText("Lỗi truy cập");
        textTitle.setText("Bạn có muốn xác thực lại không ?");

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
                checkCameraPermissionAndStartCamera();
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
                CustomToast.showToast(this, "Vui lòng cho phép quyền truy cập camera để chụp ảnh", 1000);
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
    }
}
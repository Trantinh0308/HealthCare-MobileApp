package com.example.healthcare;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.healthcare.models.WebSocketManager;
import com.google.common.util.concurrent.ListenableFuture;
import androidx.camera.view.PreviewView;

public class Websocket extends AppCompatActivity {

    private WebSocketManager webSocketManager;
    private ImageCapture imageCapture;
    private PreviewView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);

        // Initialize WebSocketManager and connect to the server
        webSocketManager = new WebSocketManager();

        // Set WebSocketListener to handle WebSocket events
        webSocketManager.setWebSocketListener(new WebSocketManager.WebSocketListener() {
            @Override
            public void onConnect() {
                Log.d("WebSocket", "WebSocket connection successful");
                Toast.makeText(Websocket.this, "Connected to server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnect() {
                Log.d("WebSocket", "WebSocket disconnected");
                Toast.makeText(Websocket.this, "Disconnected from server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e("WebSocket", "Error: " + error);
                Toast.makeText(Websocket.this, "WebSocket error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Connect to the WebSocket server
        webSocketManager.connect("ws://192.168.39.245:8765"); // Replace with your Python server URL

        // Get the PreviewView from layout
        viewFinder = findViewById(R.id.viewFinder);

        // Check and request camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startCamera();
        }
    }

    // Request camera permission at runtime
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(); // Start the camera if permission granted
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Get cameraProvider
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Create ImageCapture builder
                imageCapture = new ImageCapture.Builder().build();

                // Create CameraSelector for back camera (or front camera)
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK) // Use back camera
                        .build();

                // Create Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider()); // Link with PreviewView

                // Bind camera and Preview to lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);

            } catch (Exception e) {
                Log.e("CameraActivity", "Camera start failed: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.close(); // Close WebSocket connection when the Activity is destroyed
    }
}

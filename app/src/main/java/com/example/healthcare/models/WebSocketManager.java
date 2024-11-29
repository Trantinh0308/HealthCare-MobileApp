package com.example.healthcare.models;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager {

    private WebSocket webSocket;
    private WebSocketListener webSocketListener;

    // Define the WebSocketListener interface
    public interface WebSocketListener {
        void onConnect();
        void onDisconnect();
        void onError(String error);
    }

    // Method to set the listener
    public void setWebSocketListener(WebSocketListener listener) {
        this.webSocketListener = listener;
    }

    // Connect to the WebSocket server
    public void connect(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        WebSocketListener listener = new WebSocketListener() {

            @Override
            public void onConnect() {
                Log.d("WebSocket", "Connected to the server.");
                if (webSocketListener != null) {
                    webSocketListener.onConnect(); // Notify listener
                }
            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onError(String error) {
                Log.e("WebSocket", "Error occurred: " );
                if (webSocketListener != null) {
                    webSocketListener.onError("t.getMessage()"); // Notify listener
                }
            }










        };

        // Connect the WebSocket
        webSocket = client.newWebSocket(request, (okhttp3.WebSocketListener) listener);
    }

    // Send frame data through the WebSocket connection
    public void sendFrame(byte[] frameData) {
        if (webSocket != null) {
            webSocket.send(ByteString.of(frameData));
        }
    }

    // Close the WebSocket connection
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing WebSocket");
        }
    }
}
package com.example.healthcare.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.healthcare.R;


public class AlarmReceiver extends BroadcastReceiver {
    private static int notificationId = 1;
    static final String title = "Bạn có một lịch khám trực tuyến vào lúc ";
    final String CHANNEL_ID = "201";
    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("remind")) {
            String date = intent.getStringExtra("dateRemind");
            String time = intent.getStringExtra("appointmentTime");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        CHANNEL_ID, "PushNotification", NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(notificationChannel);
            }
            String body = title + time +" - "+date;
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(body);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("HealthCare nhắc lịch")
                    .setContentText(body)
                    .setSmallIcon(R.drawable.notification)
                    .setColor(ContextCompat.getColor(context, R.color.mainColor))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle);

            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
    private synchronized int generateNotificationId() {
        return notificationId++;
    }
}

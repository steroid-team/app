package com.github.steroidteam.todolist.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.steroidteam.todolist.R;

public class ReminderBroadcast extends BroadcastReceiver {
    public static int REMINDER_ID = 200;
    public static String REMINDER_CHANNEL_ID = "notifyChannelID";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Test of reminder")
                .setContentText("Hey This is a test !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(REMINDER_ID, builder.build());
    }
}


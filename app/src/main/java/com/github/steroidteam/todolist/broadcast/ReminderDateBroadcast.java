package com.github.steroidteam.todolist.broadcast;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.github.steroidteam.todolist.R;
import java.util.Date;

public class ReminderDateBroadcast extends BroadcastReceiver {
    public static int REMINDER_DATE_ID = 200;
    public static String REMINDER_DATE_CHANNEL_ID = "notifyDateChannelID";
    public static CharSequence NAME_DATE_CHANNEL = "ReminderDateChannel";
    public static String DESCRIPTION_DATE_CHANNEL = "Channel for the date reminder";
    private static int unique_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskDescription =
                intent.getStringExtra(context.getString(R.string.content_date_reminder));
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, REMINDER_DATE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.title_date_reminder))
                        .setContentText(taskDescription)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(REMINDER_DATE_ID, builder.build());
    }

    public static void createNotificationChannel(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel(
                            REMINDER_DATE_CHANNEL_ID, NAME_DATE_CHANNEL, importance);
            channel.setDescription(DESCRIPTION_DATE_CHANNEL);

            NotificationManager notificationManager =
                    activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Just call this method to put a notification based on time
    public static void createNotification(Date date, String taskDescription, Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), ReminderDateBroadcast.class);
        intent.putExtra(
                activity.getApplicationContext().getString(R.string.content_date_reminder),
                taskDescription);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        activity.getApplicationContext(), unique_ID++, intent, 0);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
    }
}

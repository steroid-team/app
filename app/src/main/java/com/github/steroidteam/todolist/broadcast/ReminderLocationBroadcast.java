package com.github.steroidteam.todolist.broadcast;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.view.ItemViewFragment;

public class ReminderLocationBroadcast extends BroadcastReceiver {
    public static int REMINDER_LOC_ID = 201;
    public static String REMINDER_LOC_CHANNEL_ID = "notifyLocationChannelID";
    public static CharSequence NAME_LOCATION_CHANNEL = "ReminderLocationChannel";
    public static String DESCRIPTION_LOCATION_CHANNEL = "Channel for the location reminder";
    // The radius of the central point alert region in meters
    private static final float RADIUS = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, REMINDER_LOC_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.title_location_reminder))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentText(context.getString(R.string.content_location_reminder))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(REMINDER_LOC_ID, builder.build());
    }

    public static void createLocationNotificationChannel(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            REMINDER_LOC_CHANNEL_ID,
                            NAME_LOCATION_CHANNEL,
                            NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(DESCRIPTION_LOCATION_CHANNEL);

            NotificationManager notificationManager =
                    activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Just call this method to put a notification based on time
    public static void createLocationNotification(Location location, Activity activity) {
        Intent intent =
                new Intent(activity.getApplicationContext(), ReminderLocationBroadcast.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(activity.getApplicationContext(), 0, intent, 0);

        LocationManager locationManager =
                (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(
                                activity.getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                                activity.getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    ItemViewFragment.PERMISSIONS_ACCESS_LOCATION);
            return;
        }
        locationManager.addProximityAlert(
                location.getLatitude(), location.getLongitude(), RADIUS, -1, pendingIntent);
    }
}

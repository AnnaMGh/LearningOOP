package com.learning.java.app.utilities;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

import com.learning.java.app.R;
import com.learning.java.app.database.SqliteHelper;
import com.learning.java.app.model.User;

public class ServiceChecker extends IntentService {

    SharedPreferences settings;
    SqliteHelper database;
    User u;

    public ServiceChecker() {
        super("schecker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (condition()) {
            notification();
            scheduleInFuture();
        }
    }

    boolean condition() {

        if (getBaseContext() != null) {
            settings = getSharedPreferences("Learning_java", 0);
            database = SqliteHelper.getInstance(getBaseContext());

            String id = settings.getString("ACCOUNT_KEY", null);

            if (id != null) {
                u = database.getUser(id);

                if (u.getNotifications() == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false;
    }


    void notification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.drawable.java_blue_logo)
                .setContentTitle("Learning java")
                .setContentText("Time passed by. Don't forget to improve your java skills!");

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());

    }

    public void scheduleInFuture() {
        long ct = System.currentTimeMillis();
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), ServiceChecker.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

        if (mgr != null) {
            //mgr.set(AlarmManager.RTC_WAKEUP, ct + 10 * 60 * 1000/*10 minute*/, pi);
            mgr.set(AlarmManager.RTC_WAKEUP, ct + 1 * 60 * 1000/*10 minute*/, pi);
            //h/m/s/ms
        }
        stopSelf(); //stop service since its no longer needed and new alarm is set
    }
}
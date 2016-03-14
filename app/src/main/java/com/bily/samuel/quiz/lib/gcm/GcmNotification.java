package com.bily.samuel.quiz.lib.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bily.samuel.quiz.MainActivity;
import com.bily.samuel.quiz.R;

/**
 * Created by samuel on 22.11.2015.
 */
public class GcmNotification extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        try{
            if(intent.getStringExtra("type").equals("0")){
                Intent resultIntent = new Intent(this, MainActivity.class);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                buildNotification(8020, intent, resultPendingIntent);
            }
        }catch(NullPointerException e){
            Log.e("GCM NullPointerExeption", e.toString());
        }
        return null;
    }

    public void buildNotification(int id, Intent intent, PendingIntent resultPendingIntent){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("message"))
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(resultPendingIntent);
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;
        notificationBuilder.setDefaults(defaults);
        notificationBuilder.setAutoCancel(true);
        notificationManager.notify(id,notificationBuilder.build());
    }
}

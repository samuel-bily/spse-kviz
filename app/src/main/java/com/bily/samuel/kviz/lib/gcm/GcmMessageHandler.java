package com.bily.samuel.kviz.lib.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by samuel on 22.11.2015.
 */
public class GcmMessageHandler extends IntentService {

    private Handler handler;

    public GcmMessageHandler(){
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
        Context context = GcmReceiver.getContext();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        String title = extras.getString("title");
        String message = extras.getString("message");
        String type = extras.getString("type");

        Intent i = new Intent(context, GcmNotification.class);
        i.putExtra("title",title);
        i.putExtra("message",message);
        i.putExtra("type",type);
        context.startService(i);
        Log.e("GCM", "Received: (" + messageType + ") " + title);
        GcmReceiver.completeWakefulIntent(intent);**/

    }
}

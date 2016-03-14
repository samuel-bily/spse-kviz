package com.bily.samuel.quiz.lib.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by samuel on 22.11.2015.
 */
public class GcmReceiver extends WakefulBroadcastReceiver{

    static Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName componentName = new ComponentName(context.getPackageName(),GcmMessageHandler.class.getName());
        GcmReceiver.context = context;
        startWakefulService(context,(intent.setComponent(componentName)));
        setResultCode(Activity.RESULT_OK);
    }

    public static Context getContext(){
        return context;
    }
}

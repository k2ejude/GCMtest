package com.example.jude.gcmtest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

    public static final int NOTIFCATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "GCMDemo";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType  = gcm.getMessageType(intent);

        if(!extras.isEmpty()){// has effect of unparcelling Bundle
             /*
                         * Filter messages based on message type. Since it is likely that GCM
                         * will be extended in the future with new message types, just ignore
                         * any message types you're not interested in, or that you don't
                         * recognize.
                         */
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType )) {
                sendNotification("Send error: " + extras.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                for(int i=0; i<5; i++){
                    Log.i(TAG, "Working... "+ (i+1)+"/5 @ " + SystemClock.elapsedRealtime());
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e){}
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: "+ extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg){
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Notification").setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFCATION_ID, mBuilder.build());
    }
}

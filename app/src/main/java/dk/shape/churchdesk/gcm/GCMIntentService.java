package dk.shape.churchdesk.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 22/05/15.
 */
public class GCMIntentService extends IntentService {

    private static final String KEY_BODY = "body";

    public static final int NOTIFICATION_ID = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GCMIntentService() {
        super("CD-GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(getApplicationContext(), "We got a push notification", Toast.LENGTH_SHORT).show();
//        Bundle extras = intent.getExtras();
//
//        if (!extras.isEmpty()) {
//            String message = intent.getStringExtra(KEY_BODY);
//            extras.remove(KEY_BODY);
//            sendNotification(message, LoginActivity.class, extras);
//        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, Class clzz, Bundle extras) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        Intent openIntent = new Intent(this, clzz);
        if (extras != null)
            openIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

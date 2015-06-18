package dk.shape.churchdesk.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import dk.shape.churchdesk.MainActivity;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 22/05/15.
 */
public class GCMIntentService extends IntentService {

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
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty() && extras.containsKey("type"))
            sendNotification(extras);

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = extras.getString("message");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.login_logo)
                        .setAutoCancel(true)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        Intent openIntent = new Intent(this, MainActivity.class);
        openIntent.putExtras(extras);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

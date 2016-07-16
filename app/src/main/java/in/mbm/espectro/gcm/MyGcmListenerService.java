package in.mbm.espectro.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import in.mbm.espectro.R;
import in.mbm.espectro.Splash;

/**
 * Created by tarunsmalviya12 on 3/3/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    /**
     * Variables.
     */
    private SharedPreferences sharedPref;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            String message = data.getString("message");

            if (from.startsWith("/topics/events")) {
                // message received from some topic.
                sendNotification(new JSONObject(message));
            } else {
                // normal downstream message.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(JSONObject message) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(message.optString("title"))
                .setContentText(message.optString("description"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(message.optString("img_url"))) {
            try {
                Bitmap remote_picture = BitmapFactory.decodeStream((InputStream) new URL(message.optString("img_url")).getContent());

                // Create the style object with BigPictureStyle subclass.
                NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
                pictureStyle.setBigContentTitle(message.optString("title"));
                pictureStyle.setSummaryText(message.optString("description"));
                pictureStyle.bigPicture(remote_picture);

                notificationBuilder.setStyle(pictureStyle).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}

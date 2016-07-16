package in.mbm.espectro.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import in.mbm.espectro.utils.Constants;
import in.mbm.espectro.utils.SharedPreferenceKey;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class RegistrationService extends IntentService {

    public RegistrationService() {
        super("RegistrationService");
    }

    /**
     * Variables.
     */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public void onHandleIntent(Intent intent) {
        try {
            sharedPref = getSharedPreferences(SharedPreferenceKey.SHARED_PREFERENCE, Context.MODE_PRIVATE);
            editor = sharedPref.edit();

            // Generating GCM ID.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(Constants.GOOGLE_GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // Subscribing for events topic messages.
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + "events", null);

            editor.putString(SharedPreferenceKey.GCM_REGISTRATION_ID, token);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            editor.putString(SharedPreferenceKey.GCM_REGISTRATION_ID, "");
            editor.commit();
        }
    }
}
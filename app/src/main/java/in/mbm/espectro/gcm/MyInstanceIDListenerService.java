package in.mbm.espectro.gcm;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Variables.
     */
    private SharedPreferences sharedPref;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
    }
}

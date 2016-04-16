package co.edu.udistrital.dulcesparamiamor.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import co.edu.udistrital.dulcesparamiamor.R;

/**
 * Created by Oscar on 15/04/2016.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }



    @Override
    public void onHandleIntent(Intent intent) {

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_SenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            System.out.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
package co.edu.udistrital.dulcesparamiamor.gcm;

import android.content.Context;

/**
 * Created by Oscar on 17/04/2016.
 */
public interface IGCMClient {

    public  void getGCMRegId(final String gcm_SenderId, final Context applicationContext);
}

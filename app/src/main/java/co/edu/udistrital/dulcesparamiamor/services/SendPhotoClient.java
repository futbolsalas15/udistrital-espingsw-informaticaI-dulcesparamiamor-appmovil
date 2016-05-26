package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.fotoamor.OEPhotoLove;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

/**
 * Created by JulioS on 03/04/2016.
 */
public class SendPhotoClient {

    WebServiceResponseListener listener;
    private ServiceClient serviceClient;

    public SendPhotoClient(Context context){
            serviceClient  = new ServiceClient(context.getString(R.string.ws_enviarfoto_amor_url),context.getString(R.string.ws_namespace) ,context.getString(R.string.ws_validar_amor__method), "u" );

    }

    public WebServiceResponseListener getListener() {
        return listener;
    }

    public void setListener(WebServiceResponseListener listener) {
        Log.e("Set listener", "..");
        this.listener = listener;
        serviceClient.setWsListener(listener);
    }

    public void addPhotoLove(final PropertyInfo[] properties ){
        serviceClient.requestAsync(properties);
    }
}

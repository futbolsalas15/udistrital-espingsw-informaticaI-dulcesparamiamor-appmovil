package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.opencv.core.Mat;
import org.opencv.objdetect.Objdetect;

import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OEToken;
import co.edu.udistrital.dulcesparamiamor.services.validaramor.OEValidarAmor;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

/**
 * Created by JulioS on 03/04/2016.
 */
public class ValidarAmorClient {

    WebServiceResponseListener listener;
    private ServiceClient serviceClient;

    public ValidarAmorClient(Context context) {
        serviceClient = new ServiceClient(context.getString(R.string.ws_validar_amor_url), context.getString(R.string.ws_namespace), context.getString(R.string.ws_validar_amor_method), "u");

    }

    public WebServiceResponseListener getListener() {
        return listener;
    }

    public void setListener(WebServiceResponseListener listener) {
        Log.e("Set listener", "..");
        this.listener = listener;
        serviceClient.setWsListener(listener);
    }

    public void validarAmor(final PropertyInfo[] properties ) {
        serviceClient.requestAsync(properties);
    }
}

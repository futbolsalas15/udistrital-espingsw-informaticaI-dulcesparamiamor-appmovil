package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.util.Log;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OEAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OEToken;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

/**
 * Created by JulioS on 01/05/2016.
 */
public class GenerarTokenClient {

    WebServiceResponseListener listener;
    private ServiceClient serviceClient;

    public GenerarTokenClient(Context context){
        serviceClient  = new ServiceClient(context.getString(R.string.ws_generar_token_url),context.getString(R.string.ws_namespace) ,context.getString(R.string.ws_generar_token_method) , "a");

    }

    public WebServiceResponseListener getListener() {
        return listener;
    }

    public void setListener(WebServiceResponseListener listener) {
        Log.e("Set listener", "..");
        this.listener = listener;
        serviceClient.setWsListener(listener);
    }

    public void generarToken(OEToken oeToken){
        serviceClient.requestAsync(oeToken);
    }


}

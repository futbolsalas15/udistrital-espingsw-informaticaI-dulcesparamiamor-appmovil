package co.edu.udistrital.dulcesparamiamor.presenter.webservices;

import android.content.Context;
import android.util.Log;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.webservices.autenticarusuario.OEAutenticar;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

/**
 * Created by JulioS on 10/04/2016.
 */
public class AutenticarUsuarioClient{

    WebServiceResponseListener listener;
    private ServiceClient serviceClient;

    public AutenticarUsuarioClient(Context context){
        serviceClient  = new ServiceClient(context.getString(R.string.ws_autenticar_usuario_url),context.getString(R.string.ws_namespace) ,context.getString(R.string.ws_autenticar_usuario_method) , "u");

    }

    public WebServiceResponseListener getListener() {
        return listener;
    }

    public void setListener(WebServiceResponseListener listener) {
        Log.e("Set listener","..");
        this.listener = listener;
        serviceClient.setWsListener(listener);
    }

    public void autenticarUsuario(OEAutenticar autenticarInput){
        Log.e("Asyncall ", "autenticar");
        serviceClient.requestAsync(autenticarInput);
    }


}

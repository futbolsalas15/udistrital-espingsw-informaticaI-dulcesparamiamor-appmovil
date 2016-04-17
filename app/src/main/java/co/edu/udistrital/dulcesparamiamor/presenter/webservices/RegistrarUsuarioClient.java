package co.edu.udistrital.dulcesparamiamor.presenter.webservices;

import android.content.Context;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.webservices.registrarusuario.OEUsuario;
import co.edu.udistrital.dulcesparamiamor.utils.ServiceClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

/**
 * Created by JulioS on 10/04/2016.
 */
public class RegistrarUsuarioClient  {
    WebServiceResponseListener listener;

    private ServiceClient serviceClient;
    public RegistrarUsuarioClient(Context context){
        serviceClient  = new ServiceClient(context.getString(R.string.ws_registrar_usuario_url),context.getString(R.string.ws_namespace) ,context.getString(R.string.ws_registrar_usuario_method), "u");

    }

    public WebServiceResponseListener getListener() {
        return listener;
    }

    public void setListener(WebServiceResponseListener listener) {
        this.listener = listener;
        serviceClient.setWsListener(listener);
    }


  public void registrarUsuario(OEUsuario registarUsuarioInput){
      serviceClient.requestAsync(registarUsuarioInput);
  }

}

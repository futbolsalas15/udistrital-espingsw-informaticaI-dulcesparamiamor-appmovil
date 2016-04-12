package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;
import org.opencv.core.Mat;
import org.opencv.objdetect.Objdetect;

import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.registrarusuario.OEUsuario;
import co.edu.udistrital.dulcesparamiamor.services.registrarusuario.OSUsuario;
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

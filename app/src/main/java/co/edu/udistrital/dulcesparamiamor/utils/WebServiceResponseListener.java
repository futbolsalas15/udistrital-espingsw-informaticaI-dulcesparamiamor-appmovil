package co.edu.udistrital.dulcesparamiamor.utils;

import org.ksoap2.serialization.SoapObject;

import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OSAutenticar;

/**
 * Created by JulioS on 10/04/2016.
 */
public interface WebServiceResponseListener {


        public void onWebServiceResponse(SoapObject result);


}

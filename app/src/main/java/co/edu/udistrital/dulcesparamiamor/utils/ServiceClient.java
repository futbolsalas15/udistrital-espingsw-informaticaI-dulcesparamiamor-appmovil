package co.edu.udistrital.dulcesparamiamor.utils;

/**
 * Created by JulioS on 02/04/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.objdetect.Objdetect;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.services.Response;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OEAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OSAutenticar;

public class ServiceClient implements Serializable {

    private static final boolean DEBUG_SOAP_REQUEST_RESPONSE = true;
    private static String SESSION_ID;
    private String requestURL;
    String namespace;
    String methodName;
    boolean objectMapping = false;
    boolean isDotNet = true;
    String objectParameterName = "";
    WebServiceResponseListener wsListener;

    public WebServiceResponseListener getWsListener() {
        return wsListener;
    }

    public boolean isDotNet() {
        return isDotNet;
    }

    public void setIsDotNet(boolean isDotNet) {
        this.isDotNet = isDotNet;
    }

    public void setWsListener(WebServiceResponseListener wsListener) {
        this.wsListener = wsListener;

    }

    public ServiceClient(String requestURL, String namespace, String methodName) {
        this.requestURL = requestURL;
        this.namespace = namespace;
        this.methodName = methodName;
    }

    public ServiceClient(String requestURL, String namespace, String methodName, String objectParameterName) {
        this.requestURL = requestURL;
        this.namespace = namespace;
        this.methodName = methodName;
        this.objectParameterName = objectParameterName;
    }
    private final void debug(HttpTransportSE ht) {
        ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
        if (DEBUG_SOAP_REQUEST_RESPONSE) {
            Log.v("SOAP RETURN", "Request XML:\n" + ht.requestDump);
            Log.v("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }


    public SoapObject request(Object objectInput) {

        SoapObject request = new SoapObject(this.namespace, this.methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = isDotNet;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.addMapping(this.namespace, this.objectParameterName, objectInput.getClass());


        request.addProperty(objectParameterName, objectInput);
        //envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(this.requestURL);
        SoapObject response = null;

        debug(ht);
        try {
            ht.call(this.namespace + this.methodName, envelope);


            Object retObj = envelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
            /*    if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);*/
            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    SoapObject soapObject = (SoapObject) obj;
                    //OSAutenticar resultVariable = new OSAutenticar(j);
                    //return resultVariable;
                     return  soapObject;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }



    public  void requestAsync(final Object object){
        new AsyncTask<Void, Void, SoapObject>(){

            @Override
            protected SoapObject doInBackground(Void... params) {
                return request(object);
            }


            @Override
            protected void onPostExecute(SoapObject result)
            {
                //eventHandler.Wsdl2CodeEndedRequest();
                if (wsListener != null){
                    wsListener.onWebServiceResponse(result);
                }else{
                    Log.e("serviceclient", "NO WSListener");
                }
            }
        }.execute();
    }

}
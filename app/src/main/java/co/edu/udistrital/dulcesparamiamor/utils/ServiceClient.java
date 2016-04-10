package co.edu.udistrital.dulcesparamiamor.utils;

/**
 * Created by JulioS on 02/04/2016.
 */

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

    public ServiceClient(String requestURL, String namespace, String methodName) {
        this.requestURL = requestURL;
        this.namespace = namespace;
        this.methodName = methodName;
    }

    private final void testResponse(HttpTransportSE ht) {
        ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
        if (DEBUG_SOAP_REQUEST_RESPONSE) {
            Log.v("SOAP RETURN", "Request XML:\n" + ht.requestDump);
            Log.v("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }


    public OSAutenticar request(OEAutenticar parameters) {

        SoapObject request = new SoapObject(this.namespace, this.methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.addMapping("http://tempuri.org/", "u", new OEAutenticar().getClass());
        request.addProperty("u", parameters);
        //envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(this.requestURL);
        SoapObject response = null;

        testResponse(ht);
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
                    SoapObject j = (SoapObject) obj;
                    OSAutenticar resultVariable = new OSAutenticar(j);
                    return resultVariable;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SoapObject request(HashMap<String, Object> properties) {

        SoapObject request = new SoapObject(this.namespace, this.methodName);

        for (String key : properties.keySet()) {
            request.addProperty(key, properties.get(key));
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.addMapping(this.namespace, "response", new OSAutenticar().getClass());
        //envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        envelope.setOutputSoapObject(request);
        HttpTransportSE ht = new HttpTransportSE(this.requestURL);
        SoapObject response = null;
        ;
        try {
            testResponse(ht);
            ht.call(this.namespace + this.methodName, envelope);
            response = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
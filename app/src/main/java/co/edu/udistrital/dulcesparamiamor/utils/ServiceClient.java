package co.edu.udistrital.dulcesparamiamor.utils;

/**
 * Created by JulioS on 02/04/2016.
 *
 *
 */

import android.util.Log;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.opencv.objdetect.Objdetect;

import java.util.HashMap;

public class ServiceClient  {

    private static final boolean DEBUG_SOAP_REQUEST_RESPONSE = true;
    private static String SESSION_ID;
    private String requestURL;
    String  namespace;
    String methodName;

    public  ServiceClient(String requestURL,String  namespace, String methodName){
         this.requestURL = requestURL;
         this.namespace  = namespace;
         this.methodName = methodName;
    }
    private final void testResponse(HttpTransportSE ht) {
        ht.debug = DEBUG_SOAP_REQUEST_RESPONSE;
        if (DEBUG_SOAP_REQUEST_RESPONSE) {
            Log.v("SOAP RETURN", "Request XML:\n" + ht.requestDump);
            Log.v("SOAP RETURN", "\n\n\nResponse XML:\n" + ht.responseDump);
        }
    }

    public SoapPrimitive request(HashMap<String, Objdetect> properties) {

        SoapObject request = new SoapObject(this.namespace, this.methodName);

        for(String key : properties.keySet()){
            request.addProperty(key, properties.get(key));
        }
        // SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.implicitTypes = true;
        envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        /*MarshalFloat md = new MarshalFloat();
        md.register(envelope);
        envelope.setOutputSoapObject(request);*/

        HttpTransportSE ht = new HttpTransportSE(this.requestURL);
        SoapPrimitive response = null;
        try {
            ht.call(this.namespace + this.methodName, envelope);
            response = (SoapPrimitive)envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
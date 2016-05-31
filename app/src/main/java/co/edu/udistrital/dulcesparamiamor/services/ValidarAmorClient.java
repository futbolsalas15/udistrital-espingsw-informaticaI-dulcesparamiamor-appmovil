package co.edu.udistrital.dulcesparamiamor.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.opencv.core.Mat;
import org.opencv.objdetect.Objdetect;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.gcm.GCMClientID;
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

    private GoogleCloudMessaging gcm;
    private  String PROJECT_NUMBER;
    private  String gcmRegID;
    private PropertyInfo[] properties;
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

    public void validarAmor(PropertyInfo[] properties,String gcm_SenderId, Context applicationContext ) {
        this.properties = properties;
        getGCMRegId(gcm_SenderId,applicationContext);
        //serviceClient.requestAsync(properties);
    }

    public void getGCMRegId(final String gcm_SenderId, final Context applicationContext){

        String gcmRegIdFromFile = readFromFile(applicationContext);
        gcmRegID = gcmRegIdFromFile;
        if(gcmRegIdFromFile.equals("")){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        PROJECT_NUMBER = gcm_SenderId;
                        String scope = "GCM";
                        gcmRegID = InstanceID.getInstance(applicationContext).getToken(PROJECT_NUMBER,scope);

                        msg = "Device registered, registration ID=" + gcmRegID;
                        Log.i("GCM", msg);

                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return gcmRegID;
                }

                @Override
                protected void onPostExecute(String gcmRegID) {
                    writeToFile(gcmRegID, applicationContext);
                    GCMClientID.createGCMClientID(gcmRegID);
                    //etRegId.setText(gcmRegID);
                    PropertyInfo property = new PropertyInfo();
                    property = new PropertyInfo();
                    property.setName("idDevice");
                    property.setValue(gcmRegID);
                    property.setType(String.class);
                    properties[2] = property;
                    serviceClient.requestAsync(properties);
                }
            }.execute(null, null, null);

        }else {
           // etRegId.setText(gcmRegID);
            GCMClientID.createGCMClientID(gcmRegIdFromFile);
            PropertyInfo property = new PropertyInfo();
            property = new PropertyInfo();
            property.setName("idDevice");
            property.setValue(gcmRegID);
            property.setType(String.class);
            properties[2] = property;
            serviceClient.requestAsync(properties);
        }
    }

    private void writeToFile(String gcmRegId,Context applicationContext) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    applicationContext.openFileOutput("GCMRegID.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(gcmRegId);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context applicationContext) {

        String gcmRegId = "";

        try {
            InputStream inputStream = applicationContext.openFileInput("GCMRegID.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                gcmRegId = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("GCMClient", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("GCMClient", "Can not read file: " + e.toString());
        }
        return gcmRegId;
    }
}

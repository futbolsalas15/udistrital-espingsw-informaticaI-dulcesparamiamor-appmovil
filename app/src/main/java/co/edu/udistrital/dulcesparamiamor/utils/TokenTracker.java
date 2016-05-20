package co.edu.udistrital.dulcesparamiamor.utils;

import android.content.Context;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.GenerarTokenClient;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OEToken;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OSToken;

/**
 * Created by JulioS on 01/05/2016.
 */
public class TokenTracker {

    private static TokenTracker instance = null;
    private Context context;
    private String FILENAME = "token";
    private String token;
    protected TokenTracker(Context context) {

    }
    public static TokenTracker getInstance(Context context) {
        if(instance == null) {
            instance = new TokenTracker(context);
            context = context;
        }
        return instance;
    }


    public String getToken(Context context){
       if(token !=null && token != ""){
           return token;
       }else{
         String nToken =  getTokenFromFile(context);
         if(nToken != "" && nToken != null){
             token = nToken;
             return  token;
         }else{
             return null;
         }
       }
    }

    private  boolean fileExistance(String fname, Context context){
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    private  void saveToken(String token, Context context){

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILENAME, context.MODE_PRIVATE);
            fos.write(token.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getTokenFromFile(Context context) {
        if(fileExistance(FILENAME, context)){
            return readLocalToken(context);
        }
        return null;
    }

    public String readLocalToken(Context context){
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

         return  sb.toString();
    }

    public void requestToken(final Context context,final TokenListenter tokenListener){
            GenerarTokenClient generarTokenClient = new GenerarTokenClient(context);
            generarTokenClient.setListener(new WebServiceResponseListener() {
                @Override
                public void onWebServiceResponse(SoapObject result) {
                    OSToken osToken = new OSToken(result);
                    token = osToken.getToken();
                    Log.e("Token", token);
                    tokenListener.onTokenGerenated(token);
                    saveToken(token, context);
                }
            });
            OEToken oeToken = new OEToken();
            oeToken.setUsuario(context.getString(R.string.app_client_name));
            oeToken.setPassword(context.getString(R.string.app_client_passwod));
            generarTokenClient.generarToken(oeToken);
    }
}

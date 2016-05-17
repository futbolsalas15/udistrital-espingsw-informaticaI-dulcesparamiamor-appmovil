package co.edu.udistrital.dulcesparamiamor.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import co.edu.udistrital.dulcesparamiamor.R;

/**
 * Created by Oscar on 17/04/2016.
 */
public class GCMClient implements IGCMClient{



    private  GoogleCloudMessaging gcm;
    private  String PROJECT_NUMBER;
    private  String gcmRegID;

    @Override
    public  void getGCMRegId(final String gcm_SenderId, final Context applicationContext){

        String gcmRegIdFromFile = readFromFile(applicationContext);
        if(gcmRegIdFromFile.equals("")){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        PROJECT_NUMBER = gcm_SenderId;
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(applicationContext);
                        }
                      gcmRegID = gcm.register(PROJECT_NUMBER);
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
                }


            }.execute(null, null, null);

        }else {
            GCMClientID.createGCMClientID(gcmRegIdFromFile);
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

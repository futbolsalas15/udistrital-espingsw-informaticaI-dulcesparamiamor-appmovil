package co.edu.udistrital.dulcesparamiamor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    Button btnRegId;
    EditText etRegId;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnRegId = (Button) findViewById(R.id.btnGetRegId);
        etRegId = (EditText) findViewById(R.id.etRegId);

        btnRegId.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getRegId();
            }
        });
    }



    public void loadImagefromGallery(View view)
    {
        startActivity(new Intent(HomeActivity.this, UploadActivity.class));

    }

    public void startValidation(View view)
    {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));

    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    PROJECT_NUMBER = getResources().getString(R.string.gcm_SenderId);
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }

}

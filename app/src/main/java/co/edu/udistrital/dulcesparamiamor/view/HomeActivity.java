package co.edu.udistrital.dulcesparamiamor.view;

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

import co.edu.udistrital.dulcesparamiamor.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
          }



    public void EditLove(View view)
    {
        startActivity(new Intent(HomeActivity.this, EditLoveActivity.class));
    }

    public void startValidation(View view)
    {
        startActivity(new Intent(HomeActivity.this, CameraActivity.class));

    }
}

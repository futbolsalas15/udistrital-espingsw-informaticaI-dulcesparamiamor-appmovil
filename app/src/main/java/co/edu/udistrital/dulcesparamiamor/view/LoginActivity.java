package co.edu.udistrital.dulcesparamiamor.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.opencv.objdetect.Objdetect;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.UserProfile;
import co.edu.udistrital.dulcesparamiamor.services.AutenticarUsuarioClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    TextView lblsingup ;
    EditText Email,Password;
    Button button;
    AlertDialog.Builder builder;

    UserProfile userProfile;
    SharedPreferences mPrefs ;
    ProgressDialog prgDialog;
    AutenticarUsuarioClient autenticarUsuarioClient;
    Context currentContext;
    WebServiceResponseListener webServiceResponseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        currentContext = this;


        webServiceResponseListener  = new WebServiceResponseListener() {
            @Override
            public void onWebServiceResponse(SoapObject response) {
                String code = response.getProperty(0).toString();
                Log.e("Response",  code +  ", " + response.getProperty(1).toString() );
                if(code.equals("1")) {
                    Log.e("Response", "Autenticado " + code );
                    //showDialog(activity.getString(R.string.logintittlesuccess),activity.getString(R.string.loginsuccess),code);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("UserProfile", "");
                    prefsEditor.commit();

                    Intent intent = new Intent (LoginActivity.this,HomeActivity.class);
                    LoginActivity.this.startActivity(intent);
                } else if (code.equals("2")) {
                    Log.e("Response", "No autenticado " + code );
                    String message = response.getProperty(1).toString();
                    showDialog(LoginActivity.this.getString(R.string.notification),message,code);
                }
            }
        };



        lblsingup = (TextView)findViewById(R.id.lblsingup);

        Email = (EditText) findViewById(R.id.txtemail);
        Password = (EditText) findViewById(R.id.txtpassword);
        button = (Button) findViewById(R.id.button);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
       //Linea valida las preferencias si el usuario ya se ha logueado se obtiene el json del usuario y se redirige al Home.
        Gson gson = new Gson();
        String json = mPrefs.getString("UserProfile", "");
        if(!json.equalsIgnoreCase(""))
        {
            userProfile = gson.fromJson(json, UserProfile.class);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        //Fin Cache del usuario.

        lblsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Email.getText().toString().equals("") || Password.getText().toString().equals(""))
                {
                 builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Please fill all the fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("correo", Email.getText().toString());
                    params.put("clave", Password.getText().toString());
                    autenticarUsuarioClient = new AutenticarUsuarioClient(currentContext);
                    autenticarUsuarioClient.setListener(webServiceResponseListener);
                    autenticarUsuarioClient.execute(params);
                }

            }
        });
    }


    public void showDialog(String tittle, String message ,String code)
    {
        if(code.equals("2")) {

            builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(tittle);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // Dismiss the progress bar when application is closed
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }
}

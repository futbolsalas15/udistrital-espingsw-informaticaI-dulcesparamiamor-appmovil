package co.edu.udistrital.dulcesparamiamor.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ksoap2.serialization.SoapObject;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.UserProfile;
import co.edu.udistrital.dulcesparamiamor.services.AutenticarUsuarioClient;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OEAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OSAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.WSAutenticarUsuario;
import co.edu.udistrital.dulcesparamiamor.utils.Helper;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

import com.crashlytics.android.Crashlytics;


import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    TextView lblsingup;
    EditText Email, Password;
    Button button;
    AlertDialog.Builder builder;
    AutenticarUsuarioClient autenticarUsuarioClient;
    UserProfile userProfile;
    SharedPreferences mPrefs;
    ProgressDialog prgDialog;
    Context currentContext;
    WSAutenticarUsuario wsAutenticarUsuario;
    WebServiceResponseListener wsAutenticarResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        currentContext = this;

        autenticarUsuarioClient = new AutenticarUsuarioClient(currentContext);
        autenticarUsuarioClient.setListener(new WebServiceResponseListener() {
            @Override
            public void onWebServiceResponse(SoapObject result) {
                OSAutenticar response = new OSAutenticar(result);


                    if (response.getCodigoRespuesta() == 1) {
                        Log.e("Response", "Autenticado " + response.getMensajeRespuesta());
                        //showDialog(activity.getString(R.string.logintittlesuccess),activity.getString(R.string.loginsuccess),code);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putString("UserProfile", "");
                        prefsEditor.commit();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        LoginActivity.this.startActivity(intent);
                    } else if (response.getCodigoRespuesta() == 0) { //Se cambio a 0 posiblemente lo hallan cambiado en el servidor.
                        showDialog(LoginActivity.this.getString(R.string.notification), response.getMensajeRespuesta() != null  ? response.getMensajeRespuesta() :"Error trantado de conectar con el servidor");
                    } else {
                        showDialog(LoginActivity.this.getString(R.string.notification),"Se obtuvo una respuesta no esperada");
                    }
                }


        });

        lblsingup = (TextView) findViewById(R.id.lblsingup);

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
        if (!json.equalsIgnoreCase("")) {
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
                if (Email.getText().toString().equals("") || Password.getText().toString().equals("")) {
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
                if (!Helper.isValidEmailAddress(Email.getText().toString())) {
                    builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Incorrect mail ");
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

                    SoapObject soapObject = new SoapObject();
                    soapObject.addProperty("correo", Email.getText().toString());
                    soapObject.addProperty("clave", Password.getText().toString());
                    soapObject.addProperty("token", "");
                    OEAutenticar autenticarInput = new OEAutenticar(soapObject);
                    Log.e("Autenticar usuario","..");
                    autenticarUsuarioClient.autenticarUsuario(autenticarInput);

                }

            }
        });
    }


    public void showDialog(String tittle, String message) {
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

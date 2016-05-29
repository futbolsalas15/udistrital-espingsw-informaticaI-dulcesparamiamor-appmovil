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
import co.edu.udistrital.dulcesparamiamor.services.GenerarTokenClient;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OEAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.autenticarusuario.OSAutenticar;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OEToken;
import co.edu.udistrital.dulcesparamiamor.services.generartoken.OSToken;
import co.edu.udistrital.dulcesparamiamor.utils.Helper;
import co.edu.udistrital.dulcesparamiamor.utils.TokenListenter;
import co.edu.udistrital.dulcesparamiamor.utils.TokenTracker;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

import com.crashlytics.android.Crashlytics;


import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    TextView lblsingup;
    EditText email, password;
    Button button;
    AlertDialog.Builder builder;
    AutenticarUsuarioClient autenticarUsuarioClient;
    UserProfile userProfile;
    SharedPreferences mPrefs;
    ProgressDialog prgDialog;
    Context currentContext;
    TokenTracker tokenTracker = TokenTracker.getInstance(getBaseContext());
    String token = "";

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
                        SharedPreferences sharedpreferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();

                        prefsEditor.putString("UserProfile", "");
                        prefsEditor.putString("email", email.getText().toString());
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

        email = (EditText) findViewById(R.id.txtemail);
        password = (EditText) findViewById(R.id.txtpassword);
        button = (Button) findViewById(R.id.button);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        mPrefs =   getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
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
                token = tokenTracker.getToken(getBaseContext());
                if(token != null && !token.equals("") ){
                    if (email.getText().toString().equals("") || password.getText().toString().equals("")) {
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
                    if (!Helper.isValidEmailAddress(email.getText().toString())) {
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

                        OEAutenticar oeAutenticar= new OEAutenticar();
                        oeAutenticar.setClave(password.getText().toString());
                        oeAutenticar.setToken(token);
                        oeAutenticar.setCorreo(email.getText().toString());
                        autenticarUsuarioClient.autenticarUsuario(oeAutenticar);

                    }
                }else {
                    builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Something went wrong");
                    builder.setMessage("Tenemos problemas al conectar con el servidor intente de nuevo por favor");
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
        });

        final String mToken = tokenTracker.getToken(getBaseContext());
        if(mToken != null  && mToken !=""){
          token = mToken;
        }else{
            prgDialog = new ProgressDialog(this);
            prgDialog.show();
            tokenTracker.requestToken(getBaseContext(), new TokenListenter() {
                @Override
                public void onTokenGerenated(String newToken) {
                     if(newToken != null && !newToken.trim().equals("")  ){
                         token = mToken;
                     }
                    prgDialog.dismiss();
                }
            });
        }
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

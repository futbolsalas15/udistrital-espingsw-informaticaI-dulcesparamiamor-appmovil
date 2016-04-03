package co.edu.udistrital.dulcesparamiamor.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.UnsupportedEncodingException;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.UserProfile;
import cz.msebera.android.httpclient.Header;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    TextView lblsingup ;
    EditText Email,Password;
    Button button;
    AlertDialog.Builder builder;

    UserProfile userProfile;
    SharedPreferences mPrefs ;

    String loginurl = "http://webappjasontiw.azurewebsites.net/login.php";
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
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
                    //BackgroundTask backgroundTask = new BackgroundTask(LoginActivity.this);
                    //backgroundTask.execute("login", Email.getText().toString(), Password.getText().toString());

                    params.put("email", Email.getText().toString());
                    params.put("password",Password.getText().toString());
                    makeHTTPCall();
                }

            }
        });
    }

    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        prgDialog.setMessage(LoginActivity.this.getString(R.string.login));
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(loginurl,
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            prgDialog.hide();
                            String str = new String(responseBody, "UTF-8");
                            JsonObject(str); //Convierte respuesta en JSON y lo convierte a notificación.
                            //Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Device not connected to Internet. HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    public void JsonObject(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("server_response");

            JSONObject JO = jsonArray.getJSONObject(0);
            String code = JO.getString("code");
            String message = JO.getString("message");

            if(code.equals("login_true"))
            {
                //showDialog(activity.getString(R.string.logintittlesuccess),activity.getString(R.string.loginsuccess),code);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("UserProfile", JO.getJSONArray("object").getJSONObject(0).toString());
                prefsEditor.commit();

                Intent intent = new Intent (LoginActivity.this,HomeActivity.class);
                LoginActivity.this.startActivity(intent);
            }
            else if (code.equals("login_false"))
            {
                showDialog(LoginActivity.this.getString(R.string.notification),message,code);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String tittle, String message ,String code)
    {
        if(code.equals("login_false")) {
           // if(code.equals("reg_true"))
           //     message = RegisterLoverActivity.this.getString(R.string.registersucces);
           // else
           //     message = RegisterLoverActivity.this.getString(R.string.registerfailed);

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

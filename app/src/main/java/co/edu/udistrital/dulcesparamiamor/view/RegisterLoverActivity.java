package co.edu.udistrital.dulcesparamiamor.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.RegistrarUsuarioClient;
import co.edu.udistrital.dulcesparamiamor.services.registrarusuario.OEUsuario;
import co.edu.udistrital.dulcesparamiamor.services.registrarusuario.OSUsuario;
import co.edu.udistrital.dulcesparamiamor.utils.Helper;
import co.edu.udistrital.dulcesparamiamor.utils.TokenTracker;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;
import cz.msebera.android.httpclient.Header;

public class RegisterLoverActivity extends AppCompatActivity {
String name ,email,password; //info user


    Button buttonsingup;
    EditText loverName, loverPhone, loverEmail, loverFacebook;
    TextView ImageSelector;
    AlertDialog.Builder builder;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_GRANTED = 1;

    ProgressDialog prgDialog;
    String encodedString;
    String imgPath, fileName;
    private static int RESULT_LOAD_IMG = 1;
    SoapObject properties;
    OEUsuario oeUsuario;
    RegistrarUsuarioClient registrarUsuarioClient;
    TokenTracker tokenTracker = TokenTracker.getInstance(getBaseContext());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Activity create", "");
        setContentView(R.layout.activity_register_lover);

         registrarUsuarioClient = new RegistrarUsuarioClient(this);
         registrarUsuarioClient.setListener(new WebServiceResponseListener() {
             @Override
             public void onWebServiceResponse(SoapObject result) {
                 OSUsuario response = new OSUsuario(result);
                 if(response.getProperty(0).toString().equals("1")){
                     startActivity(new Intent(RegisterLoverActivity.this, HomeActivity.class));
                 }else if(response.getProperty(0).equals("2")){
                     showDialog("Registro", response.getProperty(1).toString());
                 }else{
                     //unknown response
                     showDialog("Registro", response.getProperty(1).toString());
                 }
             }
         });


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(PackageManager.PERMISSION_GRANTED == permissionCheck){
            Log.e("Permission", "OK");
        }else{
            Log.e("Permission", "No permission");
        }


        Bundle bundle = getIntent().getExtras();
        loverName = (EditText)findViewById(R.id.txtname);
        buttonsingup = (Button)findViewById(R.id.button);
        name = bundle.get("name").toString();
        email = bundle.get("email").toString();
        password = bundle.get("password").toString();

        ImageSelector =  (TextView)findViewById(R.id.image_selector);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        buttonsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loverName = (EditText)findViewById(R.id.txtname);
                loverPhone = (EditText)findViewById(R.id.txtphone);
                loverEmail= (EditText)findViewById(R.id.txtemail);
                loverFacebook =(EditText)findViewById(R.id.txtfacebook);

                if(loverName.getText().toString().equals("") || loverPhone.getText().toString().equals("")
                        || loverEmail.getText().toString().equals("") || loverFacebook.getText().toString().equals(""))
                {
                    builder = new AlertDialog.Builder(RegisterLoverActivity.this);
                    builder.setTitle(RegisterLoverActivity.this.getString(R.string.somethingwentwrong));
                    builder.setMessage(RegisterLoverActivity.this.getString(R.string.pleasefillallthefields));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else  if(!Helper.isValidEmailAddress(loverEmail.getText().toString())) {
                    builder = new AlertDialog.Builder(RegisterLoverActivity.this);
                    builder.setTitle(RegisterLoverActivity.this.getString(R.string.somethingwentwrong));
                    builder.setMessage("Email Invalid");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    properties = new SoapObject();
                    OEUsuario oeUsuario = new OEUsuario();
                    oeUsuario.setClave(password);
                    oeUsuario.setToken(tokenTracker.getToken(getBaseContext()));
                    oeUsuario.setCorreo(email);
                    oeUsuario.setTelefono(12345);
                    oeUsuario.setNombreAmor(loverName.getText().toString());
                    oeUsuario.setCorreoAmor(loverEmail.getText().toString());
                    oeUsuario.setTelefonoAmor(loverPhone.getText().toString());
                    oeUsuario.setFacebookAmor(loverFacebook.getText().toString());
                    registrarUsuarioClient.registrarUsuario(oeUsuario);
                }
            }
        });
    }



    public void showDialog(String tittle, String message)
    {
           builder = new AlertDialog.Builder(RegisterLoverActivity.this);
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
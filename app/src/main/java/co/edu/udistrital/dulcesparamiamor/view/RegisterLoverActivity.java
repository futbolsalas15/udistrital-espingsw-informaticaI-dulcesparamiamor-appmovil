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
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;
import cz.msebera.android.httpclient.Header;

public class RegisterLoverActivity extends AppCompatActivity {
String Name ,Email,Password; //info user


    Button buttonsingup;
    EditText LoverName,LoverPhone,LoverEmail,LoverFacebook;
    TextView ImageSelector;
    AlertDialog.Builder builder;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_GRANTED = 1;


   // String registerurl = "http://192.168.0.14/servicephp/register.php";
   String registerurl = "http://webappjasontiw.azurewebsites.net/register.php";
    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params;
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;
    SoapObject properties;
    OEUsuario oeUsuario;
    RegistrarUsuarioClient registrarUsuarioClient;
    boolean imageSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Activity create", "");
        imageSelected = false;
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
                 }
             }
         });


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        checkAndRequestPermision();
        if(PackageManager.PERMISSION_GRANTED == permissionCheck){
            Log.e("Permission", "OK");
        }else{
            Log.e("Permission", "No permission");
        }


        Bundle bundle = getIntent().getExtras();
        LoverName = (EditText)findViewById(R.id.txtname);
        buttonsingup = (Button)findViewById(R.id.button);
        Name = bundle.get("name").toString();
        Email = bundle.get("email").toString();
        Password = bundle.get("password").toString();

        ImageSelector =  (TextView)findViewById(R.id.image_selector);

        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        buttonsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoverName = (EditText)findViewById(R.id.txtname);
                LoverPhone = (EditText)findViewById(R.id.txtphone);
                LoverEmail= (EditText)findViewById(R.id.txtemail);
                LoverFacebook =(EditText)findViewById(R.id.txtfacebook);

                if(LoverName.getText().toString().equals("") || LoverPhone.getText().toString().equals("")
                        || LoverEmail.getText().toString().equals("") || LoverFacebook.getText().toString().equals(""))
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
                }
                else if (!imageSelected)
                {
                    builder = new AlertDialog.Builder(RegisterLoverActivity.this);
                    builder.setTitle(RegisterLoverActivity.this.getString(R.string.somethingwentwrong));
                    builder.setMessage(RegisterLoverActivity.this.getString(R.string.pleaseselectanimage));
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
                    OEUsuario registrarUsuarioInput = new OEUsuario();
                    properties.addProperty("nombre", Name);
                     properties.addProperty("correo", Email);
                     properties.addProperty("clave", Password);
                     properties.addProperty("telefono", 123456);
                     properties.addProperty("nombreAmor", LoverName.getText());
                     properties.addProperty("correoAmor", LoverEmail.getText());
                     properties.addProperty("telefonoAmor", LoverPhone.getText());
                     properties.addProperty("faceAmor", LoverFacebook.getText());
                     uploadImage(v);
                }
            }
        });
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }




    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Activity result", "");
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                imageSelected = true;
                Log.e("Image path", imgPath);
                cursor.close();
                //ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView
                //imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));

                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                //ImageSelector.setText("select");


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong + e", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }

    }

    // When Upload button is clicked
    public void uploadImage(View v) {
        Log.e("Upload Image", "");
        // When Image is selected from Gallery
        if (imgPath != null && !imgPath.isEmpty()) {
           // prgDialog.setMessage("Converting Image to Binary Data");
            //prgDialog.show();
            // Convert image to String using Base64
            encodeImagetoString();
            // When Image is not selected from Gallery
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "You must select image from gallery before you try to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {


            @Override
            protected String doInBackground(Void... params) {
                /*BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;

                bitmap = BitmapFactory.decodeFile(imgPath, options);*/
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
                if(bitmap != null){
                    Log.e("Bitmap", bitmap.toString());

                }else{
                    Log.e("Bitmap null", "");
                }
                Log.e("Backgroud", imgPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
            // prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                properties.addProperty("image", encodedString);
                oeUsuario = new OEUsuario(properties);
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }



    private void checkAndRequestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        READ_EXTERNAL_STORAGE_PERMISSION_GRANTED);

            }
        }else{
            Log.e("We have permssion", "");
        }
    }
    public void triggerImageUpload() {

        registrarUsuarioClient.registrarUsuario(oeUsuario);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_GRANTED: {
                Log.e("PERMISSION_GRANT", String.valueOf(grantResults.length));
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("READ PERMison GRANTED", "");
                } else {

                    Log.e("NO PERMSISSION ", "");
                }
                return;
            }

        }
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
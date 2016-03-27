package co.edu.udistrital.dulcesparamiamor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterLoverActivity extends AppCompatActivity {
String Name ,Email,Password; //info user


    Button buttonsingup;
    EditText LoverName,LoverPhone,LoverEmail,LoverFacebook;
    TextView ImageSelector;
    AlertDialog.Builder builder;

   // String registerurl = "http://192.168.0.14/servicephp/register.php";
   String registerurl = "http://webappjasontiw.azurewebsites.net/register.php";
    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lover);

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
                else if (ImageSelector.getText().equals(""))
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
                    params.put("name",Name);
                    params.put("email",Email);
                    params.put("password",Password);
                    params.put("lovename",LoverName.getText());
                    params.put("loveemail",LoverEmail.getText());
                    params.put("lovephone",LoverPhone.getText());
                    params.put("lovefacebook",LoverFacebook.getText());
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
                cursor.close();
                //ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView
                //imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));

                // Get the Image's file name
                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                // Put file name in Async Http Post Param which will used in Php web app
                ImageSelector.setText("select");
                params.put("filename", fileName);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // When Upload button is clicked
    public void uploadImage(View v) {
        // When Image is selected from Gallery
        if (imgPath != null && !imgPath.isEmpty()) {
            prgDialog.setMessage("Converting Image to Binary Data");
            prgDialog.show();
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

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath, options);
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
                prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        prgDialog.setMessage(RegisterLoverActivity.this.getString(R.string.recording));
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(registerurl,
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
            showDialog(RegisterLoverActivity.this.getString(R.string.notification),message,code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(String tittle, String message ,String code)
    {
       if(code.equals("reg_true") || code.equals("reg_false")) {
           if(code.equals("reg_true"))
               message = RegisterLoverActivity.this.getString(R.string.registersucces);
           else
               message = RegisterLoverActivity.this.getString(R.string.registerfailed);

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
package co.edu.udistrital.dulcesparamiamor.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.model.UserProfile;
import co.edu.udistrital.dulcesparamiamor.services.SendPhotoClient;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

public class EditLoveActivity extends AppCompatActivity {
    private static final int RQS_LOADIMAGE = 1;
    private Button btnLoad;

    Context currentContext;
    SharedPreferences mPrefs;
    UserProfile userProfile;
    // private ImageView imgView;
    private Bitmap myBitmap;
    SendPhotoClient sendPhotoClient;
    private EditText txtname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_love);
        btnLoad = (Button)findViewById(R.id.btnuploadimage);
        //imgView = (ImageView)findViewById(R.id.imgview);
        txtname = (EditText) findViewById(R.id.txtname);
        currentContext = this;
        btnLoad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RQS_LOADIMAGE);
            }
        });

        sendPhotoClient = new SendPhotoClient(currentContext);
        sendPhotoClient.setListener(new WebServiceResponseListener() {
            @Override
            public void onWebServiceResponse(SoapObject result) {
                //OSPhotoLove osphotolove = new OSPhotoLove(result);
                Log.e("Fotos Amor", result.getProperty(0).toString());
                Toast.makeText(getApplicationContext() ,result.getProperty(1).toString(), Toast.LENGTH_LONG).show();
            }
        });

    }  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RQS_LOADIMAGE
                && resultCode == RESULT_OK){

            if(myBitmap != null){
                myBitmap.recycle();
            }

            try {
                InputStream inputStream =
                        getContentResolver().openInputStream(data.getData());
                myBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                //  imgView.setImageBitmap(myBitmap);

                if(myBitmap == null){
                    Toast.makeText(EditLoveActivity.this,
                            "myBitmap == null",
                            Toast.LENGTH_LONG).show();
                }else{
                    int quantityfaces = detectFace();
                    if(quantityfaces==0)
                    {
                        Toast.makeText(EditLoveActivity.this,
                                "Not found a Face",
                                Toast.LENGTH_LONG).show();
                    }
                    else if(quantityfaces>1)
                    {
                        Toast.makeText(EditLoveActivity.this,
                                "Require only one face",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        //Toast.makeText(EditLoveActivity.this, "Done , we find " + quantityfaces + "Face", Toast.LENGTH_LONG).show();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap converted = myBitmap.copy(Bitmap.Config.RGB_565, false);
                        converted.compress(Bitmap.CompressFormat.JPEG,70,stream);
                        byte[] byteFormat = stream.toByteArray();
                        // get the base 64 string
                        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
                        mPrefs =   getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
                        //Linea valida las preferencias si el usuario ya se ha logueado se obtiene el json del usuario y se redirige al Home.
                        //Gson gson = new Gson();
                        String email = mPrefs.getString("email","");
                        if (!email.equalsIgnoreCase("")) {
                          //  userProfile = gson.fromJson(json, UserProfile.class);
                            PropertyInfo[] propertyinfos = new PropertyInfo[2];

                            PropertyInfo property = new PropertyInfo();
                            property.setName("img");
                            property.setValue(imgString);
                            property.setType(String.class);
                            propertyinfos[0] =property;
                            //property = new PropertyInfo();
                            //property.setName("name");
                            //property.setValue(txtname.getText().toString());
                            //property.setType(String.class);
                            //propertyinfos[1] =property;
                            property = new PropertyInfo();
                            property.setName("email");
                            property.setValue(email);
                            property.setType(String.class);
                            propertyinfos[1] =property;
                            sendPhotoClient.addPhotoLove(propertyinfos);
                        }

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*
       reference:
       https://search-codelabs.appspot.com/codelabs/face-detection
        */
    private int detectFace(){
        //Create a Canvas object for drawing on
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Detect the Faces
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .build();

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        if(faces.size()==1)
        {
            Face thisFace = faces.valueAt(0);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = thisFace.getWidth();
            float y2 = thisFace.getHeight();
            //myBitmap = Bitmap.createBitmap(myBitmap, (int)Math.round(x1), (int)Math.round(y1),(int)Math.round(x2) ,(int)Math.round(y2));
            //myBitmap = Bitmap.createScaledBitmap(myBitmap, 400, 400, true);
        }

        return faces.size();
    }
}



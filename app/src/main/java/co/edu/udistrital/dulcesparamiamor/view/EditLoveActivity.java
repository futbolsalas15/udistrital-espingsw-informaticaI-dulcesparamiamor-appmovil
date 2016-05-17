package co.edu.udistrital.dulcesparamiamor.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import co.edu.udistrital.dulcesparamiamor.R;

public class EditLoveActivity extends AppCompatActivity {
    private static final int RQS_LOADIMAGE = 1;
    private Button btnLoad;
    // private ImageView imgView;
    private Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_love);
        btnLoad = (Button)findViewById(R.id.btnuploadimage);
        //imgView = (ImageView)findViewById(R.id.imgview);

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
                        Toast.makeText(EditLoveActivity.this,
                                "Done , we find " + quantityfaces + "Face",
                                Toast.LENGTH_LONG).show();}
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

        return faces.size();
    }
}



package co.edu.udistrital.dulcesparamiamor.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.gcm.GCMClient;
import co.edu.udistrital.dulcesparamiamor.gcm.GCMClientID;
import co.edu.udistrital.dulcesparamiamor.gcm.IGCMClient;
import co.edu.udistrital.dulcesparamiamor.services.ValidarAmorClient;
import co.edu.udistrital.dulcesparamiamor.services.validaramor.OEValidarAmor;
import co.edu.udistrital.dulcesparamiamor.services.validaramor.OSValidarAmor;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraActivity extends ActionBarActivity
        implements CvCameraViewListener2 {

    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    static{ System.loadLibrary("opencv_java3"); }
    // A tag for log output.
    private static final String TAG =
            CameraActivity.class.getSimpleName();
    private boolean sendingImage = false;
    // A key for storing the index of the active camera.
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    private static final int CAMERA_PERMISSION_GRANTED = 1;
    private Mat mRgba;
    private Mat mGray;

    private int framesCount = 0;

    private DetectionBasedTracker  mNativeDetector;
    // A key for storing the index of the active image size.
    private static final String STATE_IMAGE_SIZE_INDEX =
            "imageSizeIndex";

    // An ID for items in the image size submenu.
    private static final int MENU_GROUP_ID_SIZE = 2;

    // The index of the active camera.
    private int mCameraIndex;

    // The index of the active image size.
    private int mImageSizeIndex;

    // Whether the active camera is front-facing.
    // If so, the camera view should be mirrored.
    private boolean mIsCameraFrontFacing;

    // The number of cameras on the device.
    private int mNumCameras;

    // The image sizes supported by the active camera.
    private List<Size> mSupportedImageSizes;

    // The camera view.
    private CameraBridgeViewBase mCameraView;

    // Whether the next camera frame should be saved as a photo.
    private boolean mIsPhotoPending;

    // A matrix that is used when saving photos.
    private Mat mBgr;

    // Whether an asynchronous menu action is in progress.
    // If so, menu interaction should be disabled.
    private boolean mIsMenuLocked;
    private boolean managerConneted;
    private ValidarAmorClient validarAmorClient;
    // The OpenCV loader callback.
    private BaseLoaderCallback mLoaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            Log.d(TAG, "OpenCV loaded successfully");
                            managerConneted = true;
                            initializeOpenCVDependencies();


                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };



    private void initializeOpenCVDependencies() {
        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);        Log.i(TAG, "OpenCV loaded successfully");

        // Load native library after(!) OpenCV initialization
        System.loadLibrary("detection_based_tracker");


            saveClassifier(cascadeDir , getResources().openRawResource(R.raw.fist_classifier), "fist_classifier");
            saveClassifier(cascadeDir, getResources().openRawResource(R.raw.palm_classifier), "palm_classifier");
            saveClassifier(cascadeDir, getResources().openRawResource(R.raw.thumbup_classifier), "thump_classifier");

            mNativeDetector = new DetectionBasedTracker(cascadeDir.getAbsolutePath() , 0);

            cascadeDir.delete();


    }


    private void saveClassifier(File directory, InputStream classifierStream, String fileName){

        File mCascadeFile = new File(directory, fileName);


        if(!mCascadeFile.exists()){
            //handler error here
            Log.e("OpenCVActivity", "No existe" + mCascadeFile.getAbsolutePath());
        }else{
            Log.e("OpenCVActivity", "OK Path existe : " + mCascadeFile.getAbsolutePath().toString());
        }

        try {
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[2048000];
            int bytesRead;
            while ((bytesRead = classifierStream.read(buffer)) != -1) {

                os.write(buffer, 0, bytesRead);
            }
            classifierStream.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    // Suppress backward incompatibility errors because we provide
    // backward-compatible fallbacks.
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validarAmorClient = new ValidarAmorClient(this.getBaseContext());
        validarAmorClient.setListener(new WebServiceResponseListener() {
            @Override
            public void onWebServiceResponse(SoapObject result) {
                //OSValidarAmor osValidarAmor = new OSValidarAmor(result);
                //System.out.println(result.toString());
                //Log.e("ValidarAmor", osValidarAmor.getMensajeRespuesta());
                if(result.getProperty(0)!=null)
                Toast.makeText(getApplicationContext(), result.getProperty(0).toString(), Toast.LENGTH_LONG).show();
            }
        });

        if (savedInstanceState != null){
            mCameraIndex = savedInstanceState.getInt(
                    STATE_CAMERA_INDEX, 0);
            mImageSizeIndex = savedInstanceState.getInt(
                    STATE_IMAGE_SIZE_INDEX, 0);
        } else {
            mCameraIndex = 0;
            mImageSizeIndex = 0;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
         checkAndRequestPermision();
        if(PackageManager.PERMISSION_GRANTED == permissionCheck){
            Log.e("Permission", "OK");
        }else{
            Log.e("Permission", "No permission");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_GRANTED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   cameraLoad();

                } else {

                    // No hay permisos
                }
                return;
            }

        }
    }

    private void cameraLoad() {

        Log.e(TAG, "//// Camera Load///");
        final Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Camera camera;
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.GINGERBREAD) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(mCameraIndex, cameraInfo);
            mIsCameraFrontFacing =
                    (cameraInfo.facing ==
                            CameraInfo.CAMERA_FACING_FRONT);
            mNumCameras = Camera.getNumberOfCameras();
            camera = Camera.open(mCameraIndex);
        } else { // pre-Gingerbread
            // Assume there is only 1 camera and it is rear-facing.
            mIsCameraFrontFacing = false;
            mNumCameras = 1;
            camera = Camera.open();
        }
        final Parameters parameters = camera.getParameters();
        camera.release();
        mSupportedImageSizes =
                parameters.getSupportedPreviewSizes();
        final Size size = mSupportedImageSizes.get(mImageSizeIndex);

        mCameraView = new JavaCameraView(this, mCameraIndex);
        mCameraView.setMaxFrameSize(size.width, size.height);
        mCameraView.setCvCameraViewListener(this);
        setContentView(mCameraView);
        mCameraView.enableView();

    }

    private void checkAndRequestPermision() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                       // MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        CAMERA_PERMISSION_GRANTED);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Log.e(TAG, "//////////// We have permission////");
            cameraLoad();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current camera index.
        savedInstanceState.putInt(STATE_CAMERA_INDEX, mCameraIndex);

        // Save the current image size index.
        savedInstanceState.putInt(STATE_IMAGE_SIZE_INDEX,
                mImageSizeIndex);

        super.onSaveInstanceState(savedInstanceState);
    }

    // Suppress backward incompatibility errors because we provide
    // backward-compatible fallbacks.
    @SuppressLint("NewApi")
    @Override
    public void recreate() {
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.HONEYCOMB) {
            super.recreate();
        } else {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,
                this, mLoaderCallback);
        mIsMenuLocked = false;
    }

    @Override
    public void onDestroy() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        mGray.release();
        mRgba.release();
        super.onDestroy();
    }




    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {


        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        //Escalar la imagen a 640 x 480
        org.opencv.core.Size sz = new org.opencv.core.Size(640, 480);
        Imgproc.resize( mGray , mGray, sz );

        //Mejorar el contraste
        Imgproc.equalizeHist(mGray , mGray);

        //proporcion entre la imagen original y la que se envia para ser procesada
        double factor = 1.5;
        String hands  = mNativeDetector.trackingDetectGesture(mGray);

        Log.e("Hands",hands);
        try {
//            JSONObject jsonObject = new JSONObject(hands);
            JSONArray jsonarray = new JSONArray(hands);
            if(jsonarray.length() > 0){
                framesCount++;
                if(framesCount == 20) {
                    Log.e("Frames", "20 Frames!!!");
                    framesCount = 0;
                    sendValidar(mRgba.clone());
                }
                JSONObject hand = jsonarray.getJSONObject(0);

                Imgproc.rectangle(mRgba, new Point(hand.getDouble("x") * factor, hand.getDouble("y") * factor),
                        new Point(hand.getDouble("x")* factor + hand.getDouble("width") * factor, hand.getDouble("y") * factor + hand.getDouble("height") * factor), FACE_RECT_COLOR, 3);



            }else {
                framesCount = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mIsCameraFrontFacing) {
            // Mirror (horizontally flip) the preview.
            Core.flip(mRgba, mRgba, 1);
        }


        return mRgba;
    }



    private String matImageToString(Mat image) {
        int cols = image.cols();
        int rows = image.rows();
        int elemSize = (int) image.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        image.get(0, 0, data);
        String dataString = new String(Base64.encode(data, Base64.DEFAULT));
        return dataString;
    }

   private void sendValidar(Mat rgba){
       //OEValidarAmor oeValidarAmor = new OEValidarAmor();
       IGCMClient igcmClient = new GCMClient();
       igcmClient.getGCMRegId(getResources().getString(R.string.gcm_SenderId),getApplicationContext());
       String idDevice = GCMClientID.createGCMClientID("").getGcmRegId();
       SharedPreferences sharedpreferences =  getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
       //oeValidarAmor.setEmail(sharedpreferences.getString("email", null));
       //oeValidarAmor.setImg(matImageToString(rgba));
       //oeValidarAmor.setIdDevice(idDevice);
       //validarAmorClient.validarAmor(oeValidarAmor);
       PropertyInfo[] propertyinfos = new PropertyInfo[3];

       PropertyInfo property = new PropertyInfo();
       property.setName("img");
       MatOfByte mob = new MatOfByte();
       MatOfInt moi = new MatOfInt(70);
       moi.fromArray(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, 70);
       Imgcodecs.imencode(".jpg", rgba, mob, moi);
       property.setValue(matImageToString(mob));
       property.setType(String.class);
       propertyinfos[0] = property;
       property = new PropertyInfo();
       property.setName("email");
       property.setValue(sharedpreferences.getString("email", null));
       property.setType(String.class);
       propertyinfos[1] =property;
       property = new PropertyInfo();
       property.setName("idDevice");
       property.setValue(idDevice);
       property.setType(String.class);
       propertyinfos[2] = property;
       validarAmorClient.validarAmor(propertyinfos);
   }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.e(TAG, "////// Create Menu ////////");
        getMenuInflater().inflate(R.menu.activity_camera, menu);
        if (mNumCameras < 2) {
            // Remove the option to switch cameras, since there is
            // only 1.
            menu.removeItem(R.id.menu_next_camera);
        }
        return true;
    }

    // Suppress backward incompatibility errors because we provide
    // backward-compatible fallbacks (for recreate).
    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mIsMenuLocked) {
            return true;
        }
        if (item.getGroupId() == MENU_GROUP_ID_SIZE) {
            mImageSizeIndex = item.getItemId();
            recreate();

            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_next_camera:
                mIsMenuLocked = true;
                mIsMenuLocked = true;

                // With another camera index, recreate the activity.
                mCameraIndex++;
                if (mCameraIndex == mNumCameras) {
                    mCameraIndex = 0;
                }
                mImageSizeIndex = 0;
                recreate();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

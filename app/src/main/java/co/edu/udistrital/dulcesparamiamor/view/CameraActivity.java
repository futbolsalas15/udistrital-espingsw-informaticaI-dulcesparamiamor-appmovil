package co.edu.udistrital.dulcesparamiamor.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.ksoap2.serialization.SoapObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import co.edu.udistrital.dulcesparamiamor.R;
import co.edu.udistrital.dulcesparamiamor.services.ValidarAmorClient;
import co.edu.udistrital.dulcesparamiamor.services.validaramor.OEValidarAmor;
import co.edu.udistrital.dulcesparamiamor.services.validaramor.OSValidarAmor;
import co.edu.udistrital.dulcesparamiamor.utils.WebServiceResponseListener;


// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraActivity extends ActionBarActivity
        implements CvCameraViewListener2 {

    static{ System.loadLibrary("opencv_java3"); }
    // A tag for log output.
    private static final String TAG =
            CameraActivity.class.getSimpleName();
    private boolean sendingImage = false;
    // A key for storing the index of the active camera.
    private static final String STATE_CAMERA_INDEX = "cameraIndex";
    private static final int CAMERA_PERMISSION_GRANTED = 1;

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
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
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
                            mBgr = new Mat();

                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };



    private void initializeOpenCVDependencies() {


        try {
            // Copy the resource into a temp file so OpenCV can load it
          /*  InputStream is = getResources().openRawResource(R.raw.palm);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "palm.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);*/
            if (cascadeClassifier == null) {
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);

                InputStream is = getResources().openRawResource(R.raw.palm);
                File mCascadeFile = new File(cascadeDir, "palm.xml");


                if(!mCascadeFile.exists()){
                    //handler error here
                    Log.e("OpenCVActivity", "No existe" + mCascadeFile.getAbsolutePath());
                }else{
                    Log.e("OpenCVActivity", "OK Path existe : " + mCascadeFile.getAbsolutePath().toString());
                }


                FileOutputStream os = new FileOutputStream(mCascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();


                // cascadeDir.delete();

                cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                cascadeClassifier.load(mCascadeFile.getAbsolutePath());
                // Load the cascade classifier
                if (cascadeClassifier.empty()) {
                    //handler error here
                    Log.e("OpenCVActivity", "No Classifier path" + mCascadeFile.getAbsolutePath().toString());
                }else{
                    Log.e("OpenCVActivity", "OK Path: " + mCascadeFile.getAbsolutePath().toString());
                }

                // cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            }


        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
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
                OSValidarAmor osValidarAmor = new OSValidarAmor(result);
                Log.e("ValidarAmor", osValidarAmor.getMensajeRespuesta());
            }
        });

        if (savedInstanceState != null) {
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
        super.onDestroy();
    }




    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();

        // Create a grayscale image
        Imgproc.cvtColor(rgba, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
        MatOfRect hands = new MatOfRect();


        // Use the classifier to detect hands
        if (cascadeClassifier != null && !cascadeClassifier.empty()) {
            cascadeClassifier.detectMultiScale(grayscaleImage, hands, 1.1, 2, 2,
                    //  new Size(absoluteHandSize, absoluteHandSize), new Size());
                    new org.opencv.core.Size(200 , 200), new org.opencv.core.Size(300,300));
        }


        Rect[] handsArray = hands.toArray();
        //if there are any hands validate love
        if(handsArray.length > 0 && validarAmorClient != null && sendingImage == false){
            sendingImage = true;
            OEValidarAmor oeValidarAmor = new OEValidarAmor();
            SharedPreferences sharedpreferences =   getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
            oeValidarAmor.setCorreo(sharedpreferences.getString("email", null));
            oeValidarAmor.setImagenAmor(matImageToString(rgba));
            validarAmorClient.validarAmor(oeValidarAmor);

        }

        // If there are any hands found, draw a rectangle around it
        for (int i = 0; i < handsArray.length; i++) {
            Log.e("OpenCVActivity", "Palms" + handsArray.toString());
            Imgproc.rectangle(rgba, handsArray[i].tl(), handsArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        }

        if (mIsCameraFrontFacing) {
            // Mirror (horizontally flip) the preview.
            Core.flip(rgba, rgba, 1);
        }

        return rgba;
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

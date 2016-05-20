package co.edu.udistrital.dulcesparamiamor.utils;

import android.os.Environment;

import org.opencv.core.Mat;

import java.nio.charset.Charset;

/**
 * Created by juliosaldana on 5/19/16.
 */
public class GestureDetector {

    public GestureDetector() {

    }


    public native String detectGesture(Mat image);
    public native float createDetector(String path);

    static {
        System.loadLibrary("gesture-detection");
    }

}

package org.opencv.samples.facedetect;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class DetectionBasedTracker
{
    public DetectionBasedTracker(String classifierFolder, int minFaceSize) {
        mGestureDetector = nativeCreateGestureDetector(classifierFolder);
    }

    public void start() {
        nativeStart(mNativeObj);
    }

    public void stop() {
        nativeStop(mNativeObj);
    }

    public void setMinFaceSize(int size) {
        nativeSetFaceSize(mNativeObj, size);
    }

    public void detect(Mat imageGray, MatOfRect faces) {
        nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
    }

    public void detectGesture(Mat imageGray) {
        nativeDetectGesture(mGestureDetector, imageGray.getNativeObjAddr());
    }

    public String trackingDetectGesture(Mat imageGray) {
      return  nativeTrackingDetectGesture(mGestureDetector, imageGray.getNativeObjAddr());
    }

    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }

    private long mNativeObj = 0;
    private long mGestureDetector = 0;

    private static native long nativeCreateObject(String cascadeName, int minFaceSize);
    private static native long nativeCreateGestureDetector(String classiferFolder);
    private static native void nativeDestroyObject(long thiz);
    private static native void nativeStart(long thiz);
    private static native void nativeStop(long thiz);
    private static native void nativeSetFaceSize(long thiz, int size);
    private static native void nativeDetect(long thiz, long inputImage, long faces);
    private static native void nativeDetectGesture(long thiz, long inputImage);
    private static native String nativeTrackingDetectGesture(long thiz, long inputImage);
}

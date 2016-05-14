#include <string.h>
#include <jni.h>
#include "gesture_manager.h"
#include "opencv2/opencv.hpp"
#include <unistd.h>
#include "iostream"
#include <math.h>
#include <string>
#include <stdio.h>
#include <fstream>
#include <sstream>

using namespace cv;
using namespace std;

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
      Gesture g();
      //VideoCapture camera = VideoCapture(0);
      //Gesture g;

      g.set_show(false);
      g.set_draw(false);
      g.set_one_hand(false);
      Mat frame;
      std::vector<Detected_Object> det_obj;
      vector<double> times;

    return env->NewStringUTF("Hello from c++");
}
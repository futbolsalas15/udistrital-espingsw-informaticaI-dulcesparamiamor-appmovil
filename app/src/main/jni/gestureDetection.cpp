//
// Created by Julio Saldaña on 5/19/16.
//

#include "gestureDetection.h"


#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace std;
using namespace cv;
//using namespace nlohmann;

Gesture* gesture;
JNIEXPORT jfloat JNICALL Java_co_edu_udistrital_dulcesparamiamor_utils_GestureDetector_createDetector(JNIEnv * jenv, jclass, jstring path){
      jfloat result = 0;
    try{

        const char *s = jenv->GetStringUTFChars(path,NULL);
        std::string directory=s;
        gesture = new  Gesture(directory);
        gesture->set_show(false);
        gesture->set_draw(false);
        gesture->set_time_to_count_detection(2);
        gesture->set_time_to_discard_detection(2);
        gesture->set_one_hand(true);
        gesture->set_frame_flip(false);

    }catch(cv::Exception& e){
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }catch (...){
        LOGD("nativeCreateObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
        return 0;
    }

    return result;
}




JNIEXPORT jstring JNICALL Java_co_edu_udistrital_dulcesparamiamor_utils_GestureDetector_detectGesture
(JNIEnv * jenv, jclass, jlong imageGray)
{
    //LOGD("Java_com_admobilize_android_adbeacon_mobile_util_facedetection_DetectionBasedTracker_nativeDetectFace Mat rows");
    std::string result="";
    Detected_Object detectionResult;
  try{
     Mat &mGr = *(Mat *)imageGray;
     Mat frame= Mat();
     cvtColor(mGr, frame, COLOR_BGRA2BGR);
     LOGE("Rows Mat = %d", frame.rows);
     LOGE("Cols Mat = %d", frame.cols);
     result = parseResults(gesture->detect(1));
    // mGr=frame;
     return (jenv)->NewStringUTF((const char *) result.c_str());

  }catch(cv::Exception& e){
     LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
     jclass je = jenv->FindClass("org/opencv/core/CvException");
     if(!je)
     je = jenv->FindClass("java/lang/Exception");
     jenv->ThrowNew(je, e.what());
  }
  catch (...)
   {
     LOGD("nativeDetect caught unknown exception");
     jclass je = jenv->FindClass("java/lang/Exception");
     jenv->ThrowNew(je, "Unknown exception in JNI code DetectionBasedTracker.nativeDetectFace()");
  }
    //LOGD("Java_com_admobilize_android_adbeacon_mobile_util_facedetection_DetectionBasedTracker_nativeDetectFace end");
    //return (jenv)->NewStringUTF((const char *) result.c_str());
    return (jenv)->NewStringUTF("detect");
}




std::string parseResults( std::vector<Detected_Object> results){
  /*json jsonArray;
  Detected_Object det;
  json jsonResults ;
  if(results.size() > 0){
    for(int i = 0 ;  i < results.size(); i++){
      jsonArray.push_back(getDetectionJson(results[i]));
    }
    jsonResults["results"] = jsonArray;
  }else{
   jsonResults = json::parse("{}");
  }

  return jsonResults.dump();*/
  string result = "[";
   for(int i = 0 ;  i < results.size(); i++){
        result += formatDetection(results[i]);
        if(i < results.size() -1){
         result += ",";
        }
   }
   return result + "]";
}


std::string formatDetection(Detected_Object detectionObject){
char* x = new char[100];
sprintf(x, "{'id':%d, 'x':%d, 'y':%d, 'hand_type':%d, 'width': %d, 'height': %d}",
 detectionObject.id_, detectionObject.x_, detectionObject.y_, detectionObject.hand_type_, detectionObject.width_, detectionObject.height_);

std::string str = x;
return str;
}


/*std::string getDetectionJson(Detected_Object detectionObject){
  json resultsObject;
  resultsObject["id"]  = detectionObject.id_;
  resultsObject["x"] = detectionObject.x_;
  resultsObject["y"] = detectionObject.y_;
  resultsObject["hand_type"] = detectionObject.hand_type_ ;
  resultsObject["width"] = detectionObject.width_;
  resultsObject["height"] = detectionObject.height_;
  resultsObject["xc"] = detectionObject.xc_;
  resultsObject["yc"] = detectionObject.yc_;


  return resultsObject;
}

namespace std {
    template<typename T>
   inline long double strtold(const char* nptr,char** endptr){
    return strtod(nptr, endptr);
   }


namespace std {
  template<typename T>
   inline  string to_string(size_t n) {
        ostringstream s;
        s << n;
        return s.str();
    }
}
*/

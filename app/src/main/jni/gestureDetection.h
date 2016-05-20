//
// Created by Julio Saldaña on 5/19/16.
//

#ifndef UDISTRITAL_ESPINGSW_INFORMATICAI_DULCESPARAMIAMOR_APPMOVIL_GESTUREDETECTION_H
#define UDISTRITAL_ESPINGSW_INFORMATICAI_DULCESPARAMIAMOR_APPMOVIL_GESTUREDETECTION_H
#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>
#include <string>
#include <vector>
#include <android/log.h>
#include "gesture_manager.h"
//#include "json.hpp"
#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jstring JNICALL  Java_co_edu_udistrital_dulcesparamiamor_utils_GestureDetector_detectGesture (JNIEnv *, jclass, jlong);


JNIEXPORT jfloat JNICALL Java_co_edu_udistrital_dulcesparamiamor_utils_GestureDetector_createDetector(JNIEnv *, jclass, jstring);


std::string formatDetection(Detected_Object detectionObject);
std::string parseResults( std::vector<Detected_Object> results);

//inline long double strtold(const char* nptr,char** endptr);

//std::string getDetectionJson(Detected_Object detectionObject);

#ifdef __cplusplus
}
#endif

#endif //UDISTRITAL_ESPINGSW_INFORMATICAI_DULCESPARAMIAMOR_APPMOVIL_GESTUREDETECTION_H

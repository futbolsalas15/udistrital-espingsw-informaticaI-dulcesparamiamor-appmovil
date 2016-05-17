LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE    := libgesture
LOCAL_SRC_FILES := libgesture.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libgesture
LOCAL_SRC_FILES := libgesture.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ../../../OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
LOCAL_MODULE    := gesture-ai
LOCAL_SRC_FILES := main.cpp
LOCAL_STATIC_LIBRARIES := libgesture
include $(BUILD_SHARED_LIBRARY)
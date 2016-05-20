LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := gesture-detection
LOCAL_SHARED_LIBRARIES += opencv_java3
LOCAL_SHARED_LIBRARIES += gesture-ai


LOCAL_SRC_FILES :=  gestureDetection.cpp

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += include ${LOCAL_PATH}/include/gesture-src  {LOCAL_PATH}/include/gesture-src/libs
LOCAL_LDLIBS     += -llog -ldl -lm -latomic -lz
LOCAL_CPPFLAGS += -std=c++11  \
                  -ljnigraphics  \
                  -DHAVE_PTHREAD   \
                  -D_LINUX  -DBOOST_HAS_PTHREADS  \
                  -I${LOCAL_PATH}/include  \
                  -I${LOCAL_PATH}/include/gesture-src  \
                   -I${LOCAL_PATH}/include/gesture-src/libs  \
                  -fexceptions


include $(BUILD_SHARED_LIBRARY)


#############################
# Prebuilt shared libraries #
#############################

include $(CLEAR_VARS)
LOCAL_MODULE := opencv_java3
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libopencv_java3.so
LOCAL_C_INCLUDES += include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gesture-ai
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libgesture.so
include $(PREBUILT_SHARED_LIBRARY)


#ifndef GESTUREMANAGERIMPL_H
#define GESTUREMANAGERIMPL_H

#include "opencv2/opencv.hpp"
#include "facedetect.hpp"
#include "motiondetector.hpp"
#include "uniqueness2.hpp"
#include "gesture_manager.h"
#include <list>

class GestureImpl {
public:
    //Constructors
    GestureImpl();
    GestureImpl(int camera);
    GestureImpl(std::string directory);
    cv::Mat frame_;
    cv::VideoCapture camera_;

    //Methods
    std::vector<Detected_Object> detect(int hand_type);
    std::vector<Detected_Object> detect(cv::Mat &frame, int hand_type);

    std::vector<Detected_Object> tracking_detect(std::vector<int> hand_types);
    std::vector<Detected_Object> tracking_detect(cv::Mat &frame, std::vector<int> hand_types);

    //Settter methods
    void show_frame();
    void set_time_to_count_detection(double time);
    void set_time_to_discard_detection(double time);
    void set_one_hand(bool one_hand);
    void set_draw(bool draw);
    void set_show(bool show);
    void set_kalman(bool kalman);
    void set_frame_flip(bool flip_frame);
    void detection_quality(int quality);
    void stop_video_capture();

    //Used only for node-SDK
    void show_detection_frame();

private:
    //debug
    bool debug;
    bool one_hand_ = false;
    bool drawing_ = false;
    bool show_ = false;
    bool kalman_ = true;
    bool flip_frame_ = false;
    float stride_ = 0.08;
    double resize_scale_;
    cv::Mat draw_frame_;

    void init();
    void init(std::string path);
    std::vector<Detected_Object> run_detect(cv::Mat &frame, int hand_type);
    std::vector<Detected_Object> run_tracking_detect(cv::Mat &frame, std::vector<int> hand_types);
    std::vector<Detected_Object> assign_detections(int hand_type, bool tracking);

    void update_tracking(std::vector<cv::Rect>& rect_array, int hand_type, trackedObject& obj);
    void draw(cv::Mat &frame, Uniqueness2 *tracker);
    void draw(cv::Mat &frame, std::vector<cv::Rect> &fs);
    bool calc_resize(cv::Mat &frame, cv::Mat &resized_frame);
    void rect_scale(cv::Rect &r);

    //Clock methods
    void setFrameRate(double newFrameRate);
    double calculateAverage(std::list<double> collection);
    int getFrameCount(double frameRate, double seconds);
    double getCurrentClock();
    double durationClockPerSecond(double startTime, double endTime);
    double frameRateManualUpdate();

    //Tracking and Detection Classes and Vectors
    std::vector< std::vector<cv::Rect> > vec_detection_rects_;
    std::vector<PicoDetector*> vec_PicoDetectors_;
    std::vector<Uniqueness2*> vec_trackers_;

    //Detection Variables and
    double secondsToCountDet_ = 0.3;
    double secondsToDiscardDet_ = 2;
    std::list<double> frameRateCollection_;
    int frameAverageCollectionLimit_ = 5;
    bool useAutoFrameRate_ = true;
    double frameRate_ = 1;
    int framesToCountDet_, framesToDiscardDet_, framesToNoDrawDet_;
    double endFrameClock_ = 0;
    double startFrameClock_ = 0;
    double secondsPerFrame_ = 1;

};

#endif // GESTUREMANAGERIMPL_H

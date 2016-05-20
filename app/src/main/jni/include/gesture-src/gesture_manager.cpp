#include "gesture_manager.h"
#include "gesture_manager_impl.h"
#ifdef WINDOWS
    #include <direct.h>
    #define GetCurrentDir _getcwd
#else
    #include <unistd.h>
    #define GetCurrentDir getcwd
 #endif

/*----------------Detection Object------------------*/
Detected_Object::Detected_Object(){

}

Detected_Object::Detected_Object(int x, int y, int width, int height, int hand_type, int id)
{
    this->x_ = x;
    this->y_ = y;
    this->width_ = width;
    this->height_ = height;
    this->hand_type_ = hand_type;
    this->xc_ = x + width/2.0;
    this->yc_ = y + height/2.0;
    this->id_ = id;

}

Detected_Object::~Detected_Object(){

}


/*----------------Gesture Manager------------------*/

Gesture::Gesture(): impl_(new GestureImpl())
{

}

Gesture::Gesture(std::string directory): impl_(new GestureImpl(directory))
{

}


Gesture::Gesture(int camera): impl_(new GestureImpl(camera))
{
    this->camera_ = &impl_->camera_;
    this->frame_ = &impl_->frame_;
}

Gesture::~Gesture() {delete impl_; impl_ = 0;}




/**********************************************************************************/
//----------------------Detection methods and submethods-------------------------//
/********************************************************************************/

std::vector<Detected_Object> Gesture::detect(int hand_type){
   return impl_->detect(hand_type);
}

std::vector<Detected_Object> Gesture::detect(cv::Mat &frame, int hand_type){
    return(impl_->detect(frame, hand_type));

    //return(run_detect(frame, hand_type));
}



std::vector<Detected_Object> Gesture::tracking_detect(std::vector<int> hand_types){
    return(impl_->tracking_detect(hand_types));

    /*
    if(!camera_.isOpened()){
        std::cout << "Camera is not opened" << std::endl;
        std::vector<Detected_Object> zero;
        return(zero);
    }else{
        camera_ >> frame_;
        return(run_tracking_detect(frame_, hand_types));
    }*/
}

std::vector<Detected_Object> Gesture::tracking_detect(cv::Mat &frame, std::vector<int> hand_types){
    return(impl_->tracking_detect(frame, hand_types));
    //return(run_tracking_detect(frame, hand_types));
}



void Gesture::show_frame(){
    impl_->show_frame();
    /*
    if(frame_.empty()){
        std::cout << "No Frame" << std::endl;
    }else{
        if(flip_frame_) flip(frame_, frame_, 1);
        cv::imshow("Show Frame", frame_);
        if(flip_frame_) flip(frame_,frame_,1);
        cv::waitKey(1);
    }*/
}

void Gesture::show_detection_frame(){
    impl_->show_detection_frame();
}

void Gesture::set_time_to_count_detection(double time){
    impl_->set_time_to_count_detection(time);
    //secondsToCountDet_ = time;
}

void Gesture::set_time_to_discard_detection(double time){
    impl_->set_time_to_discard_detection(time);
    //secondsToDiscardDet_ = time;
}

void Gesture::set_one_hand(bool one_hand){
    impl_->set_one_hand(one_hand);
    //this->one_hand_ = one_hand;
}

void Gesture::set_draw(bool draw){
    impl_->set_draw(draw);
    //this->drawing_ = draw;
}

void Gesture::set_show(bool show){
    impl_->set_show(show);
    //this->show_ = show;
}

void Gesture::set_kalman(bool kalman){
    impl_->set_kalman(kalman);
    //this->kalman_ = kalman;
}

void Gesture::set_frame_flip(bool flip_frame){
    impl_->set_frame_flip(flip_frame);
    //this->flip_frame_ = flip_frame;
}

void Gesture::detection_quality(int quality)
{
   impl_->detection_quality(quality);
}

void Gesture::stop_video_capture(){
    impl_->stop_video_capture();
  /*if(!camera_.isOpened()){
     camera_.release();
  }*/
}

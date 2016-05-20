#include "gesture_manager_impl.h"

#ifdef WINDOWS
    #include <direct.h>
    #define GetCurrentDir _getcwd
#else
    #include <unistd.h>
    #define GetCurrentDir getcwd
 #endif
/*----------------Gesture Manager------------------*/


cv::Mat grayFrame;
MotionDetector motionDetector(&grayFrame);

GestureImpl::GestureImpl()
{
    //initialize standard vars
    init();
}

GestureImpl::GestureImpl(int camera)
{
    this->camera_ = cv::VideoCapture(camera);

    //initialize standard vars
    init();
}

GestureImpl::GestureImpl(std::string directory)
{


    //initialize standard vars
    init(directory);
}

void GestureImpl::init(std::string path){
    motionDetector.sample_step = 5;
    motionDetector.wb_threshold = 20;

    //Make PicoDetectors and arrays to store their detections
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[0]->load_cascade(path + "/palm_classifier",0,0);
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[1]->load_cascade(path + "/fist_classifier",0,0);
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[2]->load_cascade(path + "/thumbup_classifier",0,0);

    //Create a vector of rect for each PicoDetector for dumping detections
    vec_detection_rects_ = std::vector< std::vector<cv::Rect> >(vec_PicoDetectors_.size());
    for(int i = 0; i < vec_PicoDetectors_.size(); ++i){
        vec_trackers_.push_back(new Uniqueness2(1,60));
        vec_trackers_[i]->deals_with_occlusion = false;
        vec_trackers_[i]->gesture_ = true;
    }

    debug = false;

}



void GestureImpl::init(){
    motionDetector.sample_step = 5;
    motionDetector.wb_threshold = 20;

    //get current absolute path
   char cCurrentPath[FILENAME_MAX];
    if (!GetCurrentDir(cCurrentPath, sizeof(cCurrentPath)))
     {
        std::cout << "Error  " << cCurrentPath << std::endl;
    }
    //std::string path = std::string(cCurrentPath);

    std::string path;
    //Get executable location
    char buff[PATH_MAX];
    ssize_t len = ::readlink("/proc/self/exe", buff, sizeof(buff)-1);
    if (len != -1) {
        buff[len] = '\0';
        path = std::string(buff);
    }else{
        std::cout << "Error file path. Does system have proc? Path: " << buff << std::endl;
    }

    //Remove from end of string till we reach a "/"
    std::size_t found = path.find_last_of("/");
    std::string new_path = path.substr(0,found);


    //Make PicoDetectors and arrays to store their detections
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[0]->load_cascade(new_path + "/classifiers/palm_classifier",0,0);
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[1]->load_cascade(new_path + "/classifiers/fist_classifier",0,0);
    vec_PicoDetectors_.push_back(new PicoDetector(&grayFrame,60,200,15));
    vec_PicoDetectors_[2]->load_cascade(new_path + "/classifiers/thumbup_classifier",0,0);

    //Create a vector of rect for each PicoDetector for dumping detections
    vec_detection_rects_ = std::vector< std::vector<cv::Rect> >(vec_PicoDetectors_.size());
    for(int i = 0; i < vec_PicoDetectors_.size(); ++i){
        vec_trackers_.push_back(new Uniqueness2(1,60));
        vec_trackers_[i]->deals_with_occlusion = false;
        vec_trackers_[i]->gesture_ = true;
    }

    debug = false;

}


int GestureImpl::getFrameCount(double frameRate, double seconds){
    return floor(seconds/(1/frameRate));
}

double GestureImpl::durationClockPerSecond(double startTime, double endTime){
    return endTime - startTime;
}

double GestureImpl::getCurrentClock(){
    return (double)cv::getTickCount()/cv::getTickFrequency();
}

void GestureImpl::setFrameRate(double newFrameRate){
    useAutoFrameRate_ = false;
    frameRate_ = newFrameRate;
    framesToCountDet_ = getFrameCount(frameRate_, secondsToCountDet_);
    framesToDiscardDet_ = getFrameCount(frameRate_, secondsToDiscardDet_);
    framesToNoDrawDet_ = getFrameCount(frameRate_, 0.5);
}


double GestureImpl::calculateAverage(std::list<double> collection){
    double average = 0;
    for (std::list<double>::iterator it = collection.begin(); it != collection.end(); it++){
        average = (average + *it);
    }
    average = ( average / collection.size() );
    return average;
}

double GestureImpl::frameRateManualUpdate(){
    if(endFrameClock_ == 0){
        endFrameClock_ = getCurrentClock();
        startFrameClock_ = endFrameClock_;
        frameRate_ = 1;
    } else {
        startFrameClock_ = endFrameClock_;
        endFrameClock_ = getCurrentClock();
        frameRateCollection_.push_back(durationClockPerSecond(startFrameClock_, endFrameClock_));

        if(frameRateCollection_.size() > frameAverageCollectionLimit_){
            frameRateCollection_.pop_front();
        }

        secondsPerFrame_ = calculateAverage(frameRateCollection_);
        frameRate_ = 1/secondsPerFrame_;
    }
    framesToCountDet_= getFrameCount(frameRate_, secondsToCountDet_);
    framesToDiscardDet_ = getFrameCount(frameRate_, secondsToDiscardDet_);
    framesToNoDrawDet_ = getFrameCount(frameRate_, 0.5);
    return frameRate_;
}

void GestureImpl::detection_quality(int quality){
    if(debug) std::cout<<"detection_quality"<<std::endl;

    if(quality > 0){
        //High
        stride_ = 0.08;
    }
    if(quality == 0 ){
        //Medium
        stride_ = 0.13;
    }
    if(quality < 0){
        //Low
        stride_ = 0.16;
    }

}

bool GestureImpl::calc_resize(cv::Mat &frame, cv::Mat &resized_frame){
    int x = frame.cols;
    int y = frame.rows;
    int area = 350000;
    //cout << "-----------------CALCULATE RESIZE-----------------" << endl;
    while(x*y > area){
        //cout << "x*y: " << x*y << endl;
        x -= 20;
        //cout << "x - 20: " << x << endl;
        resize_scale_ = x/((double)(frame.cols));
        //cout << "resize scale: " << resize_scale_ << endl;
        //cout << "y: " << y << endl;
        y = frame.rows * resize_scale_;
        //cout << "resized y: " << y << endl;
    }

    if(x != frame.cols){
        cv::resize(frame, resized_frame, cv::Size(x,y));
        return true;
    }else{
        resize_scale_ = 1;
        return false;
    }

}

/**********************************************************************************/
//----------------------Detection methods and submethods-------------------------//
/********************************************************************************/

std::vector<Detected_Object> GestureImpl::detect(int hand_type){
    if(!camera_.isOpened()){
        std::cout << "Camera is not opened" << std::endl;

        //TODO throw exception and


        std::vector<Detected_Object> zero;
        return(zero);
    }else{
        camera_ >> frame_;
        return(run_detect(frame_, hand_type));
    }
}

std::vector<Detected_Object> GestureImpl::detect(cv::Mat &frame, int hand_type){
    return(run_detect(frame, hand_type));
}

std::vector<Detected_Object> GestureImpl::run_detect(cv::Mat &frame, int hand_type){
    if(debug) std::cout<<"single_detection"<<std::endl;

    cv::Mat resized_frame;
    if(calc_resize(frame, resized_frame)){
        cv::cvtColor(resized_frame, grayFrame, cv::COLOR_BGR2GRAY);
    }else{
       cv:: cvtColor(frame, grayFrame, cv::COLOR_BGR2GRAY);
    }

    std::vector<cv::Rect> frameRect = {cv::Rect(0,0,grayFrame.cols,grayFrame.rows)};
    vec_detection_rects_[hand_type] = vec_PicoDetectors_[hand_type]->run_smartscales_in(frameRect,stride_);

    //Draw
    //cv::Mat draw_frame_;
    draw_frame_ = frame.clone();
    if(drawing_) draw(draw_frame_, vec_detection_rects_[hand_type]);

    if(show_){
        if(flip_frame_) flip(draw_frame_, draw_frame_, 1);
        imshow("Detect", draw_frame_);
        if(flip_frame_) flip(draw_frame_, draw_frame_,1);
        cv::waitKey(1);
    }


    return(assign_detections(hand_type, false));
}

std::vector<Detected_Object> GestureImpl::assign_detections(int hand_type, bool tracking){
    if(debug) std::cout<<"assign_detections"<<std::endl;
    std::vector<Detected_Object> objs;

    if(tracking){
        for(int i = 0; i < vec_trackers_[hand_type]->objects.size(); ++i){
            if(vec_trackers_[hand_type]->objects[i].face_verified){
                cv::Rect r;
                if(kalman_){
                    r = vec_trackers_[hand_type]->objects[i].rectWithFilter;
                    rect_scale(r);
                }else{
                    r = vec_trackers_[hand_type]->objects[i].rect;
                    rect_scale(r);
                }
                objs.push_back(Detected_Object(r.x,r.y,r.width,r.height,vec_trackers_[hand_type]->objects[i].hand_,vec_trackers_[hand_type]->objects[i].id));
            }
        }
    }else{
        for(int i = 0; i < vec_detection_rects_[hand_type].size(); ++i){
            cv::Rect r = vec_detection_rects_[hand_type][i];
            rect_scale(r);
            objs.push_back(Detected_Object(r.x,r.y,r.width,r.height,hand_type,-1));
        }
        return objs;
    }
}

std::vector<Detected_Object> GestureImpl::tracking_detect(std::vector<int> hand_types){
    if(!camera_.isOpened()){
        std::cout << "Camera is not opened" << std::endl;
        std::vector<Detected_Object> zero;
        return(zero);
    }else{
        camera_ >> frame_;
        return(run_tracking_detect(frame_, hand_types));
    }
}

std::vector<Detected_Object> GestureImpl::tracking_detect(cv::Mat &frame, std::vector<int> hand_types){
    return(run_tracking_detect(frame, hand_types));
}

std::vector<Detected_Object> GestureImpl::run_tracking_detect(cv::Mat &frame, std::vector<int> hand_types){
    if(debug) std::cout<<"tracking_detection_alt(alt_type1,alt_type2)"<<std::endl;

    //Bounds checking for input hands vector
    std::vector<Detected_Object> zero;
    if(hand_types.size() > 3)
        return(zero);
    if(hand_types.size() == 0)
        return(zero);

    setFrameRate(frameRateManualUpdate());
    cv::Mat resized_frame;
    if(calc_resize(frame, resized_frame)){
        cv::cvtColor(resized_frame, grayFrame, cv::COLOR_BGR2GRAY);
    }else{
        cv::cvtColor(frame, grayFrame, cv::COLOR_BGR2GRAY);
    }

    //Detector 1
    motionDetector.detect(vec_PicoDetectors_[hand_types[0]]->scales.maxv);
    vec_detection_rects_[hand_types[0]] = vec_PicoDetectors_[hand_types[0]]->run_smartscales_in(motionDetector.cluster_rects, stride_);
    vec_trackers_[hand_types[0]]->set_frame_counters(framesToCountDet_, framesToDiscardDet_, framesToNoDrawDet_, -1);
    vec_trackers_[hand_types[0]]->push_frame_rects(vec_detection_rects_[hand_types[0]], frame);
    //Uniquness2 tracker can't update original because it can't see gesture_manager so need to set manually here
    for(int i = 0; i < vec_trackers_[hand_types[0]]->objects.size(); ++i){
        if(vec_trackers_[hand_types[0]]->objects[i].in_curr_frame){
            vec_trackers_[hand_types[0]]->objects[i].hand_ = hand_types[0];
        }
    }
    vec_trackers_[hand_types[0]]->check_missed_in(motionDetector.cluster_rects);

    //Keep only 1 object
    if(one_hand_){
        while(vec_trackers_[hand_types[0]]->objects.size() > 1){
            vec_trackers_[hand_types[0]]->objects.pop_back();
        }
    }

    //cv::Mat draw_frame_;
    //if(show_){
        draw_frame_ = frame.clone();
        if(drawing_) draw(draw_frame_, vec_trackers_[hand_types[0]]);
    //}

    /*-----------------------Test for different gestures---------------------------*/
    if(hand_types.size() > 1){
        for(int i = 0; i < vec_trackers_[hand_types[0]]->objects.size(); ++i){
            if(vec_trackers_[hand_types[0]]->objects[i].in_curr_frame == false){

                std::vector<cv::Rect> search_rect = {vec_trackers_[hand_types[0]]->objects[i].rect};
                std::vector<cv::Rect> alt1_rects, alt2_rects;

                alt1_rects = vec_PicoDetectors_[hand_types[1]]->run_smartscales_in(search_rect, stride_);
                update_tracking(alt1_rects, hand_types[1], vec_trackers_[hand_types[0]]->objects[i]);

                if(hand_types.size() > 2){
                    alt2_rects = vec_PicoDetectors_[hand_types[2]]->run_smartscales_in(search_rect, stride_);
                    update_tracking(alt2_rects, hand_types[2], vec_trackers_[hand_types[0]]->objects[i]);
                }

                //Drawing
                if(drawing_){
                    for(int j = 0; j < alt1_rects.size(); ++j){
                        cv::Rect r = alt1_rects[j];
                        rect_scale(r);
                        cv::rectangle(draw_frame_, r, cv::Scalar(0,255,0), 2);
                    }
                    for(int j = 0; j < alt2_rects.size(); ++j){
                        cv::Rect r = alt2_rects[j];
                        rect_scale(r);
                        cv::rectangle(draw_frame_, r, cv::Scalar(0,0,255), 2);
                    }
                }
            }
        }
    }

    //Final draw show
    if(show_){
        if(flip_frame_) flip(draw_frame_, draw_frame_,1);
        imshow("Tracking Detection", draw_frame_);
        if(flip_frame_) flip(draw_frame_, draw_frame_,1);
        cv::waitKey(1);
    }

    return(assign_detections(hand_types[0], true));
}

void GestureImpl::update_tracking(std::vector<cv::Rect>& rect_array, int hand_type, trackedObject& obj){
    if(rect_array.size() > 0 ){
        cv::Rect r = rect_array[0];
        obj.rect = r;
        obj.xc = r.x + r.width/2.0;
        obj.yc = r.y + r.height/2.0;
        obj.width = r.width;
        obj.height = r.height;
        obj.in_curr_frame = true;
        obj.last_frame = 0;
        obj.updated_last_frame = 0;
        obj.hand_ = hand_type;
        obj.rectWithFilter = r;
        obj.num_frame += 1;
        obj.update_kalman();
    }
}

void GestureImpl::draw(cv::Mat &frame, Uniqueness2 *tracker){
    for(int i = 0; i < tracker->objects.size(); ++i){
        //Decide for Kalman and translate coordinates
        cv::Rect r;
        if(kalman_){
            r = tracker->objects[i].rectWithFilter;
            rect_scale(r);
        } else {
            r = tracker->objects[i].rect;
            rect_scale(r);
        }

        //If conditions are filled for recognition
        if (tracker->objects[i].face_verified && tracker->objects[i].last_frame < 3) {
            cv::rectangle(frame, r, tracker->objects[i].draw_color, 3);

        }
        else if (tracker->objects[i].in_curr_frame) {
            cv::rectangle(frame, r, cv::Scalar(0, 255, 255), 1);
        }
    }

}

void GestureImpl::draw(cv::Mat &frame, std::vector<cv::Rect> &fs){
    for(int i = 0; i < fs.size(); ++i){
        cv::Rect r = fs[i];
        rect_scale(r);
        cv::rectangle(frame, r, cv::Scalar(0,255,0), 2);
    }
}

void GestureImpl::show_frame(){
    if(frame_.empty()){
        std::cout << "No Frame" << std::endl;
    }else{
        if(flip_frame_) flip(frame_, frame_, 1);
        cv::imshow("Show Frame", frame_);
        if(flip_frame_) flip(frame_,frame_,1);
        cv::waitKey(1);
    }
}

void GestureImpl::show_detection_frame(){
    if(draw_frame_.empty()){
        std::cout << "No Drawing Frame" << std::endl;
    }else{
        if(flip_frame_) flip(draw_frame_, draw_frame_, 1);
        cv::imshow("Show Detection Frame", draw_frame_);
        if(flip_frame_) flip(draw_frame_,draw_frame_, 1);
        cv::waitKey(1);
    }
}


void GestureImpl::set_time_to_count_detection(double time){
    secondsToCountDet_ = time;
}

void GestureImpl::set_time_to_discard_detection(double time){
    secondsToDiscardDet_ = time;
}

void GestureImpl::set_one_hand(bool one_hand){
    this->one_hand_ = one_hand;
}

void GestureImpl::set_draw(bool draw){
    this->drawing_ = draw;
}

void GestureImpl::set_show(bool show){
    this->show_ = show;
}

void GestureImpl::set_kalman(bool kalman){
    this->kalman_ = kalman;
}

void GestureImpl::set_frame_flip(bool flip_frame){
    this->flip_frame_ = flip_frame;
}

void GestureImpl::rect_scale(cv::Rect &r){
    r.x /= resize_scale_;
    r.y /= resize_scale_;
    r.width /= resize_scale_;
    r.height /= resize_scale_;

}


void GestureImpl::stop_video_capture(){
  if(!camera_.isOpened()){
     camera_.release();
  }
}


/*//For drawing testing
Uniqueness2* Gesture::get_tkr_ptr(){
    return(vec_trackers_[0]);
}
*/

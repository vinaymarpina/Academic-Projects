#include "opencv2/video/tracking.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc_c.h"
#include <time.h>
#include <stdio.h>
#include <ctype.h>

#include "opencv2/opencv.hpp"
#include <iostream>
#include <ctime>
#include <fstream>
#include <opencv2/nonfree/features2d.hpp>
using namespace std;
using namespace cv;

// various tracking parameters (in seconds)
const double MHI_DURATION = 1;
// number of cyclic frame buffer used for motion detection
// (should, probably, depend on FPS)
//Using the SAMHI-4 
const int N = 4;

// parameters:
//  img - input video frame
//  dst - resultant motion picture
//  args - optional parameters

//--------Using SURF as feature extractor and FlannBased for assigning a new point to the nearest one in the dictionary
Ptr<DescriptorMatcher> matcher = DescriptorMatcher::create("FlannBased");
Ptr<DescriptorExtractor> extractor = new SurfDescriptorExtractor();
SurfFeatureDetector detector(150);
//---dictionary size=number of cluster's centroids
int dictionarySize = 70;
TermCriteria tc(CV_TERMCRIT_ITER, 10, 0.001);
int retries = 1;
int flags = KMEANS_PP_CENTERS;
BOWImgDescriptorExtractor bowDE(extractor, matcher);
BOWKMeansTrainer bowTrainer(dictionarySize, tc, retries, flags);

int main(int argc, char** argv)
{
	IplImage* motion = 0;
	CvCapture* capture = 0;
	string line;
	// Taing input text file which cotnains all the train videos
	ifstream myfile("train.txt");
	if (myfile.is_open())
	{
		while (getline(myfile, line))
		{
			istringstream iss(line);
			vector<string> tokens{ istream_iterator<string>{iss},
				istream_iterator<string>{} };
			std::vector<String>::iterator it = tokens.begin();
			int lable = 0;
			lable = stoi(*it);
			++it;
			for (; it != tokens.end(); ++it) 
			{
				Mat SAMHI_10;
				Size imageSize;
				int imageHeight;
				int imageWidth;
				std::cout << *it;
				std::string str = *it;
				char* cstr = new char[str.length() + 1];
				strcpy(cstr, str.c_str());
				capture = cvCaptureFromFile(cstr);
				delete[] cstr;
				if (capture)
				{	
					Mat image;
					Mat currentImage;
					Mat diffImage;

					int imageCount = 0;
					VideoCapture videoref(str);
					int totalimageCount = 0;
					VideoCapture cap(str);
					Mat frame;
					//Capturing all the frames of the Video
					do
					{
						cap >> frame;
						totalimageCount++;
					} while (!frame.empty());
					videoref >> image;

					Mat prev = image.clone();
					//Convert the image to Gray scale
					cvtColor(prev, prev, CV_RGB2GRAY);

					imageSize = prev.size();
					imageHeight = imageSize.height;
					imageWidth = imageSize.width;
					SAMHI_10 = Mat(imageHeight, imageWidth, CV_32FC1, Scalar(0, 0, 0));
					//Until all the frames in the video are processsed
					for (;;)
					{
						videoref >> currentImage;
						if (currentImage.empty())
							break;
						cvtColor(currentImage, currentImage, CV_RGB2GRAY);
						IplImage* image = cvQueryFrame(capture);
						
						if (imageCount%N == 0 && image != NULL){
							//Find the absolute difference betweent he prev and current image
							absdiff(prev, currentImage, diffImage);
							threshold(diffImage, diffImage, 25, 255, THRESH_BINARY);
							//updating the Motion history image for a given difference image between prev and current.
							updateMotionHistory(diffImage, SAMHI_10, (double)imageCount / totalimageCount, MHI_DURATION);
							prev = currentImage.clone();
						}
						imageCount++;
					}
					cvReleaseCapture(&capture);
					//cvDestroyWindow("Motion");
				}
				imshow("SAMI_10", SAMHI_10);
				//cvWaitKey(1000);
				vector<KeyPoint> keypoint;
				SAMHI_10.convertTo(SAMHI_10, CV_8UC1, 255.0);
				detector.detect(SAMHI_10, keypoint);
				Mat features;
				extractor->compute(SAMHI_10, keypoint, features);
				bowTrainer.add(features);
			}
			std::cout << "\n";
		}
		myfile.close();
	}
	else cout << "Unable to open file";
	
	vector<Mat> descriptors = bowTrainer.getDescriptors();
	int count = 0;
	for (vector<Mat>::iterator iter = descriptors.begin(); iter != descriptors.end(); iter++)
	{
		count += iter->rows;
	}
	cout << "Clustering " << count << " features" << endl;
	//choosing cluster's centroids as dictionary's words
	Mat dictionary = bowTrainer.cluster();
	bowDE.setVocabulary(dictionary);
	cout << "extracting histograms in the form of BOW for each image " << endl;
	Mat labels(0, 1, CV_32FC1);
	Mat trainingData(0, dictionarySize, CV_32FC1);
	int k = 0;
	vector<KeyPoint> keypoint1;
	Mat bowDescriptor1;

	string trainline;
	ifstream mytrainfile("train.txt");
	if (mytrainfile.is_open())
	{
		while (getline(mytrainfile, trainline))
		{
			istringstream iss(trainline);
			vector<string> tokens{ istream_iterator<string>{iss},
				istream_iterator<string>{} };
			std::vector<String>::iterator it = tokens.begin();
			int lable = 0;
			lable = stoi(*it);
			++it;
			for (; it != tokens.end(); ++it)
			{

				Mat SAMHI_10;
				Size imageSize;
				int imageHeight;
				int imageWidth;
				std::cout << *it;
				std::string str = *it;
				char* cstr = new char[str.length() + 1];
				strcpy(cstr, str.c_str());
				capture = cvCaptureFromFile(cstr);
				delete[] cstr;
				if (capture)
				{
					Mat image;
					Mat currentImage;
					Mat diffImage;

					int imageCount = 0;
					VideoCapture videoref(str);
					int totalimageCount = 0;
					VideoCapture cap(str);
					Mat frame;
					//Capturing all the frames of the Video
					do
					{
						cap >> frame;
						totalimageCount++;
					} while (!frame.empty());
					videoref >> image;

					Mat prev = image.clone();
					cvtColor(prev, prev, CV_RGB2GRAY);

					imageSize = prev.size();
					imageHeight = imageSize.height;
					imageWidth = imageSize.width;
					SAMHI_10 = Mat(imageHeight, imageWidth, CV_32FC1, Scalar(0, 0, 0));

					for (;;)
					{
						videoref >> currentImage;
						if (currentImage.empty())
							break;
						cvtColor(currentImage, currentImage, CV_RGB2GRAY);
						IplImage* image = cvQueryFrame(capture);

						if (imageCount%N == 0 && image != NULL){
							//Find the absolute difference betweent he prev and current image
							absdiff(prev, currentImage, diffImage);
							threshold(diffImage, diffImage, 25, 255, THRESH_BINARY);
							//updating the Motion history image for a given difference image between prev and current
							updateMotionHistory(diffImage, SAMHI_10, (double)imageCount / totalimageCount, MHI_DURATION);
							prev = currentImage.clone();
						}
						imageCount++;
					}
					cvReleaseCapture(&capture);
					//cvDestroyWindow("Motion");
				}
				imshow("SAMI_10", SAMHI_10);
				cvWaitKey(1000);
				SAMHI_10.convertTo(SAMHI_10, CV_8U, 255.0);
				//Using the bag of words descriptor to find the keypoints in the given MHI
				detector.detect(SAMHI_10, keypoint1);
				bowDE.compute(SAMHI_10, keypoint1, bowDescriptor1);
				//Each videos training data is adding to the array to give as input to SVM classifier
				trainingData.push_back(bowDescriptor1);
				labels.push_back((float)lable);
			}
			std::cout << "\n";
		}
		mytrainfile.close();
	}
	else cout << "Unable to open file";

	//Setting up SVM parameters
	CvSVMParams params;
	params.kernel_type = CvSVM::RBF;
	params.svm_type = CvSVM::C_SVC;
	params.gamma = 0.50625000000000009;
	params.C = 312.50000000000000;
	params.term_crit = cvTermCriteria(CV_TERMCRIT_ITER, 100, 0.000001);
	CvSVM svm;

	printf("%s\n", "Training SVM classifier");
	
	bool res = svm.train(trainingData, labels, cv::Mat(), cv::Mat(), params);

	cout << "Processing evaluation data..." << endl;

	Mat groundTruth(0, 1, CV_32FC1);
	Mat evalData(0, dictionarySize, CV_32FC1);
	k = 0;
	vector<KeyPoint> keypoint2;
	Mat bowDescriptor2;


	Mat results(0, 1, CV_32FC1);;

	string testline;
	//taking the input of the test text to predict the videos actions
	//ifstream mytestfile("C:\\Users\\Vinay\\Desktop\\Spring2015\\VideoAnalytics\\Assignments\\Assignment4\\demoTest.txt");
	
	
	cout << "\n";
	VideoCapture videoref(0);
	//cvWaitKey(30000);
	Mat image;
	Mat currentImage;
	Mat diffImage;
	int imageCount = 0;
	int totalimageCount = 150;
	
	videoref >> image;
	Mat prev = image.clone();
	cvtColor(prev, prev, CV_RGB2GRAY);
	
	Mat SAMHI_10;
	Size imageSize;
	int imageHeight;
	int imageWidth;

	imageSize = prev.size();
	imageHeight = imageSize.height;
	imageWidth = imageSize.width;
	SAMHI_10 = Mat(imageHeight, imageWidth, CV_32FC1, Scalar(0, 0, 0));
	float response = 0;
	for (;;)
			{			
				ostringstream convert;
				
						videoref >> currentImage;
						if (currentImage.empty())	
							break;
						cvtColor(currentImage, currentImage, CV_RGB2GRAY);
						IplImage* image = cvQueryFrame(capture);
						//imshow("CurrentImage", currentImage);
						if (imageCount%N == 0){
							absdiff(prev, currentImage, diffImage);
							threshold(diffImage, diffImage, 25, 255, THRESH_BINARY);
							updateMotionHistory(diffImage, SAMHI_10, (double)imageCount / totalimageCount, MHI_DURATION);
							prev = currentImage.clone();
						}
						
						//imshow("ShowSHAMI", SAMHI_10);
						
						imageCount++;
					
					cvReleaseCapture(&capture);
					//cvDestroyWindow("Motion");
					if (imageCount >=totalimageCount){
						
						SAMHI_10.convertTo(SAMHI_10, CV_8U, 255.0);
						//imshow("SAMI_10", SAMHI_10);
						//cvWaitKey(1000);
						detector.detect(SAMHI_10, keypoint2);
						bowDE.compute(SAMHI_10, keypoint2, bowDescriptor2);
						//Gives the evaluated data from the MHI of the test video
						evalData.push_back(bowDescriptor2);
						//SVM prediction of the given test video
						response = svm.predict(bowDescriptor2);

						imageCount = 0;
						SAMHI_10 = Mat(imageHeight, imageWidth, CV_32FC1, Scalar(0, 0, 0));;
					}
					convert << response;

					putText(currentImage, "Motion Detected: ", cvPoint(30, 30),

						FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					if (response == 1){
						putText(currentImage, "Walk", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 2){
						putText(currentImage, "Run", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 3){
						putText(currentImage, "Jump", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 4){
						putText(currentImage, "Side", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 5){
						putText(currentImage, "Bend", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 6){
						putText(currentImage, "Wave1", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 7){
						putText(currentImage, "Wave2", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 8){
						putText(currentImage, "Pjump", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 9){
						putText(currentImage, "Jack", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					else if (response == 10){
						putText(currentImage, "Skip", cvPoint(30, 60),
							FONT_HERSHEY_COMPLEX_SMALL, 0.8, cvScalar(64, 64, 255), 1, CV_AA);
					}
					imshow("MHI", SAMHI_10);
					cvWaitKey(10);
					imshow("Original", currentImage);
					cvWaitKey(10);
			}

}



#ifdef _EiC
main(1, "motempl.c");
#endif

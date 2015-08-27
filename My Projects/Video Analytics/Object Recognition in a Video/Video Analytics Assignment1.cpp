// Video Analytics Assignment1.cpp : Defines the entry point for the console application.
//

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <ctime>
#include <iostream>
using namespace cv;
using namespace std;

int main(int argc, char** argv)
{
	//1.	Capturing a video-clip in real-time using a video camera (Web Cam)
	VideoCapture vCapture(0); 

	while (true)
	{
		Mat originalImg;
		//2.	Grabbing a frame in the captured video 
		vCapture.read(originalImg); 
		Mat hsvImg;
		//3.	Converting the image from RGB to HSV color space
		cvtColor(originalImg, hsvImg, COLOR_BGR2HSV); 
		Mat finalImg;

		//HSV values are for red color bottle cap found using testing under room light, if the room light settings are changed
		// we will see a little dirstubance in the white spot in the video.
		//That is the reasong added multiple HSV values for testing purpose
		//inRange(hsvImg, Scalar(161, 73, 126), Scalar(179, 255, 255), finalImg); 
		//inRange(hsvImg, Scalar(170, 112, 154), Scalar(179, 255, 255), finalImg);

		//4.	Finding the “colored object” in the image using color
		unsigned int start = clock();
		inRange(hsvImg, Scalar(164, 117, 147), Scalar(179, 255, 255), finalImg);
		cout << "Time taken per video frame for step-4 in millisecs: " << clock() - start;
		//5. Showing the step 4 can be done in real time.
		imshow("Final Image", finalImg); 
		imshow("Original Image", originalImg); 

		//TO break the loop esc key need to be pressed as continously frames are getting processed
		if (waitKey(30) == 27)
		{
			cout << "esc key is pressed to stop the program" << endl;
			break;
		}
	}
	return 0;
}

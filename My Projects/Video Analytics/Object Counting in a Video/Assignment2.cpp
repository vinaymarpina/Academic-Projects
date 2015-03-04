// Video Analytics Assignment2.cpp : Defines the entry point for the console application.
//

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <ctime>
#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

using namespace cv;
using namespace std;
Mat src, erosion_dst, dilation_dst;
int erosion_type;
int erosion_elem = 2;
int erosion_size = 1;
int dilation_elem = 2;
int dilation_size = 10;
int dilation_type;

void Erosion();
void Dilation();

/*
Refreence: 4-Connected component algorithm implementation is Taken from OpenCV forum.
*/
namespace cv{

	namespace connectedcomponents{

		using std::vector;

		template<typename LabelT>

		inline static

			LabelT findRoot(const vector<LabelT> &P, LabelT i){

			LabelT root = i;

			while (P[root] < root){

				root = P[root];

			}

			return root;

		}

		template<typename LabelT>

		inline static

			void setRoot(vector<LabelT> &P, LabelT i, LabelT root){

			while (P[i] < i){

				LabelT j = P[i];

				P[i] = root;

				i = j;

			}

			P[i] = root;

		}

		template<typename LabelT>

		inline static

			LabelT find(vector<LabelT> &P, LabelT i){

			LabelT root = findRoot(P, i);

			setRoot(P, i, root);

			return root;

		}

		template<typename LabelT>

		inline static

			LabelT set_union(vector<LabelT> &P, LabelT i, LabelT j){

			LabelT root = findRoot(P, i);

			if (i != j){

				LabelT rootj = findRoot(P, j);

				if (root > rootj){

					root = rootj;

				}

				setRoot(P, j, root);

			}

			setRoot(P, i, root);

			return root;

		}

		template<typename LabelT>

		inline static

			LabelT flattenL(vector<LabelT> &P){

			LabelT k = 1;

			for (size_t i = 1; i < P.size(); ++i){

				if (P[i] < i){

					P[i] = P[P[i]];

				}
				else{

					P[i] = k; k = k + 1;

				}

			}

			return k;

		}

		const int G4[2][2] = { { -1, 0 }, { 0, -1 } };

		const int G8[4][2] = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 } };


		template<typename LabelT, typename PixelT, int connectivity = 8>

		struct LabelingImpl{

			LabelT operator()(Mat &L, const Mat &I){

				const int rows = L.rows;

				const int cols = L.cols;

				size_t nPixels = size_t(rows) * cols;

				vector<LabelT> P; P.push_back(0);

				LabelT l = 1;

				//scanning phase

				for (int r_i = 0; r_i < rows; ++r_i){

					for (int c_i = 0; c_i < cols; ++c_i){

						if (!I.at<PixelT>(r_i, c_i)){

							L.at<LabelT>(r_i, c_i) = 0;

							continue;

						}

						
						

							//B & D only

							const int b = 0;

							const int d = 1;

							assert(connectivity == 4);

							bool T[2];

							for (size_t i = 0; i < 2; ++i){

								int gr = r_i + G4[i][0];

								int gc = c_i + G4[i][1];

								T[i] = false;

								if (gr >= 0 && gr < I.rows && gc >= 0 && gc < I.cols){

									if (I.at<PixelT>(gr, gc)){

										T[i] = true;

									}

								}

							}


							if (T[b]){

								if (T[d]){

									//copy(d, b)

									L.at<LabelT>(r_i, c_i) = set_union(P, L.at<LabelT>(r_i + G4[d][0], c_i + G4[d][1]), L.at<LabelT>(r_i + G4[b][0], c_i + G4[b][1]));

								}
								else{

									//copy(b)

									L.at<LabelT>(r_i, c_i) = L.at<LabelT>(r_i + G4[b][0], c_i + G4[b][1]);

								}

							}
							else{

								if (T[d]){

									//copy(d)

									L.at<LabelT>(r_i, c_i) = L.at<LabelT>(r_i + G4[d][0], c_i + G4[d][1]);

								}
								else{

									//new label

									L.at<LabelT>(r_i, c_i) = l;

									P.push_back(l);//P[l] = l;

									l = l + 1;

								}

							}

					}

				}


				//analysis

				LabelT nLabels = flattenL(P);

				for (size_t r = 0; r < L.rows; ++r){

					for (size_t c = 0; c < L.cols; ++c){

						L.at<LabelT>(r, c) = P[L.at<LabelT>(r, c)];

					}

				}


				return nLabels;

			}

		};

	}


	int connectedComponents(Mat &L, const Mat &I, int connectivity){

		CV_Assert(L.rows == I.rows);

		CV_Assert(L.cols == I.cols);

		CV_Assert(L.channels() == 1 && I.channels() == 1);

		CV_Assert(connectivity == 8 || connectivity == 4);


		int lDepth = L.depth();

		int iDepth = I.depth();

		using connectedcomponents::LabelingImpl;

		//warn if L's depth is not sufficient?


		if (lDepth == CV_8U){

			if (iDepth == CV_8U || iDepth == CV_8S){

				if (connectivity == 4){

					return LabelingImpl<uint8_t, uint8_t, 4>()(L, I);

				}				

			}
			else if (iDepth == CV_16U || iDepth == CV_16S){

				if (connectivity == 4){

					return LabelingImpl<uint8_t, uint16_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_32S){

				if (connectivity == 4){

					return LabelingImpl<uint8_t, int32_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_32F){

				if (connectivity == 4){

					return LabelingImpl<uint8_t, float, 4>()(L, I);

				}	

			}
			else if (iDepth == CV_64F){

				if (connectivity == 4){

					return LabelingImpl<uint8_t, double, 4>()(L, I);

				}
			}

		}
		else if (lDepth == CV_16U){

			if (iDepth == CV_8U || iDepth == CV_8S){

				if (connectivity == 4){

					return LabelingImpl<uint16_t, uint8_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_16U || iDepth == CV_16S){

				if (connectivity == 4){

					return LabelingImpl<uint16_t, uint16_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_32S){

				if (connectivity == 4){

					return LabelingImpl<uint16_t, int32_t, 4>()(L, I);

				}
			}
			else if (iDepth == CV_32F){

				if (connectivity == 4){

					return LabelingImpl<uint16_t, float, 4>()(L, I);

				}

			}
			else if (iDepth == CV_64F){

				if (connectivity == 4){

					return LabelingImpl<uint16_t, double, 4>()(L, I);

				}

			}

		}
		else if (lDepth == CV_32S){

			if (iDepth == CV_8U || iDepth == CV_8S){

				if (connectivity == 4){

					return LabelingImpl<int32_t, uint8_t, 4>()(L, I);

				}


			}
			else if (iDepth == CV_16U || iDepth == CV_16S){

				if (connectivity == 4){

					return LabelingImpl<int32_t, uint16_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_32S){

				if (connectivity == 4){

					return LabelingImpl<int32_t, int32_t, 4>()(L, I);

				}

			}
			else if (iDepth == CV_32F){

				if (connectivity == 4){

					return LabelingImpl<int32_t, float, 4>()(L, I);

				}

			}
			else if (iDepth == CV_64F){

				if (connectivity == 4){

					return LabelingImpl<int32_t, double, 4>()(L, I);

				}

			}

		}


		CV_Error(CV_StsUnsupportedFormat, "unsupported label/image type");

		return -1;

	}
}

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

		//4.	Finding the “colored object” in the image using color
		unsigned int start = clock();
		inRange(hsvImg, Scalar(164, 117, 147), Scalar(179, 255, 255), finalImg);
		src = finalImg;
		//Opening Concept First use Erosion and then Dilation
		Erosion();
		Dilation();
		//Using the 4-Connected Component to count the Objects
		Mat labeledImage(dilation_dst.size(),CV_16U);
		int objectCount = connectedComponents(labeledImage,dilation_dst, 4);
		objectCount = objectCount - 1;

		string countString = "Number of Objects: "+ to_string(objectCount);

		// To draw a rectangle around the detected objects
		std::vector< std::vector<cv::Point> > contours;
		std::vector<cv::Point> points;
		cv::findContours(dilation_dst, contours, CV_RETR_LIST, CV_CHAIN_APPROX_NONE);
		for (size_t i = 0; i<contours.size(); i++) {
			for (size_t j = 0; j < contours[i].size(); j++) {
				cv::Point p = contours[i][j];
				points.push_back(p);
			}
			if (points.size() > 0){
				cv::Rect brect = cv::boundingRect(cv::Mat(points).reshape(2));
				cv::rectangle(originalImg, brect.tl(), brect.br(), cv::Scalar(100, 100, 200), 2, CV_AA);
			}
			points.clear();
		}
		
		//To print text on the image
		putText(originalImg, countString, cvPoint(30, 30),
			FONT_HERSHEY_PLAIN, 0.8, cvScalar(200, 200, 250), 1, CV_AA);
		imshow("Original Image", originalImg);

		cout << " Number of Objects" << objectCount;
		cout << endl;
		//Wait between each frame
		cvWaitKey(30);
		//TO break the loop esc key need to be pressed as continously frames are getting processed
		if (waitKey(30) == 27)
		{
			cout << "esc key is pressed to stop the program" << endl;
			break;
		}
	}
	return 0;
}


void Erosion()
{
	if (erosion_elem == 0){ erosion_type = MORPH_RECT; }
	else if (erosion_elem == 1){ erosion_type = MORPH_CROSS; }
	else if (erosion_elem == 2) { erosion_type = MORPH_ELLIPSE; }

	Mat element = getStructuringElement(erosion_type,
		Size(2 * erosion_size + 1, 2 * erosion_size + 1),
		Point(erosion_size, erosion_size));
	erode(src, erosion_dst, element);
	imshow("Erosion Demo", erosion_dst);
}

void Dilation()
{		
	if (dilation_elem == 0){ dilation_type = MORPH_RECT; }
	else if (dilation_elem == 1){ dilation_type = MORPH_CROSS; }
	else if (dilation_elem == 2) { dilation_type = MORPH_ELLIPSE; }

	Mat element = getStructuringElement(dilation_type,
		Size(2 * dilation_size + 1, 2 * dilation_size + 1),
		Point(dilation_size, dilation_size));
	dilate(erosion_dst, dilation_dst, element);
	imshow("Dilation Demo", dilation_dst);
}







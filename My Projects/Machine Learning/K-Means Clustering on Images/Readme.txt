***************************************************
Name: Venkata Vinay Kumar Marpina
Net Id: vxm133430
Class: Machine Learning CS.6375
HomeWork: 4
***************************************************
To Run the Code needed JDK 1.5 or above
****************************************************

To Compile the Kmeans Code:

javac KMeans.java

TO run the Kmeans:

java KMeans <Path to input Image> <int K-Value> <Output image name>

Example: java KMeans Koala.jpg 10 Koala10.jpg

Note: I am iterating the Kmeans ten times for finding the average compressed ratio and variance. ther is a loop count of 30 in each Kmeans run so total 10*30 = 300 iterations. So Program run will take for for K=20 around 5 mins. Please be little patient. or You can stop after finishing the one run to check the program runnability all the required results(output image, Compressed ratio) will be given after one run.
	

If you wanted to run for K: 2,5,10,15,20 and for both Koala.jpg and Penguins.jpg just run the batch file given in the zip folder. But the batch works only in windows.

Batch file name: KMeansbatchRun.bat 

****************************************************
Files Submitted: 
 KMeans.java
 Bagging-Boosting Dataset folder
 K-MenasOutput Images Folder
 Promoters Data Arff Format folder ( For Perceptron Accuracy)
 KMeansOutput.txt ( Contains individual run compression ratio values)
 KMeansbatchRun.bat
 Readme.txt
 Report-HW4.pdf

****************************************************

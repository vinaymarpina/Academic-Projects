***************************************************
Name: Venkata Vinay Kumar Marpina
Net Id: vxm133430
Class: Machine Learning CS.6375
HomeWork: 3
***************************************************
To Run the Code needed JDK 1.5 or above
****************************************************

To Compile Perceptron Code:
	
Perceptron:

    javac MainClass.java

Run Perceptron:

First Copy the stopwords.txt given in the zip to some folder.

    With out removing Stopwords from Vocab:

java MainClass <Path for train set spam folder> <Path for train set Ham folder> <Path for test set spam folder> <Path for test set ham folder> <Integer loopCount> <double Learning Rate> <Path to stopwords.txt> no

Example: java MainClass /train_spam /train_ham /test_spam /test_ham 100 0.1 /stopwords.txt no
	
    removing Stopwords from Vocab:

java MainClass <Path for train set spam folder> <Path for train set Ham folder> <Path for test set spam folder> <Path for test set ham folder> <Integer loopCount> <double Learning Rate> <Path to stopwords.txt> yes


Example: java MainClass /train_spam /train_ham /test_spam /test_ham 100 0.1 /stopwords.txt yes


*****************************************************************

To Convert the Spam/Ham files to .ARFF file

1) First Copy paste the weka.jar given in the folder in the location where you are running the batch file

Run the batch file given in the folder in windows command prompt :

 ConverttoARFF.bat <Path to Train set> <Path to Test set>

******************************************************************
Collaborative Filtering:

To Compile Collaborative Filter code:

javac CollaborativeFilter.java

To Run:

java CollaborativeFilter <path to trainfile.txt> <path to testfile.txt>

Example: java CollaborativeFilter TrainingRatings.txt TestingRatings.txt

Memory heap :

-Xms2048m or -Xms3048m


****************************************************
Files Submitted: 
 
 MainClass.java
 Perceptron.java
 CollaborativeFilter.java	
 stopwords.txt
 Readme.txt
 Report-HW3.pdf

****************************************************

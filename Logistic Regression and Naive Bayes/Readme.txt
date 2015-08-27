***************************************************
Name: Venkata Vinay Kumar Marpina
Net Id: vxm133430
Class: Machine Learning CS.6375
HomeWork: 2
***************************************************
To Run the Code needed JDK 1.5 or above
****************************************************
Note: Due to the File manipulation each run takes little more than 10 sec 
      For large number of LoopCount values like 100,200,500,1000 it takes running of LR in minutes so please be little patient, Thank You
****************************************************
To Compile the Code:

    javac MainClass.java

To Run the Code:

    There are Multiple Runs are present:

	1) To run normal Naive bayes 
		
	java MainClass <Path for train set spam folder> <Path for train set Ham folder> <Path for test set spam folder> <Path for test set ham folder> <String NB>

  	Ex: java MainClass ..\train\spam ..\train\ham ..\test\spam ..\test\ham NB
	
	*******************************************************************************************************************************************************

	2) To run  Naive bayes with stop words
	
	Please save the given stopwords.txt file in some folder first.
		
	java MainClass <Path for trainset spam folder> <Path for trainset Ham folder> <Path for testset spam folder> <Path for testset ham folder> <String NBS> <path to stopwords text>

  	Ex: java MainClass ..\train\spam ..\train\ham ..\test\spam ..\test\ham NBS ..\path\stopwords.txt
	
	*******************************************************************************************************************************************************

	3) To run normal Logistic Regression 
		
	java MainClass <Path for trainset spam folder> <Path for trainset Ham folder> <Path for testset spam folder> <Path for testset ham folder> <String LR> <Integer loopCount> <double eeta> <double lambda>

  	Ex: java MainClass ..\train\spam ..\train\ham ..\test\spam ..\test\ham LR 100 0.01 0.5

	*******************************************************************************************************************************************************

	4) To run normal Logistic Regression with stop words 

	Please save the given stopwords.txt file in some folder first.
		
	java MainClass <Path for trainset spam folder> <Path for trainset Ham folder> <Path for testset spam folder> <Path for testset ham folder> <String LRS> <Integer loopCount> <double eeta> <double lambda> <path to stopwords text>

  	Ex: java MainClass ..\train\spam ..\train\ham ..\test\spam ..\test\ham LRS 100 0.01 0.5 ..\path\stopwords.txt


****************************************************
Files Submitted: 
 
 MainClass.java
 ReadFolder.java
 NaiveBayes.java
 LogisticRegression.java
 stopwords.txt
 Readme.txt
 Report-HW2.pdf

****************************************************

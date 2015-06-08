import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

/*
 * This Class is the main class which is used to run the program
 * 
 * this class takes arguments while running 
 * 
 * arguments changes based on the type of the program you wanted to run
 * 
 * What type of arguments needed specified in the Readme file explicitly for each type of run.
 * 
 * Note: Due to the file manipulation each run takes around 15 to 20 sec please be little patient
 */
public class MainClass {

	public static void main(String[] args){
		try {
			ReadFolder readFolder = new ReadFolder();
			NaiveBayes NB = new NaiveBayes();
			NaiveBayes NBSTOP = new NaiveBayes();
			LogisticRegression LR = new LogisticRegression();
			LogisticRegression LRSTOP = new LogisticRegression();
			/*
			 * Below File reading takes the folder path for  spam and ham files
			 * for both training and testing data sets. Due to the file reading this piece of code
			 * takes more than 10 sec
			 */
			File[] filesSpamList = readFolder.getAllFiles(args[0]);
			File[] filesHamList = readFolder.getAllFiles(args[1]);
			File[] filesSpamTestList = readFolder.getAllFiles(args[2]);
			File[] filesHamTestList = readFolder.getAllFiles(args[3]);
			ArrayList<String> dataSpamStringList = new ArrayList<String>();
			ArrayList<String> dataHamStringList = new ArrayList<String>();
			/*
			 * reading the each file and converting them into one Big string for future manipulations
			 */
			for(int i=0;i<filesSpamList.length;i++){
				try {
					dataSpamStringList.add(readFolder.readFile(filesSpamList[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for(int i=0;i<filesHamList.length;i++){
				try {
					dataHamStringList.add(readFolder.readFile(filesHamList[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Hashtable<String,Double> vocab = new Hashtable<String,Double>();
			Hashtable<String,Double> vocabStopWords = new Hashtable<String,Double>();
			/*
			 * This argument tells the code to which type of classification asked to run
			 * If its NaiveBayes the string will be "NB"
			 * if its NaiveBayes with Stop words string will be "NBS"
			 * if its LogisticRegression the string will be "LR"
			 * if its LogisticRegression with stop words string will be "LRS"
			 */
			String whattoRun = args[4];
			
			if(whattoRun.equalsIgnoreCase("NB")){
				vocab = vocabulary(dataSpamStringList, dataHamStringList);
				System.out.println("*****************************************");
				System.out.println("Printing the Accuracy For NaiveBayes");
				System.out.println("*****************************************");
				NB.TrainMultinomialNB(dataSpamStringList, dataHamStringList,vocab);
				NB.accuracy(filesSpamTestList, filesHamTestList, readFolder);
			}else if(whattoRun.equalsIgnoreCase("NBS")){
				Hashtable<String,Integer> stopWords = readFolder.readStopwordsFile(args[5]);
				vocabStopWords = vocabAfterStopwords(dataSpamStringList, dataHamStringList, stopWords);
				System.out.println("*****************************************");
				System.out.println("Printing the Accuracy For NaiveBayes with Stop Words");
				System.out.println("*****************************************");
				NBSTOP.TrainMultinomialNB(dataSpamStringList, dataHamStringList,vocabStopWords);
				NBSTOP.accuracy(filesSpamTestList, filesHamTestList, readFolder);	
			}else if(whattoRun.equalsIgnoreCase("LR")){
				vocab = vocabulary(dataSpamStringList, dataHamStringList);
				int loopCount = Integer.parseInt(args[5]);
				double eta = Double.parseDouble(args[6]);
				double lambda = Double.parseDouble(args[7]);
				System.out.println("*****************************************");
				System.out.println("Printing the Accuracy For Logistic Regression");
				System.out.println("*****************************************");
				LR.trainLogisticRegression(dataSpamStringList, dataHamStringList,vocab,loopCount,eta,lambda);
				LR.accuracy(filesSpamTestList, filesHamTestList, readFolder);
			}else if(whattoRun.equalsIgnoreCase("LRS")){
				int loopCount = Integer.parseInt(args[5]);
				double eta = Double.parseDouble(args[6]);
				double lambda = Double.parseDouble(args[7]);
				Hashtable<String,Integer> stopWords = readFolder.readStopwordsFile(args[8]);
				vocabStopWords = vocabAfterStopwords(dataSpamStringList, dataHamStringList, stopWords);
				System.out.println("*****************************************");
				System.out.println("Printing the Accuracy For Logistic Regression with Stop words");
				System.out.println("*****************************************");
				LRSTOP.trainLogisticRegression(dataSpamStringList, dataHamStringList,vocabStopWords,loopCount,eta,lambda);
				LRSTOP.accuracy(filesSpamTestList, filesHamTestList, readFolder);
			}	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * This method returns the vocabulary for the entire training data set.
	 * in this vocabulary i removed the all special characters, all the numbers
	 * removed the repeated words, white spaces.
	 * 
	 * while retrieving the vocabulary only for each word randomly assigning the weights -0.5 to 0.5
	 */
	public static Hashtable<String,Double> vocabulary(ArrayList<String> spamList, ArrayList<String> hamList){

		Hashtable<String,Double> vocab = new Hashtable<String, Double>();
		ArrayList<String> currentList = new ArrayList<String>();
		for(int j=0; j<2;j++){
			if(j==0){
				currentList = spamList;
			}else if(j==1){
				currentList = hamList;
			}
			for(int i=0;i<currentList.size();i++){
				String str = currentList.get(i);
				StringTokenizer st = new StringTokenizer(str,",");
				while(st.hasMoreElements()){
					String vocabString = (String)st.nextElement();
					Random r = new Random();
					
					if(!vocab.containsKey(vocabString)){
						double weight = (double)(0.5-r.nextDouble());
						vocab.put(vocabString, weight);
					}else{
						//TODO
					}
				}
			}
		}
		return vocab;
	}
	/*
	 * This method returns the vocabulary with out the stopping words for the entire training data set.
	 * in this vocabulary i removed the all special characters, all the numbers
	 * removed the repeated words, white spaces.
	 * 
	 * while retrieving the vocabulary only for each word randomly assigning the weight ranged from -0.5 to 0.5
	 */
	public static Hashtable<String,Double> vocabAfterStopwords(ArrayList<String> spamList, ArrayList<String> hamList,Hashtable<String,Integer> stopWords){
		
		Hashtable<String,Double> vocab = new Hashtable<String, Double>();
		ArrayList<String> currentList = new ArrayList<String>();
		for(int j=0; j<2;j++){
			if(j==0){
				currentList = spamList;
			}else if(j==1){
				currentList = hamList;
			}
			for(int i=0;i<currentList.size();i++){
				String str = currentList.get(i);
				StringTokenizer st = new StringTokenizer(str,",");
				while(st.hasMoreElements()){
					String vocabString = (String)st.nextElement();
					Random r = new Random();
					
					if(!vocab.containsKey(vocabString) && !stopWords.containsKey(vocabString) ){
						double weight = (double)(0.5-r.nextDouble());
						vocab.put(vocabString, weight);
					}else{
						//TODO
					}
				}

			}
		}
		return vocab;
		
	}
}

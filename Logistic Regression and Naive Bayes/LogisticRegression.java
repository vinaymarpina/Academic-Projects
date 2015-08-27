import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/*
 * This class uses Logistic Regression concepts to learn and test the data sets
 * 
 * In this for class values i am considering ham =0; spam =1;
 */

public class LogisticRegression {
	NaiveBayes NB = new NaiveBayes();
	double Wo = 3.0;
	Hashtable<String,Double> vocab = new Hashtable<String,Double>();
	ArrayList<Hashtable<String,Double>> eachSpamWordCount = new ArrayList<Hashtable<String,Double>>();
	ArrayList<Hashtable<String,Double>> eachHamWordCount = new ArrayList<Hashtable<String,Double>>();
	ArrayList<Double> eachSpamDocSum = new ArrayList<Double>();
	ArrayList<Double> eachHamDocSum = new ArrayList<Double>();
	
	/*
	 * This method calculates how many times a each word occurred in particular document and stores
	 * the value against that document in a table. And all these tables for a particular documents are
	 * stored in array list.
	 */
	public void eachwordCountInDocument(ArrayList<String> spamList, ArrayList<String> hamList){
		ArrayList<String> currentList = new ArrayList<String>();

		for(int i=0;i<2;i++){
			if(i==0){
				currentList = hamList;
			}else{
				currentList = spamList;
			}
			for(int j=0;j<currentList.size();j++){
				Hashtable<String,Double> currentTable = new Hashtable<String, Double>();
				String str = currentList.get(j);
				StringTokenizer st = new StringTokenizer(str,",");
				//int totalWords = st.countTokens();
				while(st.hasMoreElements()){
					String currentWord = (String)st.nextElement();
					if(vocab.containsKey(currentWord)){
						double wordCount = (double)NB.countOccurence(currentWord, str);
						//double wordProb = (double)wordCount/(double)totalWords;
						currentTable.put(currentWord, wordCount);
					}
				}
				if(i==0){
					eachHamWordCount.add(currentTable);
				}else{
					eachSpamWordCount.add(currentTable);
				}
			}
		}
	}
	/*
	 * This method is used to learn update the weights for given training set.
	 */
	public void trainLogisticRegression(ArrayList<String> spamList, ArrayList<String> hamList,Hashtable<String,Double> vocabulary, 
			int loopCount, double eta, double lambda){
		/*
		 *  Vocabulary weights are already initialized with Random numbers
		 */
		vocab = vocabulary;
		eachwordCountInDocument(spamList,hamList);
		ArrayList<String> currentList = new ArrayList<String>();
		//int count =0;
		/*
		 * This loop is to make the weights change by infinitesimal value.
		 */
		for(int k=0;k<loopCount;k++){
			/*
			 * This loop calculates the PHat value of the each document and stores in 
			 * array list. these array list are getting updated for each rotation which used to 
			 * make the weights change.
			 */
			for(int i=0;i<2;i++){
				if(i==0){
					currentList = hamList;
				}else{
					currentList = spamList;
				}
				for(int j=0;j<currentList.size();j++){
					Hashtable<String,Double> wordCount = new Hashtable<String,Double>();
					if(i==0){
						wordCount = eachHamWordCount.get(j);
					}else{	
						wordCount = eachSpamWordCount.get(j);
					}
					double phatofY = pHatofYone(wordCount);
					if(i==0){
						eachHamDocSum.add(phatofY);
					}else{
						eachSpamDocSum.add(phatofY);
					}
				}
			}
			/*
			 * This loop takes the each word in the vocabulary and updates their weight using
			 * the weight updated rule.
			 */
			for(String key : vocab.keySet()){
				double summationTerm =0.0;
				double currentweight = vocab.get(key);
				for(int i=0;i<2;i++){
					if(i==0){
						currentList = hamList;
					}else{	
						currentList = spamList;
					}
					for(int j=0;j<currentList.size();j++){
						Hashtable<String,Double> wordCount = new Hashtable<String,Double>();
						if(i==0){
							wordCount = eachHamWordCount.get(j);
						}else{
							wordCount = eachSpamWordCount.get(j);
						}
						if(wordCount.containsKey(key)){
							double keyCount = wordCount.get(key);
							double phatofY = 0.0;
							if(i==0){
								phatofY = eachHamDocSum.get(j);
							}else{	
								phatofY = eachSpamDocSum.get(j);
							}
							summationTerm = summationTerm+ (keyCount*(i-phatofY));
							
						}else{
							//TODO
						}
					}
				}
				//count++;
				double finalkeyWeight = currentweight+ (eta * summationTerm)-(eta*lambda*currentweight);
				vocab.put(key, finalkeyWeight);
				//System.out.println(count);
			}
			eachSpamDocSum.clear();
			eachHamDocSum.clear();
		}
	}
	/*
	 * This method uses the test data to classify the given document as spam or ham
	 * using Summation of WiXi value. As i am taking spam as 1 if Summation is greater than zero i am considering
	 * it as spam and if its less then i am considering it as Ham
	 * 
	 */
	public ArrayList<Integer> testLogisticRegression(File[] TestList,ReadFolder readFolder) throws Exception{
		ArrayList<Integer> spamHamCount = new ArrayList<Integer>();
		int spamCount =0;
		int hamCount =0;
		for(int i=0;i<TestList.length;i++){
			String str = readFolder.readFile(TestList[i]);
			StringTokenizer st = new StringTokenizer(str,",");
			//Integer totalWords = st.countTokens();
			Hashtable<String,Double> wordCount = new Hashtable<String,Double>();
			while(st.hasMoreElements()){
				String currentWord = (String)st.nextElement();
				double wordOccurance = (double)NB.countOccurence(currentWord, str);
				//double wordProb = (double)wordOccurance/(double)totalWords;
				wordCount.put(currentWord,wordOccurance);
			}
			double weightValue = Wo+overAllWiXi(wordCount);
			if(weightValue>0){
				spamCount++;
			}else{
				hamCount++;
			}
		}
		spamHamCount.add(spamCount);
		spamHamCount.add(hamCount);
		return spamHamCount;
	}
	/*
	 * This method gives the accuracy of the given test data set
	 */
	public double accuracy(File[] spamTestList, File[] hamTestList,ReadFolder readFolder) throws Exception{

		ArrayList<Integer> spamCount = testLogisticRegression(spamTestList, readFolder);
		ArrayList<Integer> hamCount = testLogisticRegression(hamTestList, readFolder);
		double spamAccuracy = (((double)spamCount.get(0))/((double)spamTestList.length))*100;
		//System.out.println("Printing the LR Spam Accuracy:"+ spamAccuracy);
		double hamAccuracy = (((double)hamCount.get(1))/((double)hamTestList.length))*100;
		//System.out.println("Printing the LR Ham Accuracy:"+ hamAccuracy);
		double finalAccuracy = (spamAccuracy + hamAccuracy)/2;
		System.out.println("Printing the LR Final Accuracy:"+ finalAccuracy);
		return finalAccuracy;
	}

	public double overAllWiXi(Hashtable<String,Double> wordTable){
		double overAllWiXi = 0.0;
		for(String key: wordTable.keySet()){
			if(vocab.containsKey(key)){
				double keyWeight = vocab.get(key);
				double wordCount = wordTable.get(key);
				overAllWiXi = overAllWiXi+(keyWeight * wordCount);
			}else{
				//TODO
			}
		}
		return overAllWiXi;
	}
	public double pHatofYone(Hashtable<String,Double> wordTable){	
		double pHatofY = 0.0;
		double overallSum = overAllWiXi(wordTable);
		/*
		 * To remove NAN situation assigning 1.0 very large number  and 0.0 for very small number
		 */
		if(overallSum>100){
			return 1.0;
		}else if(overallSum<-100){
			return 0.0;
		}else{
			double exponentialValue = Math.pow(Math.E, (Wo+overallSum));
			pHatofY = (exponentialValue)/(1+exponentialValue);
			//pHatofY = 1/(1+exponentialValue);
			return pHatofY;
		}
		
	}
}

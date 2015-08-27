import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * This Class Implements the NaiveBayes classification algorithms.
 * 
 *  takes input as training and testing data sets 
 *  
 *  gives out put as printing the accuracy of the test data.
 */
public class NaiveBayes {
	Hashtable<String,Double> vocab = new Hashtable<String,Double>();
	Hashtable<Integer,Double> priorTable = new Hashtable<Integer, Double>();
	Hashtable<Integer,Hashtable<String,Double>> condProbTable = new Hashtable<Integer,Hashtable<String,Double>>();
	/*
	 * This method Using the training data set learns the prior, conditional probability
	 * of the each word for given spam and ham document.
	 * 
	 */
	public void TrainMultinomialNB(ArrayList<String> spamList, ArrayList<String> hamList,Hashtable<String,Double> vocabulary){
		vocab = vocabulary;
		int N = spamList.size()+hamList.size();
		for(int i=0;i<2;i++){
			Hashtable<String,Double> wordCount = new Hashtable<String,Double>();
			Hashtable<String,Double> wordCondProb = new Hashtable<String,Double>();
			int Nc = countDocsinClass(i, spamList, hamList);
			double allTct =0;
			double prior = (double)Nc/(double)N;
			priorTable.put(i, prior);
			String textC = concatinateTextofAllDocsinClass(i, spamList, hamList);
			for(String key:vocab.keySet()){
				double countTct = countOccurence(key, textC);
				wordCount.put(key, countTct);
				allTct = allTct+countTct;
			}
			for(String key:wordCount.keySet()){
				double condProb = ((double)(wordCount.get(key)+1))/((double)(allTct+wordCount.size()));
				wordCondProb.put(key, condProb);
			}
			condProbTable.put(i, wordCondProb);
		}
	}
	/*
	 * This method apply the testing data on the learned NaiveBayes and classifies the
	 * given document as either spam or ham
	 */
	public ArrayList<Integer> applyMultinomialNB(File[] TestList,ReadFolder readFolder) throws Exception{
		Hashtable<String,Double> wordSpamCondProb = new Hashtable<String,Double>();
		Hashtable<String,Double> wordHamCondProb = new Hashtable<String,Double>();
		ArrayList<Integer> spamHamCount = new ArrayList<Integer>();
		wordSpamCondProb = condProbTable.get(0);
		wordHamCondProb = condProbTable.get(1);
		int spamCount =0;
		int hamCOunt =0;
		for(int i=0;i<TestList.length;i++){
			String str = readFolder.readFile(TestList[i]);
			ArrayList<String> W = new ArrayList<String>();
			Hashtable<Integer,Double> scoreTable = new Hashtable<Integer,Double>();
			StringTokenizer st = new StringTokenizer(str,",");
			while(st.hasMoreElements()){
				String presentString = (String)st.nextElement();
				if(vocab.containsKey(presentString)){
					W.add(presentString);
				}
			}
			for(int j=0;j<2;j++){
				double prior = priorTable.get(j);
				double score = Math.log(prior);
				for(int k=0;k<W.size();k++){
					String currentStrig = W.get(k);
					double condProb = 0;
					if(j==0){
						condProb = wordSpamCondProb.get(currentStrig);
					}else{
						condProb = wordHamCondProb.get(currentStrig);
					}
					score = score+(Math.log(condProb));

				}
				scoreTable.put(j, score);
			}
			double spamScore = scoreTable.get(0);
			double hamScore = scoreTable.get(1);
			if(spamScore>hamScore){
				spamCount++;
			}else{
				hamCOunt++;
			}
		}
		spamHamCount.add(spamCount);
		spamHamCount.add(hamCOunt);
		return spamHamCount;
	}
	/*
	 * This method calculate the overall accuracy of the NaiveBayes for the given test data
	 */
	public double accuracy(File[] spamTestList, File[] hamTestList,ReadFolder readFolder) throws Exception{

		ArrayList<Integer> spamCount = applyMultinomialNB(spamTestList, readFolder);
		ArrayList<Integer> hamCount = applyMultinomialNB(hamTestList, readFolder);
		double spamAccuracy = (((double)spamCount.get(0))/((double)spamTestList.length))*100;
		//System.out.println("Printing the Spam Accuracy:"+ spamAccuracy);
		double hamAccuracy = (((double)hamCount.get(1))/((double)hamTestList.length))*100;
		//System.out.println("Printing the Ham Accuracy:"+ hamAccuracy);
		double finalAccuracy = (spamAccuracy + hamAccuracy)/2;
		System.out.println("Printing the Final Accuracy:"+ finalAccuracy);
		return finalAccuracy;
	}
	/*
	 * This method counts the given word occurred in a particular document
	 */
	public int countOccurence(String searchString, String fullString){
		int counter = 0;
		if(searchString.length()>1){
			Pattern pattern = Pattern.compile(searchString);
			Matcher matcher = pattern.matcher(fullString);
			while (matcher.find())
				counter++;
		}
		return counter;
	}
	/*
	 * This method returns the numbers of documents having class value as 0 or 1
	 */
	public int countDocsinClass(int i,ArrayList<String> spamList, ArrayList<String> hamList){
		int Nc = 0;
		if(i==0){
			Nc = spamList.size();
		}else if(i==1){
			Nc = hamList.size();
		}else{
			//TODO
		}
		return Nc;
	}
	/*
	 * This method concatenate all the documents words in a list and returns one big string
	 */
	public String concatinateTextofAllDocsinClass(int i,ArrayList<String> spamList, ArrayList<String> hamList){
		String textC = "";
		if(i==0){
			for(int j=0;j<spamList.size();j++){
				String s = "";
				String str = spamList.get(j);
				StringTokenizer st = new StringTokenizer(str,",");
				while(st.hasMoreElements()){
					s = s+st.nextElement();
				}
				textC = textC+s;
			}
		}else if(i==1){
			for(int j=0;j<hamList.size();j++){
				String s = "";
				String str = hamList.get(j);
				StringTokenizer st = new StringTokenizer(str,",");
				while(st.hasMoreElements()){
					s = s+st.nextElement();
				}
				textC = textC+s;
			}
		}else{
			//TODO
		}

		return textC;
	}
	
}

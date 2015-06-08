import java.io.File;
import java.util.Hashtable;


public class Perceptron {
	Hashtable<String,Double> vocabulary = new Hashtable<String, Double>();
	Hashtable<String,Hashtable<String,Integer>> wordCount = new Hashtable<String,Hashtable<String,Integer>>();
	//Hashtable<String,Double> fileSummation = new Hashtable<String, Double>();

	public Perceptron(){

	}
	public Perceptron(Hashtable<String,Double> vocab, 
			Hashtable<String,Hashtable<String,Integer>> wrdCount){
		this.vocabulary = vocab;
		this.wordCount = wrdCount;
	}

	public void learnPerceptron(File[] spam, File[] ham, int loopCount, double learningRate){
		//double learningRate = 0.1;
		for(int j =0;j<loopCount;j++){
			File[] files;
			int fileArrayCount = 0;
			/*
			 * deltaW = learningRate*(t-o)*Xi
			 * t = target value(Spam = 1; ham = 0;)
			 * Summation of Wi*Xi >0 = o value 1 else -1
			 * Xi wordcount
			 */
			while(fileArrayCount<2){
				int t=0;
				int O =0;
				if(fileArrayCount == 0) {
					files = spam;
				}
				else files = ham;
				for(int i=0;i<files.length;i++){
					boolean update = true;
					String fileName = files[i].getName();
					if(fileName.contains("spam")) t=1;
					else t=0;
					Hashtable<String,Integer> wordCountTable = new Hashtable<String, Integer>();
					wordCountTable = wordCount.get(fileName);
					double sum = 0.0;
					sum = sumofWiXi(wordCountTable);
					if(sum>0){
						O = 1;
						if(t ==1){
							update = false;
						}else{
							update = true;
						}
					}
					else {
						O =-1;
						if(t==0){
							update = false;
						}else{
							update = true;
						}
					}
					if(update == true){
						for(String key:vocabulary.keySet()){
							double W = vocabulary.get(key);
							double deltaW = 0.0;
							if(wordCountTable.containsKey(key)){
								deltaW = learningRate*(t-O)*wordCountTable.get(key);
								W = W+deltaW;
							}
							vocabulary.put(key, W);
						}
					}
				}
				fileArrayCount++;
			}
		}
	}
	public int[] testPerceptron(File[] fileArray,Hashtable<String,Hashtable<String,Integer>> wordCountTestTable){
		int spamCount =0;
		int hamCount =0;
		int[] count = new int[2];
		for(int i=0;i<fileArray.length;i++){
			String filename = fileArray[i].getName();
			Hashtable<String,Integer> wordCountTable = new Hashtable<String, Integer>();
			wordCountTable = wordCountTestTable.get(filename);
			double sum = sumofWiXi(wordCountTable);
			if(sum>0){
				spamCount++;
			}else{
				hamCount++;
			}
		}
		count[0]= spamCount;
		count[1]= hamCount;
		return count;
	}

	public double accuracy(File[] spam, File[] ham,Hashtable<String,Hashtable<String,Integer>> wordCountTestTable) throws Exception{

		int[] spamCount = testPerceptron(spam,wordCountTestTable);
		int[] hamCount = testPerceptron(ham,wordCountTestTable);
		double spamAccuracy = (((double)spamCount[0])/((double)spam.length))*100;
		//System.out.println("Printing the LR Spam Accuracy:"+ spamAccuracy);
		double hamAccuracy = (((double)hamCount[1])/((double)ham.length))*100;
		//System.out.println("Printing the LR Ham Accuracy:"+ hamAccuracy);
		double finalAccuracy = (spamAccuracy + hamAccuracy)/2;
		System.out.println("Printing the Perceptron Final Accuracy:"+ finalAccuracy);
		return finalAccuracy;
	}
	public double sumofWiXi(Hashtable<String,Integer> wordCountTable){
		double sum = 1.0;
		for(String key: wordCountTable.keySet()){
			if(vocabulary.containsKey(key)){
				double weight = vocabulary.get(key);
				int wordCount = wordCountTable.get(key);
				sum = sum +(weight*wordCount);
			}
		}
		return sum;
	}
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;


public class MainClass {
	public static Hashtable<String, Double> vocab = new Hashtable<String, Double>();
	public static Hashtable<String,Hashtable<String,Integer>> wordCountTrainTable = new Hashtable<String, Hashtable<String,Integer>>();
	public static Hashtable<String,Hashtable<String,Integer>> wordCountTestTable = new Hashtable<String, Hashtable<String,Integer>>();



	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		File[] spamTrainFiles = readFolder(args[0]);
		File[] hamTrainFiles = readFolder(args[1]);
		Hashtable<String,Integer> stopWords = new Hashtable<String,Integer>();
		
		int loopCount = Integer.parseInt(args[4]);
		double learningRate = Double.parseDouble(args[5]);

		try {
			stopWords = readStopwordsFile(args[6]);
			String stopword = args[7];
			boolean removestpword = false;
			if(stopword.equalsIgnoreCase("yes")){
				removestpword = true;
			}
			vocabBuilder(spamTrainFiles,hamTrainFiles,true,stopWords,removestpword);
			Perceptron perceptron = new Perceptron(vocab,wordCountTrainTable);
			perceptron.learnPerceptron(spamTrainFiles, hamTrainFiles,loopCount,learningRate);
			File[] spamTestFiles = readFolder(args[2]);
			File[] hamTestFiles = readFolder(args[3]);
			vocabBuilder(spamTestFiles, hamTestFiles, false,stopWords,removestpword);
			perceptron.accuracy(spamTestFiles, hamTestFiles,wordCountTestTable);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Time taken : "+totalTime);
			//System.out.println("Done");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static File[] readFolder(String Path){
		File Folder = new File(Path);
		File[] listofFiles = Folder.listFiles();
		return listofFiles;
	}


	public static void vocabBuilder(File[] spam,File[]ham,boolean train,Hashtable<String,Integer> stopWords,boolean removeStopWord) throws Exception{

		int size =0;
		if(spam.length > ham.length) size =spam.length;
		else size = ham.length;
		File[] files = new File[size];
		int fileArray = 0;
		while(fileArray<2){
			if(fileArray == 0) files = spam;
			else files = ham;
			for(int i=0;i<files.length;i++){
				Hashtable<String,Integer> wordsTab = new Hashtable<String,Integer>();
				wordsTab = readFile(files[i],train,stopWords,removeStopWord);
				if(train == true) wordCountTrainTable.put(files[i].getName(), wordsTab);
				else wordCountTestTable.put(files[i].getName(), wordsTab);
			}
			fileArray++;
		}

	}


	public static Hashtable<String,Integer> readFile(File file,boolean train,Hashtable<String,Integer> stopWords,boolean removeStopWords) throws Exception{
		FileReader fileReader = new FileReader(file);
		BufferedReader readText = new BufferedReader(fileReader);
		Hashtable<String,Integer> wordsTable = new Hashtable<String,Integer>();
		while(true){
			String str = readText.readLine();
			if(str==null){
				break;
			}
			str = str.replaceAll("[^a-zA-Z]", " ");
			str = str.replaceAll("\\s+",",");
			str = str.substring(1, str.length());
			StringTokenizer st = new StringTokenizer(str,",");
			while(st.hasMoreElements()){
				String word = st.nextToken();
				if(wordsTable.containsKey(word)){
					int count = wordsTable.get(word);
					count = count+1;
					wordsTable.put(word, count);
				}else{
					wordsTable.put(word, 1);
					if(removeStopWords ==true){
						if(train ==true && !stopWords.containsKey(word)){
							Random rand = new Random();
							vocab.put(word,(0.5-rand.nextDouble()));
						}
					}else{
						if(train == true){
							Random rand = new Random();
							vocab.put(word,(0.5-rand.nextDouble()));
						}
					}
				}
			}
		}
		readText.close();
		return wordsTable;
	}
	
	/*
	 * This method reads the stop words file and makes table of stop words
	 */
	public static Hashtable<String,Integer> readStopwordsFile(String path) throws Exception{

		FileReader fileReader = new FileReader(path);
		BufferedReader readText = new BufferedReader(fileReader);
		Hashtable<String,Integer> stopWordList = new Hashtable<String,Integer>();
		while(true){
			String str = readText.readLine();
			if(str==null){
				break;
			}
			str = str.replaceAll("[^a-zA-Z]", "");
			stopWordList.put(str,0);
		}
		readText.close();
		//System.out.println("printing the one Big string :"+oneBigString);
		return stopWordList;
	}
	
}

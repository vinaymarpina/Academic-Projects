import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

/*
 * This class used to read the files in a folder and as well as each file
 */
public class ReadFolder {
	/*
	 * This method reads the files in a folder and returns list of files
	 * 
	 * takes input as path for the particular files folder
	 */
	public File[] getAllFiles(String s){
		File folder = new File(s);
		File[] fileList = folder.listFiles();
		return fileList;
	}
	/*
	 * This method reads the given file and removes all the special characters
	 * and spaces, it forms one big string and return it as string
	 */
	public String readFile(File file) throws Exception{

		FileReader fileReader = new FileReader(file);
		BufferedReader readText = new BufferedReader(fileReader);
		String oneBigString = "";
		while(true){
			String str = readText.readLine();
			if(str==null){
				break;
			}
			str = str.replaceAll("[^a-zA-Z]", " ");
			oneBigString = oneBigString+" "+str;
		}
		oneBigString = oneBigString.replaceAll("\\s+",",");
		oneBigString = oneBigString.substring(1, oneBigString.length());
		//System.out.println("printing the one Big string :"+oneBigString);
		return oneBigString;
	}
	/*
	 * This method reads the stop words file and makes table of stop words
	 */
	public Hashtable<String,Integer> readStopwordsFile(String path) throws Exception{

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
		
		//System.out.println("printing the one Big string :"+oneBigString);
		return stopWordList;
	}
	
	
}

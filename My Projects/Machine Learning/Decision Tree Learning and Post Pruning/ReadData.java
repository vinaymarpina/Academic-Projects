

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
/*
 * This class Reads the Data file and returns the data in List of Lists
 */

public class ReadData {

	private String filePath;

	public ReadData(String path){
		filePath = path;
	}

	public ArrayList<ArrayList> readFile() throws Exception{

		FileReader fileReader = new FileReader(filePath);
		BufferedReader readText = new BufferedReader(fileReader);
		ArrayList<ArrayList> tariningSetList = new ArrayList<ArrayList>();
		while(true){
			String str = readText.readLine();
			if(str==null){
				break;
			}
			//System.out.println("printing the string line"+str);
			StringTokenizer st = new StringTokenizer(str,",");
			ArrayList attributeData = new ArrayList();
			while(st.hasMoreElements()){
				attributeData.add(st.nextElement());
			}

			tariningSetList.add(attributeData);
		}
		return tariningSetList;
	}
}

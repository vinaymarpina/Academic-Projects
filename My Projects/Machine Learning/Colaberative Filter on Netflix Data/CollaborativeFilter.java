import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class CollaborativeFilter {

	public static void main (String[] args){
		
		System.out.println("Colaborative Filter");
		Map<Integer,Map<Integer,Double>> ratingTrainData = new HashMap<Integer,Map<Integer,Double>>();
		List<String> testDataList = new ArrayList<String>();
		try {
			ratingTrainData = readData(args[0],true);
			testDataList = readTestData(args[1]);
			long startTime = System.currentTimeMillis();
			prediction(ratingTrainData, testDataList);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Time taken : "+totalTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void prediction(Map<Integer,Map<Integer,Double>> ratingTrainData,
			List<String> testDataList){
		double errorSum = 0.0;
		double errorSqureSum = 0.0;
		double MAE =0.0;
		double RMSE = 0.0;
		int count=0;
		for(int i=0;i<testDataList.size();i++){
			count++;
			String str = testDataList.get(i);
			StringTokenizer st = new StringTokenizer(str,",");
			int J = Integer.parseInt(st.nextToken());
			int A = Integer.parseInt(st.nextToken());
			double rating= Double.parseDouble(st.nextToken());
			Map<Integer,Double> userATrainData = ratingTrainData.get(A);
				double Va = userATrainData.get(0);
				double predict = 0.0;
				double sum =0.0;
				double K = 0.0;
				double Wai = 0.0;
				//long startTime = System.currentTimeMillis();
				for(Integer I:ratingTrainData.keySet()){
						Map<Integer,Double> userITrainData = ratingTrainData.get(I);
						Wai = correlationDataInside(ratingTrainData, A, I);
						K = K+Wai;
						if(userITrainData.get(J)!= null ){
							sum = sum+((Wai)*(userITrainData.get(J)-userITrainData.get(0)));
						}else{
							//TODO
						}
				}
				//long endTime   = System.currentTimeMillis();
				//long totalTime = endTime - startTime;
				//System.out.println("Time taken : "+totalTime);
				double additionTerm = 0.0;
				if(K!=0){
					additionTerm = (sum/K);
				}
				predict =Va+additionTerm;
				if(count%1000 ==0)
				System.out.println("User ID: "+A+", Prediction Rating for "+ count+" Input Line IS : "+predict);
				errorSum = errorSum+Math.abs(predict-rating);
				errorSqureSum = errorSqureSum+((predict-rating)*(predict-rating));		
		}
		MAE = errorSum/testDataList.size();
		RMSE = Math.sqrt(errorSqureSum/testDataList.size());
		System.out.println("MeanAbsolute Error: "+MAE);
		System.out.println("Root Mean Sqaured Error: "+RMSE);
		
	}
	public static double correlationDataInside(Map<Integer,Map<Integer,Double>> ratingTrainData,Integer A,Integer I){
		Map<Integer,Double> userATrainData = ratingTrainData.get(A); 
		Map<Integer,Double> userITrainData = ratingTrainData.get(I);
		double W =0.0;
		double numeratorSum = 0.0;
		double denominatorASum = 0.0;
		double denominatorISum = 0.0;
		double Vaj = 0;double Va = 0;double Vij =0;double Vi =0;
		Va = userATrainData.get(0);
		Vi = userITrainData.get(0);
		for(Integer J:userATrainData.keySet()){
			if(userITrainData.get(J)!= null && J !=0){
				Vaj = userATrainData.get(J);
				Vij = userITrainData.get(J);
				numeratorSum = numeratorSum +((Vaj-Va)*(Vij-Vi));
				denominatorASum = denominatorASum+((Vaj-Va)*(Vaj-Va));
				denominatorISum = denominatorISum+((Vij-Vi)*(Vij-Vi));
			}
		}
		if(denominatorASum !=0 && denominatorISum !=0 && numeratorSum !=0 ){
			W = numeratorSum/Math.sqrt((denominatorASum*denominatorISum));
		}
		return W;
	}

	public static List<String> readTestData(String path) throws Exception{
		FileReader fileReader = new FileReader(path);
		BufferedReader readText = new BufferedReader(fileReader);
		List<String> testDataList = new ArrayList<String>();
		while(true){
			String str = readText.readLine();
			if(str == null){
				break;
			}
			testDataList.add(str);
		}
		readText.close();
		return testDataList;
	}
	public static Map<Integer,Map<Integer,Double>> readData(String path,boolean training) throws Exception{
		FileReader fileReader = new FileReader(path);
		BufferedReader readText = new BufferedReader(fileReader);
		Map<Integer,Map<Integer,Double>> ratingsTable = new HashMap<Integer,Map<Integer,Double>>();

		while(true){
			String str = readText.readLine();
			if(str == null){
				break;
			}
			StringTokenizer st = new StringTokenizer(str,",");

			int movieId = Integer.parseInt(st.nextToken());
			int userId = Integer.parseInt(st.nextToken());
			double rating= Double.parseDouble(st.nextToken());

			if(ratingsTable.get(userId) != null){
				Map<Integer,Double> ratingTab = ratingsTable.get(userId);
				if(ratingTab.get(movieId)==null){
					if(training ==true){
						double averageVoting = ratingTab.get(0);
						averageVoting = averageVoting * ((double)(ratingTab.size()-1));
						averageVoting = (averageVoting+rating)/((double)(ratingTab.size()));
						//System.out.println("User ID: "+userId+", AverageVoting : "+averageVoting+", Rating : "+rating);
						ratingTab.put(0, averageVoting);
						
					}
					ratingTab.put(movieId, rating);
					ratingsTable.put(userId,ratingTab);
				}else {
					//TODO
				}
			}else{
				Map<Integer,Double> ratingTab = new HashMap<Integer,Double>();
				if(training == true){
					ratingTab.put(0,rating);
				}
				ratingTab.put(movieId,rating);
				ratingsTable.put(userId,ratingTab);
			}

		}
		readText.close();
		return ratingsTable;
	}
}

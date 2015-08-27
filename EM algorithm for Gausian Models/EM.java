import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


public class EM {
	private static double[] mean ;
	private static double[] covariance ;
	private static double[] mixingCoeff;
	private static ArrayList<Double> dataList;
	public static void main(String[] args){
		try {
			dataList = readData(args[0]);
			int K = Integer.parseInt(args[1]);
			String constantVariance = args[2];
			mean = new double[K];
			covariance = new double[K];
			mixingCoeff = new double[K];
			Random rand = new Random();
			
			//Initialization of Mean , CoVariance, Mixing Coefficient
			
				for(int i=0;i<K;i++){
					mean[i]=dataList.get(rand.nextInt(dataList.size()));
					if(constantVariance.equalsIgnoreCase("no")){
						double sum =0.0;
						for(int j=0;j<dataList.size();j++){
							sum= sum+((dataList.get(j)-mean[i])*(dataList.get(j)-mean[i]));
						}
						covariance[i] = (rand.nextInt(5)+1)*(sum/(dataList.size()-1));
					}else if (constantVariance.equalsIgnoreCase("yes")){
						covariance[i]=1.0;
					}else{
						//TODO
					}
					mixingCoeff[i]=(double)(1.0/K);
				}
			
			//Initial Log Likelihood Calculation
			System.out.println("Initial Log Liklehood: "+logLikelihood());
			double initialLogliklihood = 0.0;;
			double newLogliklihood = 1.0;
			int count = 0;
			//Untill Log likelihood difference reaches Threshold
			while(Math.abs(newLogliklihood-initialLogliklihood)> 0.001){
				count++;
				//To stop at 1000 iterations.
				if(count>1000){
					break;
				}
			//for(int i=0; i<loopcount;i++){
				//E-Step in the Algorithm
				initialLogliklihood = newLogliklihood;
				ArrayList<ArrayList<Double>> gamaZnKList = eStep();
				//M-Step in the Algorithm
				mStep(gamaZnKList,constantVariance);
				newLogliklihood = logLikelihood();
			//}
			}
			System.out.println("LoopCount Before Convergence: "+count);
			System.out.println("Loglikelihood after Convergence: "+(newLogliklihood));
			//Printing the values 
			System.out.println("Mean Values: ");
			for(int i=0;i<mean.length;i++){
				System.out.print(mean[i]+", ");
			}
			System.out.println("");
			System.out.println("------------------------------------------------- ");
			System.out.println("Covariance Values: ");
			for(int i=0;i<covariance.length;i++){
				System.out.print(covariance[i]+", ");
			}
			System.out.println("");
			System.out.println("------------------------------------------------- ");
			System.out.println("Mixxing Coefficient Values: ");
			for(int i=0;i<mixingCoeff.length;i++){	
				System.out.print(mixingCoeff[i]+", ");
			}
			System.out.println("");
			System.out.println("------------------------------------------------- ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void mStep(ArrayList<ArrayList<Double>> gamaZnKList,String constantVariance){
		double[] nKArray = new double[mean.length];

		for(int j=0;j<mean.length;j++){
			double Nk = 0.0;
			for(int i=0;i<dataList.size();i++){
				Nk = Nk+gamaZnKList.get(i).get(j);
			}
			nKArray[j] = Nk;
		}

		//Update Mean, CoVariance , Mixing Coefficient

		for(int j=0;j<mean.length;j++){
			double newmean = 0.0;
			double newcovariance = 0.0;
			double newmixCoeff = 0.0;
			for(int i=0;i<dataList.size();i++){
				newmean = newmean+(gamaZnKList.get(i).get(j)* dataList.get(i));
			}
			newmean = newmean/nKArray[j];
			if(!Double.isNaN(newmean)){
			mean[j]= newmean;
			}
			for(int i=0;i<dataList.size();i++){
				newcovariance = newcovariance+(gamaZnKList.get(i).get(j)* (dataList.get(i)-mean[j])*(dataList.get(i)-mean[j]));
			}
			newcovariance = newcovariance/nKArray[j];
			if(!Double.isNaN(newcovariance) && newcovariance != 0.0){
				if(constantVariance.equalsIgnoreCase("no")){
					covariance[j] = newcovariance;
				}else{
					//TODO
				}
			}
			newmixCoeff = nKArray[j]/dataList.size();
			if(!Double.isNaN(newmixCoeff)){
				mixingCoeff[j] = newmixCoeff;
			}
		}


	}
	public static ArrayList<ArrayList<Double>> eStep(){
		ArrayList<ArrayList<Double>> gamaZnKList = new ArrayList<ArrayList<Double>>();

		for(int i=0;i<dataList.size();i++){
			ArrayList<Double> gamaZKList = new ArrayList<Double>();
			double numerator = 0.0;
			double denominator = 0.0;
			for(int j=0;j<mean.length;j++){
				numerator = mixingCoeff[j]*(Math.pow(2*Math.PI*covariance[j] ,-0.5)*Math.exp(-(Math.pow(dataList.get(i)-mean[j], 2))/(2*covariance[j])) );
				denominator = denominator+numerator;
			}
			for(int j=0;j<mean.length;j++){
				double top = mixingCoeff[j]*(Math.pow(2*Math.PI*covariance[j] ,-0.5)*Math.exp(-(Math.pow(dataList.get(i)-mean[j], 2))/(2*covariance[j])) );

				gamaZKList.add(top/denominator);

			}
			gamaZnKList.add(gamaZKList);
		}
		return gamaZnKList;
	}
	public static double logLikelihood(){
		double logliklihood = 0.0;

		for(int i=0; i<dataList.size();i++){
			double pofK = 0.0;
			for(int j=0;j<mean.length;j++){
				if(covariance[j]!=0.0)
					pofK = pofK+mixingCoeff[j]*(Math.pow(2*Math.PI*covariance[j] ,-0.5)*Math.exp(-(Math.pow(dataList.get(i)-mean[j], 2))/(2*covariance[j])) );
			}
			if(pofK!=0.0){
				logliklihood = logliklihood+Math.log(pofK);
			}
		}
		return logliklihood;

	}
	public static ArrayList<Double> readData(String filename) throws Exception{
		FileReader fileReader = new FileReader(filename);
		BufferedReader readText = new BufferedReader(fileReader);
		ArrayList<Double> data = new ArrayList<Double>();
		while(true){
			String str = readText.readLine();
			if(str == null){
				break;
			}
			data.add(Double.parseDouble(str));
		}
		readText.close();
		return data;

	}
}

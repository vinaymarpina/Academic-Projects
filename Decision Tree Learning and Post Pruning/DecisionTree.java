

import java.util.ArrayList;
import java.util.HashMap;

public class DecisionTree {
	public static void main(String[] args){
		int L = Integer.parseInt(args[0]);
		int K = Integer.parseInt(args[1]);
		String print = args[5];
		/*
		 * <L> <K> <training-set> <validation-set> <test-set> <to-print>
			L: integer (used in the post-pruning algorithm)
			K: integer (used in the post-pruning algorithm)
			to-print:{yes,no}
		 */
		ReadData readTrainingData = new ReadData(args[2]);
		ReadData readTestData = new ReadData(args[4]);
		ReadData validationData = new ReadData(args[3]);
		ArrayList<ArrayList> trainingsetData = new ArrayList<ArrayList>();
		ArrayList<ArrayList> testDataSet = new ArrayList<ArrayList>();
		ArrayList<ArrayList> validationDataSet = new ArrayList<ArrayList>();
		HashMap<String,Double> attributeGain = new HashMap<String,Double>();
		ArrayList headersList = new ArrayList();
		
		try {
			trainingsetData = readTrainingData.readFile();
			testDataSet = readTestData.readFile();
			validationDataSet = validationData.readFile();
			headersList = trainingsetData.get(0);
			GrowTree growTree = new GrowTree(trainingsetData);
			growTree.fillTheMap(headersList);
			//Give HeuristicNumber Along with Data List
			Node rootNode1 = growTree.growTree(trainingsetData, 1);
			Node rootNode2 = growTree.growTree(trainingsetData, 2);

			if(print.equalsIgnoreCase("yes")){
				System.out.println("Information Gain Tree Grow: ");
				System.out.println("**************************************************** ");
				growTree.printTree(rootNode1);
				System.out.println("");
				System.out.println("**************************************************** ");
				System.out.println("Variance Impurity Tree Grow: ");
				growTree.printTree(rootNode2);
				System.out.println("");
			}else{
				//TODO
			}
			Efficiency efficiency = new Efficiency(testDataSet);
			double igAccuracy = (efficiency.efficiencyCalc(rootNode1))*100;
			double viAccuracy = (efficiency.efficiencyCalc(rootNode2))*100;
			System.out.println("****************************************************");
			System.out.println("Information Gain Accuracy : "+igAccuracy);
			System.out.println("Variance Impurity Accuracy : "+viAccuracy);

			Node pruneNode1 = growTree.postPruning(L, K, rootNode1, 1, validationDataSet);
			Node pruneNode2 = growTree.postPruning(L, K, rootNode2, 2, validationDataSet);
			
			//Accuracy Using Test Data
			double igPruneAccuracy = (efficiency.efficiencyCalc(pruneNode1))*100;
			double viPruneAccuracy = (efficiency.efficiencyCalc(pruneNode2))*100;
			System.out.println("****************************************************");
			System.out.println("Information Gain Prune Accuracy : "+igPruneAccuracy);
			System.out.println("Variance Impurity Prune Accuracy : "+viPruneAccuracy);
			System.out.println("****************************************************");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

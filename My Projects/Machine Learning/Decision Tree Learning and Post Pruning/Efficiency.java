

import java.util.ArrayList;

/*
 * This method calculates the Accuracy of the testSet for the Decision tree 
 */
public class Efficiency {

	ArrayList<ArrayList> testList = new ArrayList<ArrayList>();

	public Efficiency(ArrayList<ArrayList> tList){
		testList = tList;
	}
	public double efficiencyCalc(Node rootNode){
		int accuracyCount =0;
		for(int i=1;i<testList.size();i++){
			ArrayList testValues = new ArrayList();
			testValues = testList.get(i);
			Node node = rootNode;
			while(node.getLeftChildren()!=null && node.getRightChildren()!=null){
				int index = node.getIndex();
				int attributeValue = Integer.parseInt((String)testValues.get(index));
				if(attributeValue ==0){
					node = node.getLeftChildren();
				}else{
					node = node.getRightChildren();
				}
			}
			if(node.getFinalDecession()== Integer.parseInt((String) testValues.get(testValues.size()-1))){
				accuracyCount++;
			}else{
				//TODO
			}

		}
		double accuracy = ((double)accuracyCount)/((double)(testList.size()-1));
		return accuracy;
	}
}

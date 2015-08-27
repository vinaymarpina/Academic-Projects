


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;



public class GrowTree {
	int depth=0;
	HashMap<String,Integer> usedNodes = new HashMap<String,Integer>();
	ArrayList nodeList = new ArrayList();
	ArrayList nodeSet = new ArrayList();
	ArrayList<ArrayList> attributeList = new ArrayList<ArrayList>();
	public GrowTree(ArrayList<ArrayList> aList){
		attributeList = aList;
	}
	/*
	 * This method take input Parameters as Training Data in list of List format and 
	 * Heuristic number.
	 * it Returns a rootNode Object which contains all the children of the tree
	 * 
	 * This Algorithm is provided in the Class Slides as Grow Tree Algorithm
	 */
	public Node growTree(ArrayList<ArrayList> aList,int heuristicNumber){
		int positiveCount = 0;
		int negativeCount = 0;
		for(int i=0;i<aList.size();i++){
			ArrayList attributeValues = new ArrayList();
			attributeValues = aList.get(i);
			if(i==0){
				//TODO
			}else{
				if(Integer.parseInt((String) attributeValues.get(attributeValues.size()-1))== 1){
					positiveCount++;
				}else{
					negativeCount++;
				}
			}

		}
		if(negativeCount == aList.size()-1){
			return new Node(0);
		}else if(aList.isEmpty() || positiveCount == aList.size()-1){
			return new Node(1);
		}else{
			HashMap<String,Double> attributeGain = new HashMap<String,Double>();
			attributeGain = gain(aList,heuristicNumber);
			String maxNode = null;
			maxNode = bestAttribute(attributeGain);
			if(usedNodes.get(maxNode)==0){
				usedNodes.put(maxNode, 1);
			}else{
				return new Node(1);
			}
			ArrayList attributeList = new ArrayList();
			attributeList = aList.get(0);
			int maxNodeIndex = attributeList.indexOf(maxNode);

			ArrayList<ArrayList> negativeSubSet = new ArrayList<ArrayList>();
			ArrayList<ArrayList> positiveSubSet = new ArrayList<ArrayList>();
			for(int j=0;j<aList.size();j++){
				ArrayList attributeValues = new ArrayList();
				attributeValues = aList.get(j);
				if(j==0){
					positiveSubSet.add(attributeValues);
					negativeSubSet.add(attributeValues);
				}else{
					if(Integer.parseInt((String) attributeValues.get(maxNodeIndex))== 1){
						positiveSubSet.add(attributeValues);
					}else{
						negativeSubSet.add(attributeValues);
					}
				}

			}
			Node returnNode = new Node(maxNode,growTree(negativeSubSet,heuristicNumber),growTree(positiveSubSet,heuristicNumber));
			returnNode.setIndex(maxNodeIndex);
			returnNode.setPositiveCount(positiveCount);
			returnNode.setNegativeCount(negativeCount);
			usedNodes.put(maxNode, 0);
			return returnNode ;
		}
	}
	/*
	 * This method to Print the Forward Slash in Tree Printing
	 */
	public String printSlash(int number){
		String bigSlash ="";
		for(int i=1;i<=number;i++){
			bigSlash = bigSlash+"|";
		}
		return bigSlash;
	}
	/*
	 * This method to print the Tree with the given rootNode Object
	 * in the required format
	 */
	public void printTree(Node node){
		String LS = System.getProperty("line.separator");
		if(node.getLeftChildren()!= null && node.getRightChildren()!= null){
			System.out.print(LS);
			System.out.print(printSlash(depth)+""+node.getAttributeName()+" = 0 : ");
			depth++;
			printTree(node.getLeftChildren());
			depth--;
			System.out.print(LS);
			System.out.print(printSlash(depth)+""+node.getAttributeName()+" = 1 : ");
			depth++;
			printTree(node.getRightChildren());
			depth--;
		}else{
			System.out.print(node.getFinalDecession());
			return;
		}	 
	}
	/*
	 * This Method is to find the best Attribute based on the Gain Values.
	 */
	public String bestAttribute(HashMap<String,Double> attrGainMap){
		Entry<String,Double> maxEntry = null;

		for(Entry<String,Double> entry : attrGainMap.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}
		String maxNode = maxEntry.getKey();
		return maxNode;
	}
	/*
	 * This method is to Calculate the Entropy of the Training Set for both Heuristics
	 */
	public double entropy(ArrayList<ArrayList> attrSubList,int heuristicNumber){

		//Calculate Entropy
		double positiveCount = 0;
		double negativeCount = 0;
		double entropy =0;
		if(attrSubList.size()==1 ||attrSubList ==null){
			// No subset
		}else{
			for(int i=0; i<attrSubList.size();i++){
				ArrayList attributeValues = new ArrayList();
				attributeValues = attrSubList.get(i);
				if(i==0){

				}else{
					if(Integer.parseInt((String) attributeValues.get(attributeValues.size()-1))== 1){
						positiveCount++;
					}else{
						negativeCount++;
					}
				}

			}
			double posMulti = (double)positiveCount/((double)(attrSubList.size())-1);
			double negMulti = (double)negativeCount/((double)(attrSubList.size())-1);
			if(posMulti == 0 || negMulti == 0){
				//Math.log do not allowZero values
			}else{
				if(heuristicNumber==1){
					// First Heuristic
					entropy = -(posMulti)*(Math.log(posMulti))-(negMulti)*(Math.log(negMulti));
				}else if(heuristicNumber ==2){
					//Second Heuristic given in the HW
					entropy = (positiveCount * negativeCount)/(((double)(attrSubList.size())-1)*((double)(attrSubList.size())-1));
				}

			}

		}
		//System.out.println("Printing the Entropy Calculated:"+entropy);
		return entropy;
	}

	/*
	 * This method is to calculate the Gain for both the Heauristics
	 */
	public HashMap gain(ArrayList<ArrayList> attrList,int heuristicNumber){
		HashMap<String,Double> attributeGain = new HashMap<String,Double>();
		ArrayList attributes = new ArrayList();
		attributes = attrList.get(0);
		for(int j=0; j<attributes.size()-1;j++){
			ArrayList<ArrayList> attributeNegativeSubList = new ArrayList<ArrayList>();
			ArrayList<ArrayList> attributePositiveSubList = new ArrayList<ArrayList>();
			for(int i=0; i<attrList.size();i++){
				ArrayList attributeValues = new ArrayList();
				attributeValues = attrList.get(i);
				if(i==0){
					attributeNegativeSubList.add(attributeValues);
					attributePositiveSubList.add(attributeValues);
				}else{
					if(Integer.parseInt((String) attributeValues.get(j))==0){
						attributeNegativeSubList.add(attributeValues);
					}else{
						attributePositiveSubList.add(attributeValues);
					}
				}

			}
			double totalEntropy = entropy(attrList,heuristicNumber);
			double negSubSetEntropy = entropy(attributeNegativeSubList,heuristicNumber);
			double posSubSetEntropy = entropy(attributePositiveSubList,heuristicNumber);

			double negMultiplication = (((double)attributeNegativeSubList.size())-1)/(((double)attrList.size())-1);
			double posMultiplication = (((double)attributePositiveSubList.size())-1)/(((double)attrList.size())-1);

			double gain = totalEntropy - (negMultiplication * negSubSetEntropy) -(posMultiplication * posSubSetEntropy);
			//System.out.println("Printing the Gain Calculated :"+gain);
			attributeGain.put((String)attributes.get(j),gain);

		}
		return attributeGain;
	}
	public void fillTheMap(ArrayList headersList){
		for(int i=0;i<headersList.size();i++){
			usedNodes.put((String) headersList.get(i), 0);
		}
	}
	/*
	 * This method is to prune the grown tree for given L and K
	 * Implemented using the Algorithm given in the Home Work
	 */
	public Node postPruning(int L,int K,Node rootNode,int hNumber,ArrayList<ArrayList> validationSet) throws Exception{

		Node bestNode = new Node();
		bestNode = rootNode;
		for(int i=1;i<=L;i++){
			Node rootTempNode =  cloneNode(rootNode);
			//printTree(rootTempNode);
			Random rand = new Random();
			int M = rand.nextInt(K);
			for(int j=1;j<=M;j++){
				ArrayList nodeList = new ArrayList();
				nodeList = preOrder(rootTempNode);
				int N = nodeList.size();
				int P = rand.nextInt(N);
				if(P==0 && N!=1){
					P=1;
				}

				this.nodeList.clear();
				this.nodeSet.clear();
				if( N==1){
					break;
				}
				rootTempNode = removeNode(rootTempNode, P+1);
				/*System.out.println("__________Printing the Random tree__________");
				printTree(rootTempNode);
				System.out.println("********************************");*/
			}

			Efficiency efficiency = new Efficiency(validationSet);
			double accuracyTemp = (efficiency.efficiencyCalc(rootTempNode))*100;
			double accuracyOriginal = (efficiency.efficiencyCalc(bestNode))*100;
			//System.out.println("Printing the Accuracy prune:"+accuracyTemp+": AccuracyOriginal:"+accuracyOriginal);
			if(accuracyOriginal<accuracyTemp){
				//System.out.println("Printing in side increase*****************");
				bestNode = rootTempNode;
			}
		}
		return bestNode;
	}
	/*
	 * This Method is to remove the random node selected by the Pruning algorithm conditions
	 * and add a leaf node based on the positive and negative count
	 */
	public Node removeNode(Node node,int randomNode){
		if(node != null){
			if(node.getLeftChildren() == null && node.getRightChildren() ==null)
				return node;
			nodeSet.add(node);
			if(nodeSet.size()==randomNode){
				if(node.getPositiveCount()>= node.getNegativeCount()){
					node.setAttributeName(null);
					node.setFinalDecession(1);
					node.setLeftChildren(null);
					node.setRightChildren(null);
					node.setPositiveCount(0);
					node.setNegativeCount(0);
				}else{
					node.setAttributeName(null);
					node.setFinalDecession(0);
					node.setLeftChildren(null);
					node.setRightChildren(null);
					node.setPositiveCount(0);
					node.setNegativeCount(0);
				}
			}

			removeNode(node.getLeftChildren(),randomNode);
			removeNode(node.getRightChildren(),randomNode);
			return node;
		}else{
			return new Node(1);
		}

	}
	/*
	 * This method orders the given tree implemented PreOrder
	 * returns nodes list with out leaf nodes
	 */
	public ArrayList preOrder(Node node){

		if(node.getLeftChildren() == null && node.getRightChildren() ==null)
			return nodeList;
		nodeList.add(node.getAttributeName());
		preOrder(node.getLeftChildren());
		preOrder(node.getRightChildren());
		return nodeList;
	}

	/*
	 * This method deep clones the Tree, Deep cloning is required
	 * to manipulate the clone tree.
	 */
	public Node cloneNode(Node node){
		Node clone = new Node();
		if(node.getLeftChildren()!= null && node.getRightChildren() != null){
			clone.setAttributeName(node.getAttributeName());
			clone.setFinalDecession(node.getFinalDecession());
			clone.setIndex(node.getIndex());
			clone.setPositiveCount(node.getPositiveCount());
			clone.setNegativeCount(node.getNegativeCount());
			clone.setLeftChildren(cloneNode(node.getLeftChildren()));
			clone.setRightChildren(cloneNode(node.getRightChildren()));
		}else{
			clone.setFinalDecession(node.getFinalDecession());
		}
		return clone;

	}


}

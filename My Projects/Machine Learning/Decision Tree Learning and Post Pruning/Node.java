
/*
 * This class contains the Tree Node and its getters and setters
 */
public class Node {
	private String attributeName;
	private int Index;
	private int positiveCount;
	private int negativeCount;
	private String attributeValue;
	private Node leftChildren;
	private Node rightChildren;
	private int finalDecession;

	public Node(){
		//Default Constructor
	}
	public Node(String attribute, Node left, Node right){
		this.attributeName = attribute;
		this.leftChildren = left;
		this.rightChildren = right;
	}
	public Node(int finalValue){
		this.finalDecession = finalValue;
	}
	public int getFinalDecession() {
		return finalDecession;
	}
	public int getIndex() {
		return Index;
	}
	public void setIndex(int index) {
		Index = index;
	}
	public void setFinalDecession(int finalDecession) {
		this.finalDecession = finalDecession;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public Node getLeftChildren() {
		return leftChildren;
	}
	public void setLeftChildren(Node leftChildren) {
		this.leftChildren = leftChildren;
	}
	public Node getRightChildren() {
		return rightChildren;
	}
	public void setRightChildren(Node rightChildren) {
		this.rightChildren = rightChildren;
	}
	public int getPositiveCount() {
		return positiveCount;
	}
	public void setPositiveCount(int positiveCount) {
		this.positiveCount = positiveCount;
	}
	public int getNegativeCount() {
		return negativeCount;
	}
	public void setNegativeCount(int negativeCount) {
		this.negativeCount = negativeCount;
	}

}
